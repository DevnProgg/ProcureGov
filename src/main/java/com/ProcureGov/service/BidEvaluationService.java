
package com.ProcureGov.service;

import com.ProcureGov.model.*;
        import com.ProcureGov.repository.BidEvaluationRepository;
import com.ProcureGov.repository.TenderOfferRepository;

import java.util.ArrayList;
import java.util.List;

public class BidEvaluationService {

    private final BidEvaluationRepository evaluationRepository;
    private final TenderOfferRepository tenderRepository;

    public BidEvaluationService() {
        this.evaluationRepository = new BidEvaluationRepository();
        this.tenderRepository = new TenderOfferRepository();
    }

    /**
     * Get the current user's evaluation for a specific bid (convenience method)
     */
    public BidEvaluation getMyEvaluation(int bidId, int evaluatorId) {
        return evaluationRepository.getEvaluationByBidAndEvaluator(bidId, evaluatorId);
    }

    /**
     * Calculate price score: (Lowest Bid / This Bid) × 100
     */
    public double calculatePriceScore(double bidAmount, double lowestBidAmount) {
        if (lowestBidAmount <= 0 || bidAmount <= 0) {
            return 0;
        }
        return (lowestBidAmount / bidAmount) * 100;
    }

    /**
     * Calculate delivery score: (Shortest Timeline / This Timeline) × 100
     */
    public double calculateDeliveryScore(int deliveryDays, int shortestDeliveryDays) {
        if (shortestDeliveryDays <= 0 || deliveryDays <= 0) {
            return 0;
        }
        return ((double) shortestDeliveryDays / deliveryDays) * 100;
    }

    /**
     * Calculate weighted total: (Price × 0.40) + (Technical × 0.35) + (Delivery × 0.25)
     */
    public double calculateWeightedTotal(double priceScore, double technicalScore, double deliveryScore) {
        validateScore(priceScore, "Price score");
        validateScore(technicalScore, "Technical score");
        validateScore(deliveryScore, "Delivery score");

        return (priceScore * 0.40) + (technicalScore * 0.35) + (deliveryScore * 0.25);
    }

    /**
     * Calculate final score as average of all evaluators' weighted totals
     */
    public double calculateFinalScore(int bidId) {
        return evaluationRepository.calculateFinalScore(bidId);
    }

    /**
     * Submit an evaluation score
     */
    public void submitEvaluation(int bidId, int tenderId, int evaluatorId,
                                 double technicalScore, double bidAmount, int deliveryDays) throws Exception {

        // Validate technical score
        validateScore(technicalScore, "Technical score");

        // Check if already evaluated
        if (evaluationRepository.hasEvaluatorEvaluatedBid(evaluatorId, bidId)) {
            throw new IllegalStateException("You have already evaluated this bid");
        }

        // Get reference values for calculations
        double lowestBidAmount = evaluationRepository.getLowestBidAmount(tenderId);
        int shortestDeliveryDays = evaluationRepository.getShortestDeliveryDays(tenderId);

        // Calculate auto scores
        double priceScore = calculatePriceScore(bidAmount, lowestBidAmount);
        double deliveryScore = calculateDeliveryScore(deliveryDays, shortestDeliveryDays);

        // Calculate weighted total
        double weightedTotal = calculateWeightedTotal(priceScore, technicalScore, deliveryScore);

        // Create evaluation
        BidEvaluation evaluation = new BidEvaluation();
        evaluation.setBidId(bidId);
        evaluation.setTenderId(tenderId);
        evaluation.setEvaluatorId(evaluatorId);
        evaluation.setPriceScore(priceScore);
        evaluation.setTechnicalScore(technicalScore);
        evaluation.setDeliveryScore(deliveryScore);
        evaluation.setWeightedTotal(weightedTotal);

        // Save to database
        evaluationRepository.create(evaluation);

        // Check if all evaluators have completed
        checkAndUpdateTenderStatus(tenderId);
    }

    /**
     * Check if all evaluators have completed and update tender status
     */
    private void checkAndUpdateTenderStatus(int tenderId) throws Exception {
        if (evaluationRepository.haveAllEvaluatorsCompleted(tenderId)) {
            tenderRepository.updateStatus(tenderId, "EVALUATED");
        }
    }

