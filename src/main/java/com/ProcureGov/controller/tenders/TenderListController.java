package com.ProcureGov.controller.tenders;

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
import java.util.stream.Collectors;

@WebServlet("/app/tenders")
public class TenderListController extends HttpServlet {

    private TenderService tenderService;

    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get filter parameters
            String statusFilter = request.getParameter("status");
            String categoryFilter = request.getParameter("category");

            // Get filtered tenders
            List<TenderOffer> tenders;

            if (statusFilter != null && !statusFilter.isEmpty() ||
                    categoryFilter != null && !categoryFilter.isEmpty()) {
                tenders = tenderService.getFilteredTenders(statusFilter, categoryFilter);
            } else {
                tenders = tenderService.getAllTenders();
            }

            // In the doGet method, after getting tenders
            HttpSession session = request.getSession(false);
            boolean isOfficer = false;

            if (session != null) {
                if (session.getAttribute("user") != null && session.getAttribute("user") instanceof SupplierData) {
                    request.setAttribute("userRole", "SUPPLIER");
                } else if (session.getAttribute("user") != null) {
                    EmployeeData employee = (EmployeeData) session.getAttribute("user");
                    request.setAttribute("userRole", employee.getPrivilege_level());
                    isOfficer = "PROCUREMENT_OFFICER".equals(employee.getRole_name());
                }
            }

            if (!isOfficer) {
                tenders = tenders.stream()
                        .filter(t -> !"DRAFT".equals(t.getStatus()))
                        .collect(Collectors.toList());
            }

            // Supplier-level bid lock: hide Submit Bid everywhere until their current active bid is awarded
            boolean hasActiveBid = false;
            Integer activeBidTenderId = null;
            Object user = session != null ? session.getAttribute("user") : null;
            if (user instanceof SupplierData supplier) {
                List<BidSummaryDTO> supplierBids = new BidService().getSupplierBids(supplier.getSupplier_id());
                for (BidSummaryDTO bid : supplierBids) {
                    if (!"AWARDED".equals(bid.getEvaluationStatus())) {
                        hasActiveBid = true;
                        activeBidTenderId = bid.getTenderId();
                        break;
                    }
                }
            }

            // Get all unique categories for filter dropdown
            List<String> categories = tenderService.getAllCategories();

            // Set request attributes
            request.setAttribute("tenders", tenders);
            request.setAttribute("categories", categories);
            request.setAttribute("statusFilter", statusFilter != null ? statusFilter : "");
            request.setAttribute("catFilter", categoryFilter != null ? categoryFilter : "");
            request.setAttribute("totalCount", tenders.size());
            request.setAttribute("hasActiveBid", hasActiveBid);
            request.setAttribute("activeBidTenderId", activeBidTenderId);

            // Get user info for conditional rendering
            if (session != null) {
                if (session.getAttribute("user") != null && session.getAttribute("user") instanceof SupplierData) {
                    request.setAttribute("userRole", "SUPPLIER");
                } else if (session.getAttribute("user") != null) {
                    EmployeeData employee = (EmployeeData) session.getAttribute("user");
                    request.setAttribute("userRole", employee.getPrivilege_level());
                }
            }

            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/pages/tender_directory.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Unable to load tender registry");
            request.getRequestDispatcher("/WEB-INF/views/pages/tender_directory.jsp").forward(request, response);
        }
    }
}