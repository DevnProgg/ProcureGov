package com.ProcureGov.controller.tenders;

import com.ProcureGov.model.EmployeeData;
import com.ProcureGov.model.TenderOffer;
import com.ProcureGov.service.TenderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@WebServlet("/officer/create-tender")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,     // 1 MB
        maxFileSize = 1024 * 1024 * 5,       // 5 MB
        maxRequestSize = 1024 * 1024 * 5     // 5 MB
)
public class CreateTenderServlet extends HttpServlet {

    private TenderService tenderService;
    private static final String UPLOAD_DIR = "uploads" + File.separator + "notices";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB in bytes

    @Override
    public void init() throws ServletException {
        this.tenderService = new TenderService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/modals/create_tender_form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        boolean isAutoSave = "draft".equals(action);

        try {
            // Validate user session
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login to create tenders");
                return;
            }

            // Extract form parameters
            String title = req.getParameter("title");
            String category = req.getParameter("category");
            String estimatedValueStr = req.getParameter("estimatedValue");
            String closingDatetime = req.getParameter("closingDatetime");
            String description = req.getParameter("description");

            // Basic validation for non-autosave submissions
            if (!isAutoSave) {
                if (title == null || title.trim().isEmpty()) {
                    throw new IllegalArgumentException("Tender title is required");
                }
                if (category == null || category.trim().isEmpty()) {
                    throw new IllegalArgumentException("Category is required");
                }
                if (closingDatetime == null || closingDatetime.trim().isEmpty()) {
                    throw new IllegalArgumentException("Closing date is required");
                }
            }

            // Handle file upload with PDF validation
            Part filePart = req.getPart("noticeFile");
            String fileName = null;
            String filePath = null;

            if (filePart != null && filePart.getSize() > 0) {
                // Validate file size (5 MB max)
                if (filePart.getSize() > MAX_FILE_SIZE) {
                    throw new IllegalArgumentException("File size exceeds 5 MB limit");
                }

                // Validate file type (PDF only)
                String contentType = filePart.getContentType();
                fileName = extractFileName(filePart);
                if (!contentType.equals("application/pdf") && !fileName.toLowerCase().endsWith(".pdf")) {
                    throw new IllegalArgumentException("Only PDF files are allowed");
                }

                // Create upload directory if it doesn't exist
                String uploadDir = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }

                // Generate unique filename
                String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
                filePath = UPLOAD_DIR + File.separator + uniqueFileName;

                // Save file
                Path fileFullPath = Paths.get(uploadDir, uniqueFileName);
                Files.copy(filePart.getInputStream(), fileFullPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Determine status based on action
            String status = isAutoSave ? "DRAFT" : "OPEN";

            // Create TenderOffer object
            TenderOffer tender = new TenderOffer();
            tender.setTitle(title != null ? title.trim() : "");
            tender.setDescription(description != null ? description.trim() : "");
            tender.setCategory(category);
            tender.setStatus(status);

            // Set estimated value in Maloti (LSL)
            if (estimatedValueStr != null && !estimatedValueStr.isEmpty()) {
                tender.setEstimated_value(Double.parseDouble(estimatedValueStr));
            } else {
                tender.setEstimated_value(0.0);
            }

            // Set closing/expiry datetime
            if (closingDatetime != null && !closingDatetime.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                Date expiryDate = sdf.parse(closingDatetime);
                tender.setExpiry_datetime(expiryDate);
            }

            // Set notice file path
            tender.setNotice_file_path(filePath);

            // Set created_by from session
            EmployeeData employeeData = (EmployeeData) session.getAttribute("user");
            Integer userId = employeeData.getEmployee_id();
            tender.setCreated_by(userId != null ? userId : 1);

            // Generate reference number (MPW-YYYY-NNNN)
            String referenceNumber = generateReferenceNumber();
            tender.setReference_number(referenceNumber);

            // Set publish datetime for published tenders
            if (!isAutoSave) {
                tender.setPublish_datetime(new Date());
            }

            // Save to database
            tenderService.createTender(tender);

            // Send response based on action type
            if (isAutoSave) {
                //session.setAttribute("successMessage", "Tender " + referenceNumber + " draft saved successfully");
                resp.sendRedirect(req.getContextPath() + "/app/tenders");
            } else {
                // For published tenders, redirect with success message
                //session.setAttribute("successMessage", "Tender " + referenceNumber + " published successfully");
                resp.sendRedirect(req.getContextPath() + "/app/tenders");
            }

        } catch (Exception e) {
            if (isAutoSave) {
                // For auto-save failures, send error JSON
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
            } else {
                // For form submissions, redisplay form with errors
                req.setAttribute("formError", e.getMessage());
                req.setAttribute("prevTitle", req.getParameter("title"));
                req.setAttribute("prevCategory", req.getParameter("category"));
                req.setAttribute("prevBudget", req.getParameter("estimatedValue"));
                req.setAttribute("prevClosing", req.getParameter("closingDatetime"));
                req.setAttribute("prevDescription", req.getParameter("description"));
                req.getRequestDispatcher("/WEB-INF/views/modals/create_tender_form.jsp").forward(req, resp);
            }
        }
    }

    private String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) return null;

        for (String item : contentDisposition.split(";")) {
            if (item.trim().startsWith("filename")) {
                String filename = item.substring(item.indexOf("=") + 2, item.length() - 1);
                return filename.replace("\\", "/").substring(filename.lastIndexOf("/") + 1);
            }
        }
        return null;
    }

    private String generateReferenceNumber() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        Random random = new Random();
        int randomNum = 1000 + random.nextInt(9000); // 1000-9999

        return "MPW-" + year + "-" + String.format("%04d", randomNum);
    }
}