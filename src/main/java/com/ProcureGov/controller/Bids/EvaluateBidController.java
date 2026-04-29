package com.ProcureGov.controller.Bids;

import com.ProcureGov.backgroundtasks.TenderStatusManager;
import com.ProcureGov.model.*;
import com.ProcureGov.repository.SupplierDataRepository;
import com.ProcureGov.service.*;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet(urlPatterns = {"/app/evaluations/evaluate", "/app/evaluations/submit-score"})
public class EvaluateBidController extends HttpServlet {

    private BidEvaluationService evaluationService;
    private TenderService tenderService;
    private BidService bidService;
    private SupplierDataRepository supplierService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.evaluationService = new BidEvaluationService();
        this.tenderService = new TenderService();
        this.bidService = new BidService();
        this.supplierService = new SupplierDataRepository();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        try {
            // Get parameters
            String bidIdStr = req.getParameter("bidId");
            String tenderIdStr = req.getParameter("tenderId");

            if (bidIdStr == null || tenderIdStr == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing bid or tender ID");
                return;
            }

            int bidId = Integer.parseInt(bidIdStr);
            int tenderId = Integer.parseInt(tenderIdStr);

            // Get current user
            Object user = session.getAttribute("user");
            int evaluatorId = getUserId(user);
            String userRole = getUserRole(user);

            // Check if user is authorized
            if (isAuthorizedEvaluator(userRole)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Only Procurement Officers and Evaluation Committee members can evaluate bids");
                return;
            }

            // Get tender and bid details
            TenderOffer tender = tenderService.getTenderById(tenderId);
            TenderBid bid = bidService.getBidById(bidId);

            if (tender == null || bid == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Tender or bid not found");
                return;
            }

            // Check if tender is in correct status
            if ("CLOSED".equals(tender.getStatus())) {
                TenderStatusManager.placeTenderUnderEvaluation(tenderId);
            } else if ("OPEN".equals(tender.getStatus()) || "DRAFT".equals(tender.getStatus())) {
                req.setAttribute("error", "This tender is not currently available for evaluation");
                resp.sendRedirect(req.getContextPath() + "/app/evaluations/panel");
                return;
            }

            // Check if user has already evaluated this bid
            boolean hasEvaluated = evaluationService.hasUserEvaluatedBid(evaluatorId, bidId);

            // Get evaluation summary
            BidScoreSummary scoreSummary = evaluationService.getBidScoreSummary(bidId, tenderId, evaluatorId);

            // Get evaluators status list
            List<EvaluatorStatus> evaluatorsList = evaluationService.getEvaluatorsStatus(tenderId);

            // Calculate auto scores
            double priceScore = evaluationService.calculatePriceScore(
                    bid.getPrice(), scoreSummary.getLowestBidAmount());
            double deliveryScore = evaluationService.calculateDeliveryScore(
                    bid.getDelivery_days(), scoreSummary.getShortestDeliveryDays());

            // Check if procurement officer should be notified
            boolean showOfficerNotification = false;
            if ("PROCUREMENT_OFFICER".equals(userRole) && !hasEvaluated) {
                int completedCount = evaluationService.getCompletedEvaluationCount(tenderId);
                int totalEvaluators = evaluatorsList.size();
                showOfficerNotification = (completedCount == totalEvaluators - 1);
            }

            // Set request attributes
            req.setAttribute("tender", tender);
            req.setAttribute("bid", bid);
            req.setAttribute("supplier", supplierService.findByUserId(bid.getSupplier_id()));
            req.setAttribute("hasEvaluated", hasEvaluated);
            req.setAttribute("priceScore", priceScore);
            req.setAttribute("deliveryScore", deliveryScore);
            req.setAttribute("evaluatorsList", evaluatorsList);
            req.setAttribute("evaluationsCompleted", scoreSummary.getEvaluationsCompleted());
            req.setAttribute("totalEvaluators", scoreSummary.getTotalEvaluators());
            req.setAttribute("showOfficerNotification", showOfficerNotification);

            if (hasEvaluated) {
                BidEvaluation myEvaluation = evaluationService.getMyEvaluation(bidId, evaluatorId);
                req.setAttribute("myTechnicalScore", myEvaluation.getTechnicalScore());
                req.setAttribute("myWeightedTotal", myEvaluation.getWeightedTotal());
                req.setAttribute("myEvaluationDate", myEvaluation.getEvaluatedAt());
            }

            // Forward to evaluation page
            req.getRequestDispatcher("/WEB-INF/views/modals/evaluation_panel.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Error loading evaluation: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/modals/evaluation_panel.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        try {
            // Get current user
            Object user = session.getAttribute("user");
            int evaluatorId = getUserId(user);
            String userRole = getUserRole(user);

            // Check authorization
            if (isAuthorizedEvaluator(userRole)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Only Procurement Officers and Evaluation Committee members can evaluate bids");
                return;
            }

            // Get form parameters
            String bidIdStr = req.getParameter("bidId");
            String tenderIdStr = req.getParameter("tenderId");
            String technicalScoreStr = req.getParameter("technicalScore");
            String bidAmountStr = req.getParameter("bidAmount");
            String deliveryDaysStr = req.getParameter("deliveryDays");

            // Validate required parameters
            if (bidIdStr == null || tenderIdStr == null || technicalScoreStr == null) {
                req.setAttribute("error", "Missing required evaluation parameters");
                req.getRequestDispatcher("/WEB-INF/views/modals/evaluation_panel.jsp").forward(req, resp);
                return;
            }

            int bidId = Integer.parseInt(bidIdStr);
            int tenderId = Integer.parseInt(tenderIdStr);
            double technicalScore = Double.parseDouble(technicalScoreStr);
            double bidAmount = Double.parseDouble(bidAmountStr != null ? bidAmountStr : "0");
            int deliveryDays = Integer.parseInt(deliveryDaysStr != null ? deliveryDaysStr : "0");

            // Submit the evaluation
            evaluationService.submitEvaluation(bidId, tenderId, evaluatorId, technicalScore, bidAmount, deliveryDays);

            // Redirect with success message
            resp.sendRedirect(req.getContextPath() + "/app/evaluations/evaluate?bidId=" + bidId +
                    "&tenderId=" + tenderId + "&success=Evaluation submitted successfully!");

        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid number format in submitted data");
            req.getRequestDispatcher("/WEB-INF/views/modals/evaluation_panel.jsp").forward(req, resp);

        } catch (IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
            String bidId = req.getParameter("bidId");
            String tenderId = req.getParameter("tenderId");
            resp.sendRedirect(req.getContextPath() + "/app/evaluations/evaluate?bidId=" + bidId +
                    "&tenderId=" + tenderId + "&error=" + java.net.URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));

        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/modals/evaluation_panel.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "An error occurred while submitting your evaluation. Please try again.");
            req.getRequestDispatcher("/WEB-INF/views/modals/evaluation_panel.jsp").forward(req, resp);
        }
    }

    private boolean isAuthorizedEvaluator(String role) {
        return !"PROCUREMENT_OFFICER".equals(role) &&
                !"BOARD_MEMBER".equals(role);
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
}