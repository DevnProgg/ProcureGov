package com.ProcureGov.controller.Bids;

import com.ProcureGov.model.*;
import com.ProcureGov.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/app/bids/keep-existing")
public class KeepExistingBidController extends HttpServlet {

    private TenderService tenderService;

    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        String newTenderIdParam = request.getParameter("newTenderId");

        try {
            int newTenderId = Integer.parseInt(newTenderIdParam);
            TenderOffer tender = tenderService.getTenderById(newTenderId);

            // Set notification message
            session.setAttribute("infoMessage",
                    "You have chosen to keep your existing bid. The new bid submission for '" +
                            (tender != null ? tender.getTitle() : "this tender") + "' has been cancelled.");

            // Redirect back to dashboard or tender list
            response.sendRedirect(request.getContextPath() + "/app/supplier/dashboard");

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid tender ID");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/app/supplier/dashboard");
        }
    }
}