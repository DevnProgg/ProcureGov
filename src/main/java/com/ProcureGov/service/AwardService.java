package com.ProcureGov.service;

import com.ProcureGov.dto.AwardDTO;
import com.ProcureGov.model.*;
import com.ProcureGov.repository.AwardRepository;
import com.ProcureGov.repository.MailMessageQueueRepository;
import com.ProcureGov.repository.TenderOfferRepository;
import com.ProcureGov.util.EmailUtility;

import java.sql.Connection;
import java.util.List;

public class AwardService {

    private final AwardRepository awardDAO;
    private final TenderOfferRepository tenderOfferRepository;
    private final EmailUtility emailUtility;
    private final MailMessageQueueRepository mailMessageQueueRepository;

    public AwardService() {
        this.awardDAO = new AwardRepository();
        this.tenderOfferRepository = new TenderOfferRepository();
        this.mailMessageQueueRepository = new MailMessageQueueRepository();
        this.emailUtility = new EmailUtility();
    }

    /**
     * Create a new award and send email notification to supplier
     */
    public boolean createAward(Award award) throws Exception{
        // Validate award data
        if (award.getTender_id() <= 0 || award.getBid_id() <= 0 || award.getAwarded_by() <= 0) {
            return false;
        }

        // Check if award already exists for this tender
        if (awardDAO.hasAwardForTender(award.getTender_id())) {
            return false;
        }

        try (Connection conn = awardDAO.getConnection()) {
            conn.setAutoCommit(false);

            try {
                boolean awardCreated = awardDAO.createAward(conn, award);
                if (!awardCreated) {
                    conn.rollback();
                    return false;
                }

                tenderOfferRepository.updateStatus(conn, award.getTender_id(), "AWARDED");
                sendAwardNotificationEmail(conn, award);

                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Send email notification to supplier about the award
     */
    private void sendAwardNotificationEmail(Connection conn, Award award) throws Exception{
            SupplierData supplier = getSupplierByBidId(conn, award.getBid_id());
            TenderOffer tender = getTenderById(conn, award.getTender_id());

            if (supplier == null) {
                throw new Exception("Unable to load supplier details for award notification");
            }
            if (tender == null) {
                throw new Exception("Unable to load tender details for award notification");
            }

            // Validate supplier email
            String supplierEmail = supplier.getEmail();
            if (supplierEmail == null || supplierEmail.trim().isEmpty()) {
                throw new Exception("Supplier email is missing for award notification");
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

            mailMessageQueueRepository.Enqueue(conn, supplierEmail, subject, emailBody);
    }

    private SupplierData getSupplierByBidId(Connection conn, int bidId) throws Exception{
        return awardDAO.getSupplierByBidId(conn, bidId);
    }

    private TenderOffer getTenderById(Connection conn, int tenderId) throws Exception{
        return awardDAO.getTenderById(conn, tenderId);
    }

    /**
     * Get all awards (public view)
     */
    public List<AwardDTO> getAllAwards() throws Exception{
        return awardDAO.getAllAwards();
    }

    /**
     * Get awards by supplier ID
     */
    public List<AwardDTO> getAwardsBySupplierId(int supplierId) throws Exception{
        return awardDAO.getAwardsBySupplierId(supplierId);
    }

    /**
     * Get recent awards for dashboard
     */
    public List<AwardDTO> getRecentAwards(int limit) throws Exception{
        return awardDAO.getRecentAwards(limit);
    }

    /**
     * Check if tender has award
     */
    public boolean hasAwardForTender(int tenderId) throws Exception{
        return awardDAO.hasAwardForTender(tenderId);
    }
}