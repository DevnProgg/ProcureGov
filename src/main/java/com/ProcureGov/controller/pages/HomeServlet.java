package com.ProcureGov.controller.pages;


import com.ProcureGov.dto.TenderStatsDTO;
import com.ProcureGov.model.*;
import com.ProcureGov.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private TenderService tenderService;

    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            if (session.getAttribute("user") != null && session.getAttribute("user") instanceof  SupplierData) {
                response.sendRedirect(request.getContextPath() + "/supplier/dashboard");
                return;
            } else if (session.getAttribute("user") != null && session.getAttribute("user") instanceof EmployeeData) {
                response.sendRedirect(request.getContextPath() + "app/officer/dashboard");
                return;
            }
        }

        try {
            List<TenderOffer> featuredTenders = tenderService.getOpenTenders(3);
            request.setAttribute("featuredTenders", featuredTenders);

            // Get tender statistics
            TenderStatsDTO stats = tenderService.getTenderStats();
            request.setAttribute("tenderStats", stats);

            // Set page metadata
            request.setAttribute("pageTitle", "ProcureGov - Lesotho Public Procurement Portal");
            request.setAttribute("pageDescription", "Access real-time tender listings, transparent bidding, and authoritative documentation for government infrastructure projects.");

        } catch (Exception e) {
            request.setAttribute("error", "Unable to load homepage data. Please try again later.");

            // Set default empty values to prevent JSP errors
            if (request.getAttribute("featuredTenders") == null) {
                request.setAttribute("featuredTenders", null);
            }
            if (request.getAttribute("tenderStats") == null) {
                request.setAttribute("tenderStats", new TenderStatsDTO());
            }
        }

        // Forward to homepage JSP
        request.getRequestDispatcher("/WEB-INF/views/pages/index.jsp").forward(request, response);
    }
}