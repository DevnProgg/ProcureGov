package com.ProcureGov.service;

import com.ProcureGov.backgroundtasks.TenderStatusManager;
import com.ProcureGov.dto.AwardDTO;
import com.ProcureGov.model.*;
import com.ProcureGov.repository.AwardRepository;
import com.ProcureGov.repository.MailMessageQueueRepository;
import com.ProcureGov.util.EmailUtility;

import java.util.List;

public class AwardService {

    private final AwardRepository awardDAO;
    private final EmailUtility emailUtility;
    private final MailMessageQueueRepository mailMessageQueueRepository;

    public AwardService() {
        this.awardDAO = new AwardRepository();
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

        // Create the award in the database
        boolean awardCreated = awardDAO.createAward(award);

        if (awardCreated) {
            // Send email notification to the supplier
            TenderStatusManager.placeTenderCompleted(award.getTender_id());
            sendAwardNotificationEmail(award);
        }

        return awardCreated;
    }

    /**
     * Send email notification to supplier about the award
     */
    private void sendAwardNotificationEmail(Award award) throws Exception{
            SupplierData supplier = getSupplierByBidId(award.getBid_id());
            TenderOffer tender = getTenderById(award.getTender_id());

            if (supplier == null || tender == null) {
                return;
            }

            // Validate supplier email
            String supplierEmail = supplier.getEmail();
            if (supplierEmail == null || supplierEmail.trim().isEmpty()) {
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

            mailMessageQueueRepository.Enqueue(supplierEmail, subject, emailBody);
    }

    private SupplierData getSupplierByBidId(int bidId) throws Exception{
        return awardDAO.getSupplierByBidId(bidId);
    }

    private TenderOffer getTenderById(int tenderId) throws Exception{
        return awardDAO.getTenderById(tenderId);
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