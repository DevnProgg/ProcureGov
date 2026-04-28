package com.ProcureGov.controller.tenders;

import com.ProcureGov.model.*;
import com.ProcureGov.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/app/tenders/*")
public class TenderDetailServlet extends HttpServlet {

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

        String pathInfo = request.getPathInfo();

        // If no tender ID, redirect to tender list
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/tenders");
            return;
        }

        try {
            // Extract tender ID from path
            String tenderIdStr = pathInfo.substring(1);
            int tenderId = Integer.parseInt(tenderIdStr);

            // Get tender details
            TenderOffer tender = tenderService.getTenderById(tenderId);

            if (tender == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Set tender attribute
            request.setAttribute("tender", tender);

            // Check if user is logged in and has already bid on this tender
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("user") instanceof SupplierData supplier) {
                boolean hasBid = bidService.hasSupplierBidOnTender(supplier.getSupplier_id(), tenderId);
                request.setAttribute("hasBid", hasBid);
                request.setAttribute("userRole", "SUPPLIER");
            }

            if ("DRAFT".equals(tender.getStatus())) {
                request.setAttribute("canEdit", true);
            } else {
                request.setAttribute("canEdit", false);
            }
            // Get bid count for this tender
            int bidCount = bidService.getBidCountForTender(tenderId);
            request.setAttribute("bidCount", bidCount);

            // Forward to detail page
            request.getRequestDispatcher("/WEB-INF/views/modals/tender_detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid tender ID");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}