package com.ProcureGov.controller.awards;

import com.ProcureGov.dto.BidDetailDTO;
import com.ProcureGov.model.TenderOffer;
import com.ProcureGov.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/app/officer/award")
public class AwardTenderServlet extends HttpServlet {

    private TenderService tenderService;
    private BidService bidService;
    private BidEvaluationService evaluationService;
    private AwardService awardService;

    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        bidService = new BidService();
        evaluationService = new BidEvaluationService();
        awardService = new AwardService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get all evaluated tenders for the dropdown
            List<TenderOffer> evaluatedTenders = tenderService.getTendersByStatus("EVALUATED");
            request.setAttribute("evaluatedTenders", evaluatedTenders);

            // Check if a specific tender is requested
            String tenderIdParam = request.getParameter("tenderId");

            if (tenderIdParam != null && !tenderIdParam.isEmpty()) {
                int tenderId = Integer.parseInt(tenderIdParam);

                // Get tender details
                TenderOffer tender = tenderService.getTenderById(tenderId);

                if (tender != null) {
                    request.setAttribute("tender", tender);

                    // Get all bids for this tender with evaluation data
                    List<BidDetailDTO> bids = bidService.getDetailedBidsForTender(tenderId);

                    // Check if any bid is already awarded
                    for (BidDetailDTO bid : bids) {
                        boolean isAwarded = awardService.hasAwardForTender(tenderId);
                        bid.setAwarded(isAwarded);
                    }

                    // Sort bids by evaluation score
                    bids.sort((b1, b2) -> Double.compare(b2.getEvaluationScore(), b1.getEvaluationScore()));

                    request.setAttribute("bids", bids);

                    // Evaluation statistics
                    int totalEvaluators = evaluationService.getTotalEvaluatorsCount();
                    int completedEvaluations = evaluationService.getCompletedEvaluationCount();

                    request.setAttribute("totalEvaluators", totalEvaluators);
                    request.setAttribute("completedEvaluations", completedEvaluations);

                    // Calculate completion percentage
                    double completionPercentage = totalEvaluators > 0
                            ? ((double) completedEvaluations / totalEvaluators) * 100
                            : 0;
                    request.setAttribute("completionPercentage", completionPercentage);
                }
            }

            // Forward to JSP
            request.setAttribute("pageTitle", "Award Contract");
            request.setAttribute("pageSection", "Operations");
            request.getRequestDispatcher("/WEB-INF/views/modals/award_contract.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Error loading award page: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/modals/award_contract.jsp")
                    .forward(request, response);
        }
    }


}