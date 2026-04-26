
package com.ProcureGov.controller.Bids;

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
import java.util.List;

@WebServlet("/app/evaluations/evaluate")
public class EvaluateBidServlet extends HttpServlet {

    private BidEvaluationService evaluationService;
    private TenderService tenderService;
    private BidEvaluationService bidService;
    private BidService bidService2;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.evaluationService = new BidEvaluationService();
        this.tenderService = new TenderService();
        this.bidService = new BidEvaluationService();
        this.bidService2 = new BidService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

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

            // Check if user is authorized (PROCUREMENT_OFFICER or EVALUATION_COMMITTEE)
            if (!isAuthorizedEvaluator(userRole)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Only Procurement Officers and Evaluation Committee members can evaluate bids");
                return;
            }

            // Get tender and bid details
            TenderOffer tender = tenderService.getTenderById(tenderId);
            TenderBid bid = bidService2.getBidById(bidId);

            if (tender == null || bid == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Tender or bid not found");
                return;
            }

            // Check if tender is in UNDER_EVALUATION status
            if (!"UNDER_EVALUATION".equals(tender.getStatus())) {
                req.setAttribute("error", "This tender is not currently under evaluation");
                req.getRequestDispatcher("/WEB-INF/views/modals/evaluation_panel.jsp").forward(req, resp);
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
            if (userRole.equals("PROCUREMENT_OFFICER") && !hasEvaluated) {
                int completedCount = evaluationService.getCompletedEvaluationCount();
                int totalEvaluators = evaluatorsList.size();
                showOfficerNotification = (completedCount == totalEvaluators - 1);
            }

            // Set request attributes
            req.setAttribute("tender", tender);
            req.setAttribute("bid", bid);
            req.setAttribute("supplier", getSupplierInfo(bid.getSupplier_id()));
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
            e.printStackTrace();
            req.setAttribute("error", "Error loading evaluation: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/modals/evaluation_panel.jsp").forward(req, resp);
        }
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

    private SupplierData getSupplierInfo(int supplierId) {
        // Implement this to get supplier information
        return null;
    }
}