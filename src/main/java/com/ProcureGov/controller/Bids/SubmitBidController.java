package com.ProcureGov.controller.Bids;

import com.ProcureGov.dto.BidSummaryDTO;
import com.ProcureGov.model.*;
import com.ProcureGov.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;

@WebServlet("/app/bids/submit")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,        // 1 MB
        maxFileSize = 1024 * 1024 * 10,         // 10 MB
        maxRequestSize = 1024 * 1024 * 50       // 50 MB
)
public class SubmitBidController extends HttpServlet {

    private TenderService tenderService;
    private BidService bidService;
    private static final String UPLOAD_DIR = "uploads/bids";

    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        bidService = new BidService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        SupplierData supplier = (SupplierData) session.getAttribute("user");
        String tenderIdParam = request.getParameter("tenderId");

        // Validate tenderId parameter
        if (tenderIdParam == null || tenderIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/app/tenders");
            return;
        }

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            TenderOffer tender = tenderService.getTenderById(tenderId);

            // Validate tender exists and is open
            if (tender == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Tender not found");
                return;
            }

            if (!"OPEN".equals(tender.getStatus())) {
                session.setAttribute("errorMessage", "This tender is no longer open for submissions.");
                response.sendRedirect(request.getContextPath() + "/app/tenders/" + tenderId);
                return;
            }

            // Check if tender has expired
            if (tender.getExpiry_datetime() != null &&
                    tender.getExpiry_datetime().before(new Timestamp(System.currentTimeMillis()))) {
                session.setAttribute("errorMessage", "The submission deadline for this tender has passed.");
                response.sendRedirect(request.getContextPath() + "/app/tenders/" + tenderId);
                return;
            }

            // Enforce one-active-bid rule: if supplier has any active (non-awarded) bid, block new submissions
            Integer activeBidTenderId = getActiveBidTenderId(supplier.getSupplier_id());
            if (activeBidTenderId != null) {
                session.setAttribute("errorMessage",
                        "You already have an active bid on another tender and cannot submit a new bid until that tender is awarded.");
                response.sendRedirect(request.getContextPath() + "/app/supplier/dashboard");
                return;
            }

            // No existing bid - proceed to normal submission form
            request.setAttribute("tender", tender);
            request.setAttribute("pageTitle", "Submit Bid");
            request.setAttribute("activePage", "tenders");
            request.setAttribute("pageSection", "Tenders");

            request.getRequestDispatcher("/WEB-INF/views/modals/bid_submission_form.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid tender ID");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading the bid form");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        SupplierData supplier = (SupplierData) session.getAttribute("user");

        // Get form parameters
        String tenderIdParam = request.getParameter("tenderId");
        String bidAmountParam = request.getParameter("bidAmount");
        String deliveryDaysParam = request.getParameter("deliveryDays");
        String complianceStatement = request.getParameter("complianceStatement");
        Part filePart = null;

        try {
            filePart = request.getPart("bidDocument");
        } catch (Exception e) {
            // File might not be uploaded
        }

        // Validate tenderId
        if (tenderIdParam == null || tenderIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/app/tenders");
            return;
        }

        int tenderId = Integer.parseInt(tenderIdParam);
        TenderOffer tender = null;

