package com.ProcureGov.controller.Bids;

import com.ProcureGov.model.*;
import com.ProcureGov.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/app/bids/replace")
public class BidReplacementServlet extends HttpServlet {

    private BidService bidService;
    private TenderService tenderService;

    @Override
    public void init() throws ServletException {
        bidService = new BidService();
        tenderService = new TenderService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        SupplierData supplier = (SupplierData) session.getAttribute("user");

        String action = request.getParameter("action");
        String existingBidIdParam = request.getParameter("existingBidId");
        String newTenderIdParam = request.getParameter("newTenderId");

        try {
            int existingBidId = Integer.parseInt(existingBidIdParam);
            int newTenderId = Integer.parseInt(newTenderIdParam);

            // Verify the existing bid belongs to this supplier
            TenderBid existingBid = bidService.getBidById(existingBidId);
            if (existingBid == null || existingBid.getSupplier_id() != supplier.getSupplier_id()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized bid access");
                return;
            }

            if ("replace".equals(action)) {
                // Withdraw the existing bid
                boolean withdrawn = bidService.withdrawBid(existingBidId, supplier.getSupplier_id());

                if (withdrawn) {
                    // Set success message and redirect to submit form
                    session.setAttribute("successMessage",
                            "Your previous bid has been withdrawn. You may now submit your new bid.");
                    response.sendRedirect(request.getContextPath() +
                            "/app/bids/submit?tenderId=" + newTenderId);
                } else {
                    session.setAttribute("errorMessage", "Failed to withdraw existing bid. Please try again.");
                    response.sendRedirect(request.getContextPath() + "/app/supplier/dashboard");
                }

            } else {
                // Invalid action
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/app/supplier/dashboard");
        }
    }
}