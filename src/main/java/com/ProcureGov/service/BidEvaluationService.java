
package com.ProcureGov.service;

import com.ProcureGov.model.*;
        import com.ProcureGov.repository.BidEvaluationRepository;
import com.ProcureGov.repository.EvaluationRepository;
import com.ProcureGov.repository.TenderBidRepository;
import com.ProcureGov.repository.TenderOfferRepository;
import java.util.List;

public class BidEvaluationService {

    private final BidEvaluationRepository evaluationRepository;
    private final TenderOfferRepository tenderRepository;
    private final EvaluationRepository evaluationRepositorylog;

    public BidEvaluationService() {
        this.evaluationRepository = new BidEvaluationRepository();
        this.tenderRepository = new TenderOfferRepository();
        this.evaluationRepositorylog = new EvaluationRepository();
    }

    /**
     * Get the current user's evaluation for a specific bid (convenience method)
     */
    public BidEvaluation getMyEvaluation(int bidId, int evaluatorId) throws Exception {
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
    public double calculateWeightedTotal(double priceScore, double technicalScore, double deliveryScore) throws Exception {
        validateScore(priceScore, "Price score");
        validateScore(technicalScore, "Technical score");
        validateScore(deliveryScore, "Delivery score");

        return (priceScore * 0.40) + (technicalScore * 0.35) + (deliveryScore * 0.25);
    }

    /**
     * Calculate final score as average of all evaluators' weighted totals
     */
    public double calculateFinalScore(int bidId) throws Exception {
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

        //check is there is an evaluation already started in the database
        List<BidEvaluation> bid = evaluationRepository.getEvaluationsByBid(bidId);

        if (bid.isEmpty()) {
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
        } else {
           BidEvaluation evaluation =  bid.getFirst();
            //update record
            if (!evaluationRepository.updateScores(evaluation.getEvaluationId(), priceScore,technicalScore, deliveryScore,  weightedTotal)){
                throw new IllegalStateException("Something went wrong in updating the score");
            }
        }

        //write the evaluation logs to the database
        EvaluatorBidLog log = new EvaluatorBidLog();
        log.setBid_id(bidId);
        log.setEmployee_id(evaluatorId);
        log.setPrice_score(priceScore);
        log.setTechnical_compliance_score(technicalScore);
        log.setDelivery_timeline_score(deliveryScore);
        log.setWeighted_total(weightedTotal);
        evaluationRepositorylog.create(log);

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
    public BidScoreSummary getBidScoreSummary(int bidId, int tenderId, int evaluatorId) throws Exception {
        BidScoreSummary summary = new BidScoreSummary();
        summary.setBidId(bidId);

        // Get reference values
        double lowestBidAmount = evaluationRepository.getLowestBidAmount(tenderId);
        int shortestDeliveryDays = evaluationRepository.getShortestDeliveryDays(tenderId);

        // Set auto-calculated scores
        // You'll need to get bid details from BidRepository
        TenderBidRepository tenderBid = new TenderBidRepository();
        TenderBid bid = tenderBid.findById(bidId);
        summary.setBidAmount(bid.getPrice());
        summary.setDeliveryDays(bid.getDelivery_days());

        summary.setLowestBidAmount(lowestBidAmount);
        summary.setShortestDeliveryDays(shortestDeliveryDays);

        // Calculate auto scores based on actual bid values
        summary.setPriceScore(calculatePriceScore(bid.getPrice(), lowestBidAmount));
        summary.setDeliveryScore(calculateDeliveryScore(bid.getDelivery_days(), shortestDeliveryDays));

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
        summary.setTotalEvaluators(evaluationRepository.getTotalEvaluatorsCount());

        // Calculate final score if all evaluators completed
        if (summary.getEvaluationsCompleted() == summary.getTotalEvaluators()) {
            summary.setFinalScore(calculateFinalScore(bidId));
        }

        return summary;
    }

    /**
     * Get all evaluators' status for a tender
     */
    public List<EvaluatorStatus> getEvaluatorsStatus(int tenderId) throws Exception {
        return evaluationRepository.getEvaluatorsForTender(tenderId);
    }

    private void validateScore(double score, String scoreName) throws Exception {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException(scoreName + " must be between 0 and 100");
        }
    }

    /**
     * Check if user has already evaluated a bid
     */
    public boolean hasUserEvaluatedBid(int evaluatorId, int bidId) throws Exception {
        return evaluationRepository.hasEvaluatorEvaluatedBid(evaluatorId, bidId);
    }


    /**
     * Get completed evaluation count
     */
    public int getCompletedEvaluationCount() throws Exception {
        return evaluationRepository.getCompletedEvaluationCount();
    }

    /**
     * Get completed evaluation count for a specific bid
     */
    public int getCompletedEvaluationCount(int bidId) throws Exception {
        return evaluationRepository.getCompletedEvaluationsCount(bidId);
    }

    /**
     * Get total evaluators count
     */
    public int getTotalEvaluatorsCount() throws Exception {
        return evaluationRepository.getTotalEvaluatorsCount();
    }

    /**
     * Get average technical score for a bid
     */
    public double getAverageTechnicalScore(int bidId) throws Exception {
        List<BidEvaluation> evaluations = evaluationRepository.getEvaluationsByBid(bidId);
        if (evaluations.isEmpty()) return 0;

        double sum = evaluations.stream()
                .mapToDouble(BidEvaluation::getTechnicalScore)
                .sum();
        return sum / evaluations.size();
    }

}