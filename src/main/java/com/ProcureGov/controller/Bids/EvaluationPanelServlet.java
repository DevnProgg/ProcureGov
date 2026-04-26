package com.ProcureGov.controller.Bids;

import com.ProcureGov.dto.LeaderboardEntryDTO;
import com.ProcureGov.dto.UnevaluatedBidDTO;
import com.ProcureGov.model.*;
import com.ProcureGov.service.*;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/app/evaluations/panel")
public class EvaluationPanelServlet extends HttpServlet {

    private BidEvaluationService evaluationService;
    private TenderService tenderService;
    private BidService bidService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.evaluationService = new BidEvaluationService();
        this.tenderService = new TenderService();
        this.bidService = new BidService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        try {
            // Get current user
            Object user = session.getAttribute("user");
            int evaluatorId = getUserId(user);
            String userRole = getUserRole(user);

            // Check authorization
            if (!isAuthorizedEvaluator(userRole)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Only Procurement Officers and Evaluation Committee members can access this panel");
                return;
            }

            String tenderIdStr = req.getParameter("tenderId");

            // If tenderId is provided, show bids for that specific tender
            if (tenderIdStr != null && !tenderIdStr.isEmpty()) {
                handleTenderDetailView(req, resp, Integer.parseInt(tenderIdStr), evaluatorId, userRole);
            } else {
                // Show list of all relevant tenders
                handleTenderListView(req, resp, userRole);
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Error loading evaluation panel: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/modals/evaluation_panel_view.jsp").forward(req, resp);
        }
    }

    /**
     * Show list of tenders that are either UNDER_EVALUATION or CLOSED (ready for evaluation)
     */
    private void handleTenderListView(HttpServletRequest req, HttpServletResponse resp, String userRole)
            throws ServletException, IOException {

        // Get tenders that are ready for evaluation
        List<TenderOffer> evaluationTenders = getEvaluationTenders(userRole);

        // Separate into active evaluation and pending evaluation
        List<TenderOffer> underEvaluation = evaluationTenders.stream()
                .filter(t -> "UNDER_EVALUATION".equals(t.getStatus()))
                .collect(Collectors.toList());

        List<TenderOffer> closedPending = evaluationTenders.stream()
                .filter(t -> "CLOSED".equals(t.getStatus()) && !isAlreadyAwarded(t.getTender_id()))
                .collect(Collectors.toList());

        List<TenderOffer> evaluated = evaluationTenders.stream()
                .filter(t -> "EVALUATED".equals(t.getStatus()))
                .collect(Collectors.toList());

        req.setAttribute("underEvaluationTenders", underEvaluation);
        req.setAttribute("closedTenders", closedPending);
        req.setAttribute("evaluatedTenders", evaluated);
        req.setAttribute("userRole", userRole);
        req.setAttribute("viewMode", "tenderList");

        req.getRequestDispatcher("/WEB-INF/views/modals/evaluation_panel_view.jsp").forward(req, resp);
    }

