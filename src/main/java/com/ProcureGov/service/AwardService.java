package com.ProcureGov.service;

import com.ProcureGov.dto.AwardDTO;
import com.ProcureGov.model.*;
import com.ProcureGov.repository.AwardRepository;
import com.ProcureGov.util.EmailUtility;

import java.util.List;
import java.util.logging.Logger;

public class AwardService {

    private static final Logger logger = Logger.getLogger(AwardService.class.getName());
    private final AwardRepository awardDAO;
    private final EmailUtility emailUtility;

    public AwardService() {
        this.awardDAO = new AwardRepository();
        this.emailUtility = new EmailUtility();

    }

    /**
     * Create a new award and send email notification to supplier
     */
    public boolean createAward(Award award) {
        // Validate award data
        if (award.getTender_id() <= 0 || award.getBid_id() <= 0 || award.getAwarded_by() <= 0) {
            logger.warning("Invalid award data for tender_id: " + award.getTender_id());
            return false;
        }

        // Check if award already exists for this tender
        if (awardDAO.hasAwardForTender(award.getTender_id())) {
            logger.warning("Award already exists for tender_id: " + award.getTender_id());
            return false;
        }

        // Create the award in the database
        boolean awardCreated = awardDAO.createAward(award);

        if (awardCreated) {
            // Send email notification to the supplier
            sendAwardNotificationEmail(award);
            logger.info("Award created and notification sent for award_id: " + award.getAward_id());
        }

        return awardCreated;
    }

    /**
     * Send email notification to supplier about the award
     */
    private void sendAwardNotificationEmail(Award award) {
        try {
            // Get supplier details (you'll need to implement these methods in your DAO/repository)
            SupplierData supplier = getSupplierByBidId(award.getBid_id());
            TenderOffer tender = getTenderById(award.getTender_id());

            if (supplier == null || tender == null) {
                logger.warning("Could not find supplier or tender details for award notification");
                return;
            }

            // Validate supplier email
            String supplierEmail = supplier.getEmail();
            if (supplierEmail == null || supplierEmail.trim().isEmpty()) {
                logger.warning("No email address found for supplier: " + supplier.getBusiness_name());
                return;
            }

            // Generate email content
            String subject = String.format("Contract Award Notification - %s", tender.getTitle());
            String emailBody = emailUtility.generateAwardEmailBody(
                    supplier,
                    tender.getTitle(),
                    award.getAward_date() != null ? award.getAward_date().toString() : "N/A",
                    award.getAwarded_value(),
                    String.valueOf(award.getAward_id())
            );

            // Send email asynchronously to avoid blocking
            new Thread(() -> {
                boolean emailSent = emailUtility.sendEmail(supplierEmail, subject, emailBody);
                if (emailSent) {
                    logger.info("Award notification email sent successfully to: " + supplierEmail);
                    // Optionally update the award record to indicate email was sent
                    awardDAO.updateEmailSentStatus(award.getAward_id(), true);
                } else {
                    logger.warning("Failed to send award notification email to: " + supplierEmail);
                }
            }).start();

        } catch (Exception e) {
            logger.severe("Error sending award notification email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper methods (you'll need to implement these based on your data layer)
    private SupplierData getSupplierByBidId(int bidId) {
        // This should fetch supplier details from your database based on the bid
        // You might need to create a SupplierRepository or add this method to AwardRepository
        return awardDAO.getSupplierByBidId(bidId);
    }

    private TenderOffer getTenderById(int tenderId) {
        // This should fetch tender details from your database
        return awardDAO.getTenderById(tenderId);
    }

    /**
     * Get all awards (public view)
     */
    public List<AwardDTO> getAllAwards() {
        return awardDAO.getAllAwards();
    }

    /**
     * Get awards by supplier ID
     */
    public List<AwardDTO> getAwardsBySupplierId(int supplierId) {
        return awardDAO.getAwardsBySupplierId(supplierId);
    }

    /**
     * Get recent awards for dashboard
     */
    public List<AwardDTO> getRecentAwards(int limit) {
        return awardDAO.getRecentAwards(limit);
    }

    /**
     * Get award by ID
     */
    public Award getAwardById(int awardId) {
        return awardDAO.getAwardById(awardId);
    }

    /**
     * Get award by tender ID
     */
    public Award getAwardByTenderId(int tenderId) {
        return awardDAO.getAwardByTenderId(tenderId);
    }

    /**
     * Check if tender has award
     */
    public boolean hasAwardForTender(int tenderId) {
        return awardDAO.hasAwardForTender(tenderId);
    }
}