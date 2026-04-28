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
import java.util.Date;
import java.util.UUID;

@WebServlet("/officer/edit-tender")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,     // 1 MB
        maxFileSize = 1024 * 1024 * 5,       // 5 MB
        maxRequestSize = 1024 * 1024 * 5     // 5 MB
)
public class EditTenderServlet extends HttpServlet {

    private TenderService tenderService;
    private static final String UPLOAD_DIR = "uploads" + File.separator + "notices";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB in bytes

    @Override
    public void init() throws ServletException {
        this.tenderService = new TenderService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // Validate session
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        EmployeeData user = (EmployeeData) session.getAttribute("user");
        if (!"PROCUREMENT_OFFICER".equals(user.getRole_name())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        try {
            String tenderIdStr = req.getParameter("id");
            if (tenderIdStr == null || tenderIdStr.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/app/tenders");
                return;
            }

            int tenderId = Integer.parseInt(tenderIdStr);
            TenderOffer tender = tenderService.getTenderById(tenderId);

            if (tender == null) {
                session.setAttribute("errorMessage", "Tender not found");
                resp.sendRedirect(req.getContextPath() + "/app/tenders");
                return;
            }

            // Check if tender can be edited (only DRAFT status)
            if (!"DRAFT".equals(tender.getStatus())) {
                session.setAttribute("errorMessage", "Only draft tenders can be edited");
                resp.sendRedirect(req.getContextPath() + "/app/tenders");
                return;
            }

            // Pre-populate form fields
            req.setAttribute("tender", tender);
            req.setAttribute("isEdit", true);

            // Format dates for the form
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            if (tender.getExpiry_datetime() != null) {
                req.setAttribute("formattedClosingDate", dateFormat.format(tender.getExpiry_datetime()));
            }

            // Forward to the create/edit form
            req.getRequestDispatcher("/WEB-INF/views/modals/create_tender_form.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid tender ID");
            resp.sendRedirect(req.getContextPath() + "/app/tenders");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Error loading tender: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/app/tenders");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String tenderIdStr = req.getParameter("tenderId");

        try {
            // Validate user session
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login to edit tenders");
                return;
            }

            EmployeeData user = (EmployeeData) session.getAttribute("user");
            if (!"PROCUREMENT_OFFICER".equals(user.getRole_name())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }

            if (tenderIdStr == null || tenderIdStr.isEmpty()) {
                throw new IllegalArgumentException("Tender ID is required");
            }

            int tenderId = Integer.parseInt(tenderIdStr);

            // Check authorization
            if (!tenderService.canEditTender(tenderId, user.getEmployee_id())) {
                throw new IllegalArgumentException("You can only edit your own draft tenders");
            }

            // Extract form parameters
            String title = req.getParameter("title");
            String category = req.getParameter("category");
            String estimatedValueStr = req.getParameter("estimatedValue");
            String closingDatetime = req.getParameter("closingDatetime");
            String description = req.getParameter("description");

            // Validation
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Tender title is required");
            }
            if (category == null || category.trim().isEmpty()) {
                throw new IllegalArgumentException("Category is required");
            }
            if (closingDatetime == null || closingDatetime.trim().isEmpty()) {
                throw new IllegalArgumentException("Closing date is required");
            }

            // Handle file upload
            Part filePart = req.getPart("noticeFile");
            String filePath = req.getParameter("existingFilePath"); // Keep existing file if no new upload

            if (filePart != null && filePart.getSize() > 0) {
                // Validate file size
                if (filePart.getSize() > MAX_FILE_SIZE) {
                    throw new IllegalArgumentException("File size exceeds 5 MB limit");
                }

                // Validate file type
                String contentType = filePart.getContentType();
                String fileName = extractFileName(filePart);
                if (!"application/pdf".equals(contentType)) {
                    assert fileName != null;
                    if (!fileName.toLowerCase().endsWith(".pdf")) {
                        throw new IllegalArgumentException("Only PDF files are allowed");
                    }
                }

                // Delete old file if exists
                if (filePath != null && !filePath.isEmpty()) {
                    String oldFilePath = getServletContext().getRealPath("") + File.separator + filePath;
                    File oldFile = new File(oldFilePath);
                    if (oldFile.exists()) {
                        if(!oldFile.delete()){
                            throw new IllegalArgumentException("Cannot delete old file");
                        }
                    }
                }

                // Save new file
                String uploadDir = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    if(!uploadDirFile.mkdirs()){
                        throw new IllegalArgumentException("Cannot create upload directory");
                    }
                }

                String uniqueFileName = UUID.randomUUID() + "_" + fileName;
                filePath = UPLOAD_DIR + File.separator + uniqueFileName;
                Path fileFullPath = Paths.get(uploadDir, uniqueFileName);
                Files.copy(filePart.getInputStream(), fileFullPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Determine status based on action
            String status = "draft".equals(action) ? "DRAFT" : "OPEN";

            // Update tender object
            TenderOffer tender = new TenderOffer();
            tender.setTender_id(tenderId);
            tender.setTitle(title.trim());
            tender.setDescription(description != null ? description.trim() : "");
            tender.setCategory(category);
            tender.setStatus(status);
            tender.setNotice_file_path(filePath);

            // Set estimated value
            if (estimatedValueStr != null && !estimatedValueStr.isEmpty()) {
                tender.setEstimated_value(Double.parseDouble(estimatedValueStr));
            } else {
                tender.setEstimated_value(0.0);
            }

            // Set closing datetime
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date expiryDate = sdf.parse(closingDatetime);
            tender.setExpiry_datetime(expiryDate);

            // Set publish datetime if publishing
            if (!"draft".equals(action)) {
                tender.setPublish_datetime(new Date());
            }

            // Update in database
            tenderService.updateTender(tender);

            // Send response
            if ("draft".equals(action)) {
                session.setAttribute("successMessage", "Draft updated successfully");
            } else {
                session.setAttribute("successMessage", "Tender published successfully");
            }
            resp.sendRedirect(req.getContextPath() + "/app/tenders");

        } catch (Exception e) {
            // Handle errors
            req.setAttribute("formError", e.getMessage());
            req.setAttribute("prevTitle", req.getParameter("title"));
            req.setAttribute("prevCategory", req.getParameter("category"));
            req.setAttribute("prevBudget", req.getParameter("estimatedValue"));
            req.setAttribute("prevClosing", req.getParameter("closingDatetime"));
            req.setAttribute("prevDescription", req.getParameter("description"));
            req.setAttribute("isEdit", true);
            req.setAttribute("tenderId", tenderIdStr);
            req.setAttribute("existingFilePath", req.getParameter("existingFilePath"));
            req.getRequestDispatcher("/WEB-INF/views/modals/create_tender_form.jsp").forward(req, resp);
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
}