    /**
     * Show detailed bid evaluation view for a specific tender
     */
    private void handleTenderDetailView(HttpServletRequest req, HttpServletResponse resp,
                                        int tenderId, int evaluatorId, String userRole)
            throws ServletException, IOException {

        // Get tender details
        TenderOffer tender = tenderService.getTenderById(tenderId);
        if (tender == null) {
            req.setAttribute("error", "Tender not found");
            handleTenderListView(req, resp, userRole);
            return;
        }

        // Check if tender is in a valid state for evaluation
        if (!isValidEvaluationStatus(tender.getStatus())) {
            req.setAttribute("error", "This tender is not available for evaluation");
            handleTenderListView(req, resp, userRole);
            return;
        }

        // Get all bids for this tender
        List<TenderBid> allBids = bidService.getBidsForTender(tenderId);

        // Get unevaluated bids for current user
        List<UnevaluatedBidDTO> unevaluatedBids = getUnevaluatedBids(allBids, evaluatorId, tenderId);

        // Get leaderboard (only if tender is EVALUATED)
        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        List<BidScoreSummary> allBidSummaries = new ArrayList<>();

        if ("EVALUATED".equals(tender.getStatus()) || "AWARDED".equals(tender.getStatus())) {
            leaderboard = getLeaderboard(allBids, tenderId);
            allBidSummaries = getAllBidSummaries(allBids, tenderId, evaluatorId);
        }

        // Get evaluation progress for this tender
        Map<String, Object> progress = getEvaluationProgress(tenderId, allBids, evaluatorId);

        // Set request attributes
        req.setAttribute("tender", tender);
        req.setAttribute("unevaluatedBids", unevaluatedBids);
        req.setAttribute("leaderboard", leaderboard);
        req.setAttribute("allBidSummaries", allBidSummaries);
        req.setAttribute("userRole", userRole);
        req.setAttribute("viewMode", "bidDetail");
        req.setAttribute("evaluationProgress", progress);

        req.getRequestDispatcher("/WEB-INF/views/modals/evaluation_panel_view.jsp").forward(req, resp);
    }

    /**
     * Get tenders that are relevant for evaluation based on user role
     */
    private List<TenderOffer> getEvaluationTenders(String userRole) {
        // Get tenders with relevant statuses for evaluation
        List<TenderOffer> allTenders = tenderService.getAllTendersExcludingDrafts();

        return allTenders.stream()
                .filter(t -> "UNDER_EVALUATION".equals(t.getStatus()) ||
                        "CLOSED".equals(t.getStatus()) ||
                        "EVALUATED".equals(t.getStatus()) ||
                        "AWARDED".equals(t.getStatus()))
                .sorted((t1, t2) -> {
                    // Sort: UNDER_EVALUATION first, then CLOSED, then EVALUATED
                    int priority1 = getStatusPriority(t1.getStatus());
                    int priority2 = getStatusPriority(t2.getStatus());
                    if (priority1 != priority2) {
                        return Integer.compare(priority1, priority2);
                    }
                    // Then by publish date (newest first)
                    return t2.getPublish_datetime().compareTo(t1.getPublish_datetime());
                })
                .collect(Collectors.toList());
    }

    private int getStatusPriority(String status) {
        switch (status) {
            case "UNDER_EVALUATION": return 1;
            case "CLOSED": return 2;
            case "EVALUATED": return 3;
            case "AWARDED": return 4;
            default: return 5;
        }
    }

    private boolean isValidEvaluationStatus(String status) {
        return "UNDER_EVALUATION".equals(status) ||
                "CLOSED".equals(status) ||
                "EVALUATED".equals(status) ||
                "AWARDED".equals(status);
    }

    private boolean isAlreadyAwarded(int tenderId) {
        // Check if tender has any awards
        return false; // Implement actual check
    }

    /**
     * Get bids that the current user hasn't evaluated yet
     */
    private List<UnevaluatedBidDTO> getUnevaluatedBids(List<TenderBid> allBids, int evaluatorId, int tenderId) {
        List<UnevaluatedBidDTO> unevaluated = new ArrayList<>();

        for (TenderBid bid : allBids) {
            boolean hasEvaluated = evaluationService.hasUserEvaluatedBid(evaluatorId, bid.getBid_id());

            if (!hasEvaluated) {
                UnevaluatedBidDTO dto = new UnevaluatedBidDTO();
                dto.setBidId(bid.getBid_id());
                dto.setSupplierName(getSupplierName(bid.getSupplier_id()));
                dto.setBidAmount(bid.getPrice());
                dto.setDeliveryDays(bid.getDelivery_days());
                dto.setSubmittedAt(bid.getSubmitted_at());
                dto.setEvaluationsCompleted(evaluationService.getCompletedEvaluationCount(bid.getBid_id()));
                dto.setTotalEvaluators(evaluationService.getTotalEvaluatorsCount(tenderId));
                unevaluated.add(dto);
            }
        }

        return unevaluated;
    }

