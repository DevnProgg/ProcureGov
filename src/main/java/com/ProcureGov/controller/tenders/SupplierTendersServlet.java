package com.ProcureGov.controller.tenders;

import com.ProcureGov.model.*;
import com.ProcureGov.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/app/supplier/tenders/*")
public class SupplierTendersServlet extends HttpServlet {

    private TenderService tenderService;

    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // List all open tenders
                List<TenderOffer> allTenders = tenderService.getAllOpenTenders();
                request.setAttribute("tenders", allTenders);
                request.getRequestDispatcher("/WEB-INF/views/pages/tender_directory.jsp").forward(request, response);

            } else {
                // View specific tender details
                String tenderIdStr = pathInfo.substring(1);
                int tenderId = Integer.parseInt(tenderIdStr);

                TenderOffer tender = tenderService.getTenderById(tenderId);
                if (tender != null) {
                    request.setAttribute("tender", tender);
                    request.getRequestDispatcher("/WEB-INF/views/modals/tender_detail.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}