    /**
     * Get evaluation context for a bid
     */
    public BidScoreSummary getBidScoreSummary(int bidId, int tenderId, int evaluatorId) {
        BidScoreSummary summary = new BidScoreSummary();
        summary.setBidId(bidId);

        // Get reference values
        double lowestBidAmount = evaluationRepository.getLowestBidAmount(tenderId);
        int shortestDeliveryDays = evaluationRepository.getShortestDeliveryDays(tenderId);

        // Set auto-calculated scores
        // You'll need to get bid details from BidRepository
        TenderOfferRepository tenderRepo = new TenderOfferRepository();
        // Assuming you have a way to get bid details
        // summary.setBidAmount(bid.getBidAmount());
        // summary.setDeliveryDays(bid.getDeliveryDays());

        summary.setLowestBidAmount(lowestBidAmount);
        summary.setShortestDeliveryDays(shortestDeliveryDays);

        // Calculate auto scores based on actual bid values
        // summary.setPriceScore(calculatePriceScore(bid.getBidAmount(), lowestBidAmount));
        // summary.setDeliveryScore(calculateDeliveryScore(bid.getDeliveryDays(), shortestDeliveryDays));

        // Check if current user already evaluated
        boolean hasEvaluated = evaluationRepository.hasEvaluatorEvaluatedBid(evaluatorId, bidId);
        summary.setHasCurrentUserEvaluated(hasEvaluated);

        if (hasEvaluated) {
            BidEvaluation evaluation = evaluationRepository.getEvaluationByBidAndEvaluator(bidId, evaluatorId);
            if (evaluation != null) {
                summary.setTechnicalScore(evaluation.getTechnicalScore());
                summary.setWeightedTotal(evaluation.getWeightedTotal());
            }
        }

        // Get evaluation progress
        summary.setEvaluationsCompleted(evaluationRepository.getCompletedEvaluationsCount(bidId));
        summary.setTotalEvaluators(evaluationRepository.getTotalEvaluatorsCount(tenderId));

        // Calculate final score if all evaluators completed
        if (summary.getEvaluationsCompleted() == summary.getTotalEvaluators()) {
            summary.setFinalScore(calculateFinalScore(bidId));
        }

        return summary;
    }

    /**
     * Get all evaluators' status for a tender
     */
    public List<EvaluatorStatus> getEvaluatorsStatus(int tenderId) {
        return evaluationRepository.getEvaluatorsForTender(tenderId);
    }


    /**
     * Check if all evaluations are complete
     */
    public boolean areAllEvaluationsComplete(int tenderId) {
        return evaluationRepository.haveAllEvaluatorsCompleted(tenderId);
    }

    /**
     * Get the ranked list of bids for a tender
     */
    public List<BidScoreSummary> getRankedBids(int tenderId) {
        List<BidScoreSummary> rankedBids = new ArrayList<>();
        // Implementation to get all bids ranked by final score
        // This would join TenderBids with BidEvaluations and calculate averages
        return rankedBids;
    }

    private void validateScore(double score, String scoreName) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException(scoreName + " must be between 0 and 100");
        }
    }

    public int getActiveEvaluationCount() {
        // Implementation
        return 0;
    }

    public double getAverageEvaluationScore() {
        // Implementation
        return 0.0;
    }

    /**
     * Create a new evaluation
     */
    public void createEvaluation(BidEvaluation evaluation) throws Exception {
        evaluationRepository.create(evaluation);
    }

    /**
     * Check if user has already evaluated a bid
     */
    public boolean hasUserEvaluatedBid(int evaluatorId, int bidId) {
        return evaluationRepository.hasEvaluatorEvaluatedBid(evaluatorId, bidId);
    }


    /**
     * Get completed evaluation count
     */
    public int getCompletedEvaluationCount() {
        return evaluationRepository.getCompletedEvaluationCount();
    }

    /**
     * Get completed evaluation count for a specific bid
     */
    public int getCompletedEvaluationCount(int bidId) {
        return evaluationRepository.getCompletedEvaluationsCount(bidId);
    }

    /**
     * Get total evaluators count
     */
    public int getTotalEvaluatorsCount(int tenderId) {
        return evaluationRepository.getTotalEvaluatorsCount(tenderId);
    }

    /**
     * Get average technical score for a bid
     */
    public double getAverageTechnicalScore(int bidId) {
        List<BidEvaluation> evaluations = evaluationRepository.getEvaluationsByBid(bidId);
        if (evaluations.isEmpty()) return 0;

        double sum = evaluations.stream()
                .mapToDouble(BidEvaluation::getTechnicalScore)
                .sum();
        return sum / evaluations.size();
    }

    /**
     * Check if all evaluators have completed for a tender
     */
    public boolean haveAllEvaluatorsCompletedForTender(int tenderId) {
        return evaluationRepository.haveAllEvaluatorsCompleted(tenderId);
    }


}