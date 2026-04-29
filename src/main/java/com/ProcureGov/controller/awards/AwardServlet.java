package com.ProcureGov.controller.awards;

import com.ProcureGov.dto.AwardDTO;
import com.ProcureGov.model.Award;
import com.ProcureGov.model.EmployeeData;
import com.ProcureGov.model.SupplierData;
import com.ProcureGov.service.AwardService;
import com.ProcureGov.util.AwardPDFGenerator;
import com.lowagie.text.DocumentException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@WebServlet("/app/awards/*")
public class AwardServlet extends HttpServlet {

    private AwardService awardService;

    @Override
    public void init() throws ServletException {
        awardService = new AwardService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Public awards listing (all awarded tenders)
                List<AwardDTO> awards = awardService.getAllAwards();

                // Calculate total awarded value
                BigDecimal totalValue = awards.stream()
                        .map(AwardDTO::getAwardedValue)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                request.setAttribute("totalAwardedValue", totalValue);
                request.setAttribute("awards", awards);
                request.setAttribute("pageTitle", "Award Notices");
                request.setAttribute("pageSection", "Public Notices");
                request.getRequestDispatcher("/WEB-INF/views/modals/awards_list.jsp").forward(request, response);

            } else if (pathInfo.equals("/my-awards")) {
                // Supplier-specific awards
                Object user = session.getAttribute("user");
                if (user instanceof SupplierData supplier) {
                    List<AwardDTO> awards = awardService.getAwardsBySupplierId(supplier.getSupplier_id());
                    request.setAttribute("awards", awards);
                    request.setAttribute("pageTitle", "My Awarded Contracts");
                    request.setAttribute("pageSection", "My Portal");
                    request.getRequestDispatcher("/WEB-INF/views/modals/awards_list.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Supplier access only");
                }

            } else if (pathInfo.equals("/view")) {
                // View single award details
                String awardIdParam = request.getParameter("id");
                if (awardIdParam != null) {
                    int awardId = Integer.parseInt(awardIdParam);
                    List<AwardDTO> awards = awardService.getAllAwards();
                    AwardDTO award = awards.stream()
                            .filter(a -> a.getAwardId() == awardId)
                            .findFirst()
                            .orElse(null);

                    if (award != null) {
                        request.setAttribute("award", award);
                        request.setAttribute("pageTitle", "Award Notice: " + award.getTenderReference());
                        request.getRequestDispatcher("/WEB-INF/views/modals/award_detail.jsp").forward(request, response);
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Award not found");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Award ID required");
                }

            } else if (pathInfo.equals("/download")) {
                // Download single award as PDF
                String awardIdParam = request.getParameter("id");
                if (awardIdParam != null) {
                    int awardId = Integer.parseInt(awardIdParam);
                    List<AwardDTO> awards = awardService.getAllAwards();
                    AwardDTO award = awards.stream()
                            .filter(a -> a.getAwardId() == awardId)
                            .findFirst()
                            .orElse(null);

                    if (award != null) {
                        downloadAwardPDF(response, award);
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Award not found");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Award ID required");
                }

            } else if (pathInfo.equals("/download-gazette")) {
                // Download complete gazette as PDF
                String format = request.getParameter("format");
                List<AwardDTO> awards = awardService.getAllAwards();

                if (awards.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "No awards to generate gazette");
                    return;
                }

                downloadGazettePDF(response, awards, format);

            } else if (pathInfo.equals("/recent")) {
                // Recent awards for dashboard widgets
                String limitParam = request.getParameter("limit");
                int limit = limitParam != null ? Integer.parseInt(limitParam) : 5;
                List<AwardDTO> awards = awardService.getRecentAwards(limit);
                request.setAttribute("awards", awards);
                request.getRequestDispatcher("/WEB-INF/views/modals/recent_awards_widget.jsp").forward(request, response);

            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        String pathInfo = request.getPathInfo();

        if ("/create".equals(pathInfo)) {
            try {
                // Only procurement officers can create awards
                Object user = session.getAttribute("user");
                if (!(user instanceof EmployeeData officer)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only procurement officers can create awards");
                    return;
                }

                Award award = new Award();
                award.setTender_id(Integer.parseInt(request.getParameter("tenderId")));
                award.setBid_id(Integer.parseInt(request.getParameter("bidId")));
                award.setAwarded_value(Double.parseDouble(request.getParameter("awardedValue")));
                award.setOfficer_justification(request.getParameter("justification"));
                award.setAwarded_by(officer.getEmployee_id());
                award.setAward_date(new Timestamp(System.currentTimeMillis()));

                boolean success = awardService.createAward(award);

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/app/awards");
                } else {
                    request.setAttribute("error", "Failed to create award. Tender may already have an award.");
                    doGet(request, response);
                }

            } catch (Exception e) {
                request.setAttribute("error", "Error creating award: " + e.getMessage());
                doGet(request, response);
            }
        }
    }

    /**
     * Download single award as PDF
     */
    private void downloadAwardPDF(HttpServletResponse response, AwardDTO award) throws IOException {
        try {
            byte[] pdfBytes = AwardPDFGenerator.generateAwardPDF(award);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + award.getAwardNoticeNumber() + ".pdf\"");
            response.setContentLength(pdfBytes.length);

            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();

        } catch (DocumentException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating PDF");
        }
    }

    /**
     * Download complete gazette as PDF
     */
    private void downloadGazettePDF(HttpServletResponse response, List<AwardDTO> awards, String format) throws IOException {
        try {
            byte[] pdfBytes = AwardPDFGenerator.generateGazettePDF(awards);

            String filename = "procurement_gazette_" +
                    new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + ".pdf";

            if ("inline".equals(format)) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
            } else {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            }

            response.setContentLength(pdfBytes.length);
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();

        } catch (DocumentException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating PDF");
        }
    }
}