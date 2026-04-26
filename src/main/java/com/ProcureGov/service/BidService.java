package com.ProcureGov.service;

import com.ProcureGov.dto.BidDetailDTO;
import com.ProcureGov.dto.BidSummaryDTO;
import com.ProcureGov.dto.BidStatsDTO;
import com.ProcureGov.model.TenderBid;
import com.ProcureGov.repository.TenderBidRepository;


import java.util.List;

public class BidService {

    private final TenderBidRepository bidRepository;

    public BidService() {
        this.bidRepository = new TenderBidRepository();
    }

    public List<BidSummaryDTO> getSupplierBids(int supplierId) {
        return bidRepository.findBidsBySupplierId(supplierId);
    }


    public int getTotalBidCount() {
        return bidRepository.getTotalBidCount();
    }

    public int getBidCountForCurrentMonth() {
        return bidRepository.getBidCountForCurrentMonth();
    }

    public int getBidCountForLastMonth() {
        return bidRepository.getBidCountForLastMonth();
    }

    public int getBidCountForTender(int tenderId) {
        return bidRepository.getBidCountForTender(tenderId);
    }

    public boolean hasSupplierBidOnTender(int supplierId, int tenderId) {
        return bidRepository.hasSupplierBidOnTender(supplierId, tenderId);
    }

    public boolean submitBid(TenderBid bid) {
        return bidRepository.save(bid);
    }


    /**
     * Get all bids for a specific tender (for evaluation purposes)
     */
    public List<TenderBid> getBidsForTender(int tenderId) {
        return bidRepository.findByTenderId(tenderId);
    }

    /**
     * Get detailed bid information with supplier details
     */
    public List<BidDetailDTO> getDetailedBidsForTender(int tenderId) {
        return bidRepository.findDetailedBidsByTenderId(tenderId);
    }

    /**
     * Get a specific bid by ID
     */
    public TenderBid getBidById(int bidId) {
        return bidRepository.findById(bidId);
    }

    /**
     * Update bid information
     */
    public boolean updateBid(TenderBid bid) {
        return bidRepository.update(bid);
    }

    /**
     * Withdraw/delete a bid
     */
    public boolean withdrawBid(int bidId, int supplierId) {
        return bidRepository.delete(bidId, supplierId);
    }

    /**
     * Get bid statistics for a supplier
     */
    public BidStatsDTO getSupplierBidStats(int supplierId) {
        return bidRepository.getBidStatsBySupplier(supplierId);
    }
}