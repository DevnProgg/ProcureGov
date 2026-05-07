package com.ProcureGov.controller.Bids;

import com.ProcureGov.model.*;
import com.ProcureGov.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/app/bids/replace")
public class SwitchBidController extends HttpServlet {

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

        String existingBidIdParam = request.getParameter("existingBidId");
        String newTenderIdParam = request.getParameter("newTenderId");

        try {
            int existingBidId = Integer.parseInt(existingBidIdParam);
            int newTenderId = Integer.parseInt(newTenderIdParam);

            TenderOffer newTender = tenderService.getTenderById(newTenderId);

            // Validate new tender is still open
            if (newTender == null || !"OPEN".equals(newTender.getStatus())) {
                session.setAttribute("errorMessage", "This tender is no longer open for submissions.");
                response.sendRedirect(request.getContextPath() + "/app/tenders");
                return;
            }

            // Withdraw the existing bid
            boolean withdrawnSuccessfully = bidService.withdrawBid(existingBidId, supplier.getSupplier_id());

            if (!withdrawnSuccessfully) {
                session.setAttribute("errorMessage", "Failed to withdraw your existing bid. Please try again.");
                response.sendRedirect(request.getContextPath() + "/app/supplier/dashboard");
                return;
            }

            // Redirect to the submit bid form for the new tender
            // This will allow the supplier to fill out the form for the new tender
            response.sendRedirect(request.getContextPath() + "/app/bids/submit?tenderId=" + newTenderId);

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid bid or tender ID.");
            response.sendRedirect(request.getContextPath() + "/app/supplier/dashboard");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "An error occurred while switching your bid: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/app/supplier/dashboard");
        }
    }
}

