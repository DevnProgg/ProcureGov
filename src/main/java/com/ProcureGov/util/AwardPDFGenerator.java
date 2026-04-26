package com.ProcureGov.util;


import com.ProcureGov.dto.AwardDTO;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AwardPDFGenerator {

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 24, Font.BOLD, new Color(0, 63, 135));
    private static final Font HEADING_FONT = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(0, 63, 135));
    private static final Font SUBHEADING_FONT = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(66, 71, 82));
    private static final Font BODY_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL);
    private static final Font BOLD_BODY_FONT = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static final Font SMALL_FONT = new Font(Font.HELVETICA, 8, Font.ITALIC, new Color(114, 119, 132));

    /**
     * Generate PDF for an award notice
     */
    public static byte[] generateAwardPDF(AwardDTO award) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        // Add metadata
        document.addTitle("Award Notice - " + award.getAwardNoticeNumber());
        document.addSubject("Official Procurement Award Notice");
        document.addKeywords("Procurement, Award, Tender, Contract");
        document.addAuthor("ProcureGov Authority");
        document.addCreator("ProcureGov System");

        document.open();

        // Add watermark
        addWatermark(writer, "OFFICIAL GAZETTE");

        // Add header
        addHeader(document, award);

        // Add award details
        addAwardDetails(document, award);

        // Add supplier information
        addSupplierInformation(document, award);

        // Add contract details
        addContractDetails(document, award);

        // Add justification
        addJustification(document, award);

        // Add footer
        addFooter(document, award);

        document.close();
        return outputStream.toByteArray();
    }

    /**
     * Generate PDF for multiple awards (complete gazette)
     */
    public static byte[] generateGazettePDF(java.util.List<AwardDTO> awards) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        document.addTitle("Procurement Gazette - Award Notices");
        document.addSubject("Official Procurement Gazette");
        document.addAuthor("ProcureGov Authority");

        document.open();

        // Add watermark
        addWatermark(writer, "OFFICIAL GAZETTE");

        // Add gazette header
        addGazetteHeader(document);

        // Add table of contents
        addTableOfContents(document, awards);

        // Add each award
        for (AwardDTO award : awards) {
            document.newPage();
            addGazetteAwardEntry(document, award);
        }

        // Add statistical summary
        addGazetteSummary(document, awards);

        document.close();
        return outputStream.toByteArray();
    }

    private static void addWatermark(PdfWriter writer, String text) {
        PdfContentByte contentByte = writer.getDirectContentUnder();
        Rectangle pageSize = writer.getPageSize();

        Font font = new Font(Font.HELVETICA, 60, Font.NORMAL, new Color(200, 200, 200, 50));
        com.lowagie.text.Phrase watermark = new com.lowagie.text.Phrase(text, font);

        ColumnText.showTextAligned(contentByte, Element.ALIGN_CENTER, watermark,
                pageSize.getWidth() / 2, pageSize.getHeight() / 2, 45);
    }

    private static void addHeader(Document document, AwardDTO award) throws DocumentException {
        // Official Seal Section
        Paragraph sealParagraph = new Paragraph();
        sealParagraph.setAlignment(Element.ALIGN_CENTER);

        Font sealFont = new Font(Font.HELVETICA, 8, Font.BOLD, new Color(0, 63, 135));
        Paragraph seal = new Paragraph("PROCUREMENT AUTHORITY • KINGDOM OF LESOTHO", sealFont);
        seal.setAlignment(Element.ALIGN_CENTER);
        document.add(seal);

        Paragraph blankLine = new Paragraph(" ");
        document.add(blankLine);

        // Title
        Paragraph title = new Paragraph("OFFICIAL AWARD NOTICE", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Notice Number
        Font noticeFont = new Font(Font.HELVETICA, 12, Font.BOLDITALIC, new Color(114, 119, 132));
        Paragraph noticeNumber = new Paragraph(award.getAwardNoticeNumber(), noticeFont);
        noticeNumber.setAlignment(Element.ALIGN_CENTER);
        document.add(noticeNumber);

        document.add(blankLine);

        // Divider
        addDivider(document);
    }

    private static void addGazetteHeader(Document document) throws DocumentException {
        Font sealFont = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(0, 63, 135));
        Paragraph seal = new Paragraph("PROCUREMENT AUTHORITY • KINGDOM OF LESOTHO", sealFont);
        seal.setAlignment(Element.ALIGN_CENTER);
        document.add(seal);

        Paragraph blankLine = new Paragraph(" ");
        document.add(blankLine);

        Paragraph title = new Paragraph("PROCUREMENT GAZETTE", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subtitle = new Paragraph("Official Publication of Award Notices", SUBHEADING_FONT);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        Paragraph date = new Paragraph("Published: " + now.format(formatter), SMALL_FONT);
        date.setAlignment(Element.ALIGN_CENTER);
        document.add(date);

        document.add(blankLine);
        addDivider(document);
    }

    private static void addAwardDetails(Document document, AwardDTO award) throws DocumentException {
        Paragraph sectionTitle = new Paragraph("AWARD INFORMATION", HEADING_FONT);
        sectionTitle.setSpacingBefore(15);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);
        table.setSpacingAfter(10);

        // Set column widths
        table.setWidths(new float[]{30, 70});

        addTableCell(table, "Tender Reference:", BOLD_BODY_FONT);
        addTableCell(table, award.getTenderReference(), BODY_FONT);

        addTableCell(table, "Tender Title:", BOLD_BODY_FONT);
        addTableCell(table, award.getTenderTitle(), BODY_FONT);

        addTableCell(table, "Tender Category:", BOLD_BODY_FONT);
        addTableCell(table, award.getTenderCategory(), BODY_FONT);

        addTableCell(table, "Award Date:", BOLD_BODY_FONT);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        addTableCell(table, sdf.format(award.getAwardDate()), BODY_FONT);

        addTableCell(table, "Award Notice Number:", BOLD_BODY_FONT);
        addTableCell(table, award.getAwardNoticeNumber(), BODY_FONT);

        addTableCell(table, "Contract Number:", BOLD_BODY_FONT);
        addTableCell(table, award.getContractNumber(), BODY_FONT);

        document.add(table);
    }

    private static void addSupplierInformation(Document document, AwardDTO award) throws DocumentException {
        Paragraph sectionTitle = new Paragraph("AWARDED SUPPLIER", HEADING_FONT);
        sectionTitle.setSpacingBefore(15);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{30, 70});

        addTableCell(table, "Business Name:", BOLD_BODY_FONT);
        addTableCell(table, award.getSupplierBusinessName(), BODY_FONT);

        addTableCell(table, "Email Address:", BOLD_BODY_FONT);
        addTableCell(table, award.getSupplierEmail(), BODY_FONT);

        addTableCell(table, "Phone Number:", BOLD_BODY_FONT);
        addTableCell(table, award.getSupplierPhone() != null ? award.getSupplierPhone() : "Not provided", BODY_FONT);

        document.add(table);
    }

    private static void addContractDetails(Document document, AwardDTO award) throws DocumentException {
        Paragraph sectionTitle = new Paragraph("CONTRACT DETAILS", HEADING_FONT);
        sectionTitle.setSpacingBefore(15);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{30, 70});

        addTableCell(table, "Awarded Value:", BOLD_BODY_FONT);
        addTableCell(table, String.format("M %,.2f", award.getAwardedValue()), BODY_FONT);

        addTableCell(table, "Bid Price:", BOLD_BODY_FONT);
        addTableCell(table, String.format("M %,.2f", award.getBidPrice()), BODY_FONT);

        addTableCell(table, "Delivery Timeline:", BOLD_BODY_FONT);
        addTableCell(table, award.getDeliveryDays() + " days from contract signing", BODY_FONT);

        if (award.getFinalScore() != null) {
            addTableCell(table, "Evaluation Score:", BOLD_BODY_FONT);
            addTableCell(table, String.format("%.1f%%", award.getFinalScore()), BODY_FONT);
        }

        document.add(table);
    }

    private static void addJustification(Document document, AwardDTO award) throws DocumentException {
        Paragraph sectionTitle = new Paragraph("OFFICER'S JUSTIFICATION", HEADING_FONT);
        sectionTitle.setSpacingBefore(15);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        // Add justification in a bordered box
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);
        table.setSpacingAfter(15);

        PdfPCell cell = new PdfPCell(new Phrase(award.getOfficerJustification(), BODY_FONT));
        cell.setPadding(10);
        cell.setBorderColor(new Color(194, 198, 212));
        cell.setBorderWidth(1);
        cell.setBackgroundColor(new Color(248, 249, 250));
        table.addCell(cell);

        document.add(table);

        // Issued by information
        Paragraph issuedBy = new Paragraph("Issued by: " + award.getAwardedByName(), BODY_FONT);
        issuedBy.setAlignment(Element.ALIGN_RIGHT);
        document.add(issuedBy);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        Paragraph dateIssued = new Paragraph("Date Issued: " + sdf.format(award.getAwardDate()), BODY_FONT);
        dateIssued.setAlignment(Element.ALIGN_RIGHT);
        document.add(dateIssued);
    }

    private static void addFooter(Document document, AwardDTO award) throws DocumentException {
        addDivider(document);

        Paragraph footer = new Paragraph(
                "This is a computer-generated document. No signature is required.\n" +
                        "For inquiries, contact the ProcureGov Authority at procurement@gov.ls",
                SMALL_FONT
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);

        // Add barcode/QR code for verification
        addVerificationCode(document, award);
    }

    private static void addVerificationCode(Document document, AwardDTO award) throws DocumentException {
        Paragraph verification = new Paragraph(
                "Verification Code: " + generateVerificationCode(award),
                new Font(Font.HELVETICA, 7, Font.ITALIC, new Color(114, 119, 132))
        );
        verification.setAlignment(Element.ALIGN_CENTER);
        verification.setSpacingBefore(10);
        document.add(verification);
    }

    private static String generateVerificationCode(AwardDTO award) {
        String input = award.getAwardId() + "-" + award.getTenderReference() + "-" + award.getAwardDate().getTime();
        return Integer.toHexString(input.hashCode()).toUpperCase();
    }

    private static void addTableCell(PdfPTable table, String value, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addDivider(Document document) throws DocumentException {
        Paragraph divider = new Paragraph("_____________________________________________________________________");
        divider.setAlignment(Element.ALIGN_CENTER);
        document.add(divider);
    }

    private static void addTableOfContents(Document document, java.util.List<AwardDTO> awards) throws DocumentException {
        Paragraph tocTitle = new Paragraph("TABLE OF CONTENTS", HEADING_FONT);
        tocTitle.setSpacingBefore(20);
        tocTitle.setSpacingAfter(10);
        document.add(tocTitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{70, 30});

        for (int i = 0; i < awards.size(); i++) {
            AwardDTO award = awards.get(i);
            addTableCell(table, (i + 1) + ". " + award.getTenderTitle(), BODY_FONT);
            addTableCell(table, "Page " + (i + 3), BODY_FONT);
        }

        document.add(table);
        document.newPage();
    }

    private static void addGazetteAwardEntry(Document document, AwardDTO award) throws DocumentException {
        // Entry number
        Paragraph entryNumber = new Paragraph("AWARD NOTICE", HEADING_FONT);
        entryNumber.setAlignment(Element.ALIGN_CENTER);
        document.add(entryNumber);

        Paragraph noticeNumber = new Paragraph(award.getAwardNoticeNumber(), BODY_FONT);
        noticeNumber.setAlignment(Element.ALIGN_CENTER);
        document.add(noticeNumber);

        document.add(new Paragraph(" "));

        // Award details in compact format
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{30, 70});

        addTableCell(table, "Tender:", BOLD_BODY_FONT);
        addTableCell(table, award.getTenderReference() + " - " + award.getTenderTitle(), BODY_FONT);

        addTableCell(table, "Supplier:", BOLD_BODY_FONT);
        addTableCell(table, award.getSupplierBusinessName(), BODY_FONT);

        addTableCell(table, "Awarded Value:", BOLD_BODY_FONT);
        addTableCell(table, String.format("M %,.2f", award.getAwardedValue()), BODY_FONT);

        addTableCell(table, "Date:", BOLD_BODY_FONT);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        addTableCell(table, sdf.format(award.getAwardDate()), BODY_FONT);

        document.add(table);

        addDivider(document);
    }

    private static void addGazetteSummary(Document document, java.util.List<AwardDTO> awards) throws DocumentException {
        document.newPage();

        Paragraph summaryTitle = new Paragraph("STATISTICAL SUMMARY", HEADING_FONT);
        summaryTitle.setAlignment(Element.ALIGN_CENTER);
        summaryTitle.setSpacingAfter(15);
        document.add(summaryTitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

        double totalValue = awards.stream()
                .mapToDouble(a -> a.getAwardedValue().doubleValue())
                .sum();

        long uniqueSuppliers = awards.stream()
                .mapToInt(AwardDTO::getSupplierId)
                .distinct()
                .count();

        addTableCell(table, "Total Awards Issued:", BOLD_BODY_FONT);
        addTableCell(table, String.valueOf(awards.size()), BODY_FONT);

        addTableCell(table, "Total Contract Value:", BOLD_BODY_FONT);
        addTableCell(table, String.format("M %,.2f", totalValue), BODY_FONT);

        addTableCell(table, "Unique Suppliers:", BOLD_BODY_FONT);
        addTableCell(table, String.valueOf(uniqueSuppliers), BODY_FONT);

        document.add(table);

        // Footer
        Paragraph footer = new Paragraph(
                "This gazette is published by the ProcureGov Authority in accordance with the Public Procurement Act.\n" +
                        "All awards are final and binding.",
                SMALL_FONT
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);
    }
}