        try {
            tender = tenderService.getTenderById(tenderId);

            // Re-validate tender is still open
            if (tender == null || !"OPEN".equals(tender.getStatus())) {
                request.setAttribute("formError", "This tender is no longer open for submissions.");
                request.setAttribute("tender", tender);
                forwardWithPreviousInput(request, response, tenderIdParam, bidAmountParam,
                        deliveryDaysParam, complianceStatement);
                return;
            }

            // Enforce one-active-bid rule on POST as well, so direct requests cannot bypass the UI
            Integer activeBidTenderId = getActiveBidTenderId(supplier.getSupplier_id());
            if (activeBidTenderId != null) {
                request.setAttribute("formError",
                        "You already have an active bid on another tender and cannot submit a new bid until that tender is awarded.");
                request.setAttribute("tender", tender);
                forwardWithPreviousInput(request, response, tenderIdParam, bidAmountParam,
                        deliveryDaysParam, complianceStatement);
                return;
            }

            // Check if supplier has already bid
            if (bidService.hasSupplierBidOnTender(supplier.getSupplier_id(), tenderId)) {
                response.sendRedirect(request.getContextPath() + "/app/supplier/dashboard?error=already_bid");
                return;
            }

            // Validate required fields
            StringBuilder validationErrors = new StringBuilder();

            double bidAmount = 0;
            if (bidAmountParam == null || bidAmountParam.trim().isEmpty()) {
                validationErrors.append("Bid amount is required. ");
            } else {
                try {
                    bidAmount = Double.parseDouble(bidAmountParam);
                    if (bidAmount <= 0) {
                        validationErrors.append("Bid amount must be greater than zero. ");
                    }
                } catch (NumberFormatException e) {
                    validationErrors.append("Invalid bid amount format. ");
                }
            }

            if (deliveryDaysParam == null || deliveryDaysParam.trim().isEmpty()) {
                validationErrors.append("Delivery timeline is required. ");
            } else {
                try {
                    int deliveryDays = Integer.parseInt(deliveryDaysParam);
                    if (deliveryDays <= 0) {
                        validationErrors.append("Delivery days must be greater than zero. ");
                    }
                } catch (NumberFormatException e) {
                    validationErrors.append("Invalid delivery days format. ");
                }
            }

            if (complianceStatement == null || complianceStatement.trim().isEmpty()) {
                validationErrors.append("Compliance statement is required. ");
            } else if (complianceStatement.length() < 50) {
                validationErrors.append("Compliance statement must be at least 50 characters. ");
            } else if (complianceStatement.length() > 600) {
                validationErrors.append("Compliance statement cannot exceed 600 characters. ");
            }

            if (filePart == null || filePart.getSize() == 0) {
                validationErrors.append("Technical bid document is required. ");
            } else {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                if (!fileName.toLowerCase().endsWith(".pdf") && !fileName.toLowerCase().endsWith(".docx")) {
                    validationErrors.append("Document must be in PDF or DOCX format. ");
                }
                if (filePart.getSize() > 10 * 1024 * 1024) {
                    validationErrors.append("File size exceeds 10MB limit. ");
                }
            }

            // If validation fails, re-render form with errors
            if (!validationErrors.isEmpty()) {
                request.setAttribute("formError", validationErrors.toString());
                request.setAttribute("tender", tender);
                forwardWithPreviousInput(request, response, tenderIdParam, bidAmountParam,
                        deliveryDaysParam, complianceStatement);
                return;
            }

            // Process file upload
            assert filePart != null;
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                if(!uploadDir.mkdirs()){
                    throw new RuntimeException("Unable to create upload directory.");
                }
            }

            String uniqueFileName = System.currentTimeMillis() + "_" + supplier.getSupplier_id() + "_" + fileName;
            String filePath = uploadPath + File.separator + uniqueFileName;
            filePart.write(filePath);

            // Create and save bid
            TenderBid bid = new TenderBid();
            bid.setTender_id(tenderId);
            bid.setSupplier_id(supplier.getSupplier_id());
            assert deliveryDaysParam != null;
            bid.setDelivery_days(Integer.parseInt(deliveryDaysParam));
            assert complianceStatement != null;
            bid.setCompliance_statement(complianceStatement.trim());
            bid.setDocument_file_path(UPLOAD_DIR + "/" + uniqueFileName);
            bid.setSubmitted_at(new Timestamp(System.currentTimeMillis()));
            bid.setPrice(bidAmount);

            boolean success = bidService.submitBid(bid);

            if (success) {
                // Set success message in session for display on dashboard
                session.setAttribute("successMessage", "Your bid has been successfully submitted for tender: " + tender.getTitle());
                response.sendRedirect(request.getContextPath() + "/app/supplier/dashboard");
            } else {
                request.setAttribute("formError", "Failed to submit bid. Please try again.");
                request.setAttribute("tender", tender);
                forwardWithPreviousInput(request, response, tenderIdParam, bidAmountParam,
                        deliveryDaysParam, complianceStatement);
            }

        } catch (Exception e) {
            request.setAttribute("formError", "An error occurred while processing your bid: " + e.getMessage());
            request.setAttribute("tender", tender);
            forwardWithPreviousInput(request, response, tenderIdParam, bidAmountParam,
                    deliveryDaysParam, complianceStatement);
        }
    }

    /**
     * Helper method to forward with previous input values
     */
    private void forwardWithPreviousInput(HttpServletRequest request, HttpServletResponse response,
                                          String tenderId, String bidAmount, String deliveryDays,
                                          String complianceStatement)
            throws ServletException, IOException {

        request.setAttribute("prevBidAmount", bidAmount);
        request.setAttribute("prevDeliveryDays", deliveryDays);
        request.setAttribute("prevStatement", complianceStatement);
        request.setAttribute("pageTitle", "Submit Bid");
        request.setAttribute("activePage", "tenders");
        request.setAttribute("pageSection", "Tenders");
        request.setAttribute("tenderId", tenderId);

        request.getRequestDispatcher("/WEB-INF/views/modals/bid_submission_form.jsp").forward(request, response);
    }

    /**
     * Returns the tender ID of the supplier's first active bid, or null if none exists.
     */
    private Integer getActiveBidTenderId(int supplierId) throws Exception {
        List<BidSummaryDTO> supplierBids = bidService.getSupplierBids(supplierId);
        return supplierBids.stream()
                .filter(bid -> !"AWARDED".equals(bid.getEvaluationStatus()))
                .map(BidSummaryDTO::getTenderId)
                .findFirst()
                .orElse(null);
    }
}