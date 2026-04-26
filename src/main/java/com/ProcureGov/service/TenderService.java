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

    public TenderStatsDTO getTenderStats() {
        return tenderRepository.getTenderStats();
    }

    public List<TenderOffer> getOpenTenders(int limit) {
        return tenderRepository.findOpenTenders(limit);
    }

    public List<TenderOffer> getAllOpenTenders() {
        return tenderRepository.findAllOpenTenders();
    }

    public List<TenderOffer> getAllTenders() {
        return tenderRepository.findAll();
    }

    public List<TenderOffer> getFilteredTenders(String status, String category) {
        return tenderRepository.findFilteredTenders(status, category);
    }

    public List<String> getAllCategories() {
        return tenderRepository.findAllCategories();
    }

    public TenderOffer getTenderById(int tenderId) {
        return tenderRepository.findById(tenderId);
    }

    public void createTender(TenderOffer tender) throws Exception {
        tenderRepository.create(tender);
    }

    public void updateTenderStatus(int tenderId, String status) throws Exception {
        tenderRepository.updateStatus(tenderId, status);
    }

    public List<TenderOffer> getOpenTendersExcludingDrafts(int limit) {
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

    public List<TenderOffer> getAllOpenTendersExcludingDrafts() {
        return tenderRepository.findAllOpenTendersExcludingDrafts();
    }

    public List<TenderOffer> getAllTendersExcludingDrafts() {
        return tenderRepository.findAllExcludingDrafts();
    }

    public List<TenderOffer> getFilteredTendersExcludingDrafts(String status, String category) {
        return tenderRepository.findFilteredTendersExcludingDrafts(status, category);
    }

    public boolean canEditTender(int tenderId) throws Exception {
        TenderOffer tender = tenderRepository.findById(tenderId);
        return tender != null && "DRAFT".equals(tender.getStatus());
    }

    public List<CategoryStatsDTO> getCategoryStats() {
        return tenderRepository.getTenderStatsByCategory();
    }

    public int getDraftCount() {
        try {
            return tenderRepository.getDraftCount();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<TenderOffer> getTendersClosingWithin(int hours) {
        try {
            return tenderRepository.findTendersClosingWithin(hours);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public int getPendingEvaluationCount() {
        try {
            return tenderRepository.getPendingEvaluationCount();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getStaleDraftCount(int days) {
        try {
            return tenderRepository.getStaleDraftCount(days);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<TenderOffer> getDraftTenders() {
        try {
            return tenderRepository.findDraftTenders();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}