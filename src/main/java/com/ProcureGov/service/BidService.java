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

    public List<BidSummaryDTO> getSupplierBids(int supplierId) throws Exception {
        return bidRepository.findBidsBySupplierId(supplierId);
    }


    public int getTotalBidCount() throws Exception {
        return bidRepository.getTotalBidCount();
    }

    public int getBidCountForCurrentMonth() throws Exception {
        return bidRepository.getBidCountForCurrentMonth();
    }

    public int getBidCountForLastMonth() throws Exception {
        return bidRepository.getBidCountForLastMonth();
    }

    public int getBidCountForTender(int tenderId) throws Exception {
        return bidRepository.getBidCountForTender(tenderId);
    }

    public boolean hasSupplierBidOnTender(int supplierId, int tenderId) throws Exception {
        return bidRepository.hasSupplierBidOnTender(supplierId, tenderId);
    }

    public boolean submitBid(TenderBid bid) throws Exception {
        return bidRepository.save(bid);
    }


    /**
     * Get all bids for a specific tender (for evaluation purposes)
     */
    public List<TenderBid> getBidsForTender(int tenderId) throws Exception {
        return bidRepository.findByTenderId(tenderId);
    }

    /**
     * Get detailed bid information with supplier details
     */
    public List<BidDetailDTO> getDetailedBidsForTender(int tenderId) throws Exception {
        return bidRepository.findDetailedBidsByTenderId(tenderId);
    }

    /**
     * Get a specific bid by ID
     */
    public TenderBid getBidById(int bidId) throws Exception {
        return bidRepository.findById(bidId);
    }

    /**
     * Update bid information
     */
    public boolean updateBid(TenderBid bid) throws Exception {
        return bidRepository.update(bid);
    }

    /**
     * Withdraw/delete a bid
     */
    public boolean withdrawBid(int bidId, int supplierId) throws Exception {
        return bidRepository.delete(bidId, supplierId);
    }

    /**
     * Get bid statistics for a supplier
     */
    public BidStatsDTO getSupplierBidStats(int supplierId) throws Exception {
        return bidRepository.getBidStatsBySupplier(supplierId);
    }
}