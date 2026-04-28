package com.ProcureGov.service;

import com.ProcureGov.dto.CategoryStatsDTO;
import com.ProcureGov.dto.TenderStatsDTO;
import com.ProcureGov.model.TenderOffer;
import com.ProcureGov.repository.TenderOfferRepository;

import java.util.ArrayList;
import java.util.List;

public class TenderService {

    private final TenderOfferRepository tenderRepository;

    public TenderService() {
        this.tenderRepository = new TenderOfferRepository();
    }

    public TenderStatsDTO getTenderStats() throws Exception {
        return tenderRepository.getTenderStats();
    }

    public List<TenderOffer> getOpenTenders(int limit) throws Exception {
        return tenderRepository.findOpenTenders(limit);
    }

    public List<TenderOffer> getTendersByStatus(String status) throws Exception {
        return tenderRepository.getTendersByStatus(status);
    }

    public List<TenderOffer> getAllOpenTenders() throws Exception {
        return tenderRepository.findAllOpenTenders();
    }

    public List<TenderOffer> getAllTenders() throws Exception {
        return tenderRepository.findAll();
    }

    public List<TenderOffer> getFilteredTenders(String status, String category) throws Exception {
        return tenderRepository.findFilteredTenders(status, category);
    }

    public List<String> getAllCategories() throws Exception {
        return tenderRepository.findAllCategories();
    }

    public TenderOffer getTenderById(int tenderId) throws Exception {
        return tenderRepository.findById(tenderId);
    }

    public void createTender(TenderOffer tender) throws Exception {
        tenderRepository.create(tender);
    }

    public void updateTenderStatus(int tenderId, String status) throws Exception {
        tenderRepository.updateStatus(tenderId, status);
    }

    public List<TenderOffer> getOpenTendersExcludingDrafts(int limit) throws Exception {
        return tenderRepository.findOpenTendersExcludingDrafts(limit);
    }

    public void updateTender(TenderOffer tender) throws Exception {
        // Validate tender exists
        TenderOffer existing = tenderRepository.findById(tender.getTender_id());
        if (existing == null) {
            throw new IllegalArgumentException("Tender not found");
        }

        // Validate status (can only update DRAFT tenders unless publishing)
        if (!"DRAFT".equals(existing.getStatus())) {
            throw new IllegalArgumentException("Only draft tenders can be updated");
        }

        // Update the tender
        tenderRepository.update(tender);
    }

    public boolean canEditTender(int tenderId, int userId) throws Exception {
        TenderOffer tender = tenderRepository.findById(tenderId);
        return tender != null &&
                "DRAFT".equals(tender.getStatus()) &&
                tender.getCreated_by() == userId;
    }

    public List<TenderOffer> getAllOpenTendersExcludingDrafts() throws Exception {
        return tenderRepository.findAllOpenTendersExcludingDrafts();
    }

    public List<TenderOffer> getAllTendersExcludingDrafts() throws Exception {
        return tenderRepository.findAllExcludingDrafts();
    }

    public List<TenderOffer> getFilteredTendersExcludingDrafts(String status, String category) throws Exception {
        return tenderRepository.findFilteredTendersExcludingDrafts(status, category);
    }

    public boolean canEditTender(int tenderId) throws Exception {
        TenderOffer tender = tenderRepository.findById(tenderId);
        return tender != null && "DRAFT".equals(tender.getStatus());
    }

    public List<CategoryStatsDTO> getCategoryStats() throws Exception {
        return tenderRepository.getTenderStatsByCategory();
    }

    public List<TenderOffer> getTendersClosingWithin(int hours) throws Exception {
            return tenderRepository.findTendersClosingWithin(hours);
    }

    public int getPendingEvaluationCount() throws Exception {
            return tenderRepository.getPendingEvaluationCount();
    }

    public int getStaleDraftCount(int days) throws  Exception {
            return tenderRepository.getStaleDraftCount(days);
        }

    public List<TenderOffer> getDraftTenders() throws Exception {
            return tenderRepository.findDraftTenders();
    }

}