    /**
     * Get evaluation progress for a tender
     */
    private Map<String, Object> getEvaluationProgress(int tenderId, List<TenderBid> bids, int evaluatorId) {
        Map<String, Object> progress = new HashMap<>();

        int totalBids = bids.size();
        int totalEvaluators = evaluationService.getTotalEvaluatorsCount(tenderId);
        int myEvaluatedCount = 0;
        int totalEvaluationsNeeded = totalBids * totalEvaluators;
        int completedEvaluations = 0;

        for (TenderBid bid : bids) {
            if (evaluationService.hasUserEvaluatedBid(evaluatorId, bid.getBid_id())) {
                myEvaluatedCount++;
            }
            completedEvaluations += evaluationService.getCompletedEvaluationCount(bid.getBid_id());
        }

        progress.put("totalBids", totalBids);
        progress.put("totalEvaluators", totalEvaluators);
        progress.put("myEvaluatedCount", myEvaluatedCount);
        progress.put("totalEvaluationsNeeded", totalEvaluationsNeeded);
        progress.put("completedEvaluations", completedEvaluations);
        progress.put("progressPercentage", totalEvaluationsNeeded > 0 ?
                (completedEvaluations * 100.0 / totalEvaluationsNeeded) : 0);

        return progress;
    }

    /**
     * Get leaderboard entries sorted by final score
     */
    private List<LeaderboardEntryDTO> getLeaderboard(List<TenderBid> allBids, int tenderId) {
        List<LeaderboardEntryDTO> entries = new ArrayList<>();

        for (TenderBid bid : allBids) {
            LeaderboardEntryDTO entry = new LeaderboardEntryDTO();
            entry.setBidId(bid.getBid_id());
            entry.setSupplierName(getSupplierName(bid.getSupplier_id()));
            entry.setRegNumber(getSupplierRegNumber(bid.getSupplier_id()));
            entry.setBidAmount(bid.getPrice());

            double avgTechnicalScore = evaluationService.getAverageTechnicalScore(bid.getBid_id());
            entry.setAvgTechnicalScore(avgTechnicalScore);

            double finalScore = evaluationService.calculateFinalScore(bid.getBid_id());
            entry.setFinalScore(finalScore);

            entry.setAwarded(isBidAwarded(bid.getBid_id()));

            entries.add(entry);
        }

        entries.sort((a, b) -> Double.compare(b.getFinalScore(), a.getFinalScore()));
        return entries;
    }

    private List<BidScoreSummary> getAllBidSummaries(List<TenderBid> allBids, int tenderId, int evaluatorId) {
        List<BidScoreSummary> summaries = new ArrayList<>();

        for (TenderBid bid : allBids) {
            BidScoreSummary summary = evaluationService.getBidScoreSummary(
                    bid.getBid_id(), tenderId, evaluatorId);
            summary.setSupplierName(getSupplierName(bid.getSupplier_id()));
            summaries.add(summary);
        }

        return summaries;
    }

    private boolean isAuthorizedEvaluator(String role) {
        return "PROCUREMENT_OFFICER".equals(role) ||
                "BOARD_MEMBER".equals(role);
    }

    private int getUserId(Object user) {
        if (user instanceof EmployeeData) {
            return ((EmployeeData) user).getEmployee_id();
        }
        return 0;
    }

    private String getUserRole(Object user) {
        if (user instanceof EmployeeData) {
            return ((EmployeeData) user).getRole_name();
        }
        return "";
    }

    private String getSupplierName(int supplierId) {
        // Implement actual supplier lookup
        return "Supplier #" + supplierId;
    }

    private String getSupplierRegNumber(int supplierId) {
        // Implement actual supplier reg number lookup
        return "REG-" + supplierId;
    }

    private boolean isBidAwarded(int bidId) {
        // Implement actual award check
        return false;
    }
}