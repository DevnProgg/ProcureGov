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

@WebServlet("/app/supplier/bids/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10,  // 10 MB
        maxRequestSize = 1024 * 1024 * 50 // 50 MB
)
public class SupplierBidsController extends HttpServlet {

    private BidService bidService;
    private TenderService tenderService;
    private static final String UPLOAD_DIR = "uploads/bids";

    @Override
    public void init() throws ServletException {
        bidService = new BidService();
        tenderService = new TenderService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        SupplierData loggedInSupplier = (SupplierData) session.getAttribute("user");
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // List all bids
                List<BidSummaryDTO> allBids = bidService.getSupplierBids(loggedInSupplier.getSupplier_id());
                request.setAttribute("bids", allBids);
                request.getRequestDispatcher("/WEB-INF/views/modals/bids_list.jsp").forward(request, response);

            } else if (pathInfo.equals("/submit")) {
                // Show bid submission form
                String tenderIdParam = request.getParameter("tenderId");
                if (tenderIdParam != null) {
                    int tenderId = Integer.parseInt(tenderIdParam);
                    TenderOffer tender = tenderService.getTenderById(tenderId);
                    request.setAttribute("tender", tender);
                }
                request.getRequestDispatcher("/WEB-INF/views/modals/bid_submission_form.jsp").forward(request, response);

            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        SupplierData loggedInSupplier = (SupplierData) session.getAttribute("user");
        String pathInfo = request.getPathInfo();

        if ("/submit".equals(pathInfo)) {
            try {
                // Handle bid submission
                int tenderId = Integer.parseInt(request.getParameter("tenderId"));
                int deliveryDays = Integer.parseInt(request.getParameter("deliveryDays"));
                String complianceStatement = request.getParameter("complianceStatement");

                // Handle file upload
                Part filePart = request.getPart("bidDocument");
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

                // Save file
                String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    if(!uploadDir.mkdirs()) {
                        throw new IOException("Failed to create upload directory");
                    }
                }

                String filePath = uploadPath + File.separator + System.currentTimeMillis() + "_" + fileName;
                filePart.write(filePath);

                // Create bid
                TenderBid bid = new TenderBid();
                bid.setTender_id(tenderId);
                bid.setSupplier_id(loggedInSupplier.getSupplier_id());
                bid.setDelivery_days(deliveryDays);
                bid.setCompliance_statement(complianceStatement);
                bid.setDocument_file_path(UPLOAD_DIR + "/" + fileName);
                bid.setSubmitted_at(new Timestamp(System.currentTimeMillis()));

                boolean success = bidService.submitBid(bid);

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/supplier/bids?success=Bid submitted successfully");
                } else {
                    request.setAttribute("error", "Failed to submit bid");
                    doGet(request, response);
                }
            } catch (Exception e) {
                request.setAttribute("error", "Error submitting bid: " + e.getMessage());
                doGet(request, response);
            }
        }
    }
}