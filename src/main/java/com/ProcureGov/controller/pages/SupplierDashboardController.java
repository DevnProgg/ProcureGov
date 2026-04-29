package com.ProcureGov.controller.pages;

import com.ProcureGov.dto.BidSummaryDTO;
import com.ProcureGov.model.*;
import com.ProcureGov.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/app/supplier/dashboard")
public class SupplierDashboardController extends HttpServlet {

    private TenderService tenderService;
    private BidService bidService;

    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        bidService = new BidService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        SupplierData loggedInSupplier = (SupplierData) session.getAttribute("user");

        try {
            // Get open tenders
            List<TenderOffer> openTenders = tenderService.getOpenTenders(5);
            request.setAttribute("openTenders", openTenders);

            // Get supplier's recent bids
            List<BidSummaryDTO> myBids = bidService.getSupplierBids(loggedInSupplier.getSupplier_id());
            request.setAttribute("myBids", myBids);

            // Check for success message from session
            String successMessage = (String) session.getAttribute("successMessage");
            if (successMessage != null) {
                request.setAttribute("successMessage", successMessage);
                session.removeAttribute("successMessage");
            }

            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/pages/supplierDashboard.jsp").forward(request, response);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading dashboard data");
        }
    }
}