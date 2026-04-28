
package com.ProcureGov.repository;

import com.ProcureGov.model.BidEvaluation;
import com.ProcureGov.model.EvaluatorStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BidEvaluationRepository extends BaseRepository {

    /**
     * Create a new bid evaluation
     */
    public void create(BidEvaluation evaluation) throws Exception {
        String sql = "INSERT INTO BidEvaluations (bid_id, tender_id, evaluator_id, price_score, technical_score, delivery_score, weighted_total) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, evaluation.getBidId());
            stmt.setInt(2, evaluation.getTenderId());
            stmt.setInt(3, evaluation.getEvaluatorId());
            stmt.setDouble(4, evaluation.getPriceScore());
            stmt.setDouble(5, evaluation.getTechnicalScore());
            stmt.setDouble(6, evaluation.getDeliveryScore());
            stmt.setDouble(7, evaluation.getWeightedTotal());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                evaluation.setEvaluationId(rs.getInt(1));
            }
        }
    }

    /**
     * updates only specific fields
     */
    public boolean updateScores(int evaluationId, double priceScore,
                                double technicalScore, double deliveryScore,
                                double weightedTotal) throws Exception {
        String sql = "UPDATE BidEvaluations SET " +
                "price_score = ?, " +
                "technical_score = ?, " +
                "delivery_score = ?, " +
                "weighted_total = ?, " +
                "evaluated_at = CURRENT_TIMESTAMP " +
                "WHERE evaluation_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, priceScore);
            stmt.setDouble(2, technicalScore);
            stmt.setDouble(3, deliveryScore);
            stmt.setDouble(4, weightedTotal);
            stmt.setInt(5, evaluationId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        }
    }

    /**
     * Check if an evaluator has already evaluated a specific bid
     */
    public boolean hasEvaluatorEvaluatedBid(int evaluatorId, int bidId) throws Exception{
        String sql = "SELECT COUNT(*) FROM BidEvaluations WHERE evaluator_id = ? AND bid_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, evaluatorId);
            stmt.setInt(2, bidId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        }
        return false;
    }

    /**
     * Get evaluations for a specific bid (for current user)
     */
    public BidEvaluation getEvaluationByBidAndEvaluator(int bidId, int evaluatorId) throws Exception {
        String sql = "SELECT * FROM BidEvaluations WHERE bid_id = ? AND evaluator_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bidId);
            stmt.setInt(2, evaluatorId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvaluation(rs);
                }
            }

        }
        return null;
    }

    /**
     * Get all evaluations for a specific bid (for averaging)
     */
    public List<BidEvaluation> getEvaluationsByBid(int bidId) throws Exception{
        List<BidEvaluation> evaluations = new ArrayList<>();
        String sql = "SELECT be.*, e.full_names as evaluator_name " +
                "FROM BidEvaluations be " +
                "JOIN Employees e ON be.evaluator_id = e.employee_id " +
                "WHERE be.bid_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bidId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BidEvaluation evaluation = mapResultSetToEvaluation(rs);
                    evaluation.setEvaluatorName(rs.getString("evaluator_name"));
                    evaluations.add(evaluation);
                }
            }
        }
        return evaluations;
    }

    /**
     * Get the lowest bid amount for a tender
     */
    public double getLowestBidAmount(int tenderId) throws Exception {
        String sql = "SELECT MIN(price) FROM TenderBids WHERE tender_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tenderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }

        }
        return 0;
    }

    /**
     * Get the shortest delivery timeline for a tender
     */
    public int getShortestDeliveryDays(int tenderId) throws Exception{
        String sql = "SELECT MIN(delivery_days) FROM TenderBids WHERE tender_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tenderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Get all evaluators for a tender
     */
    public List<EvaluatorStatus> getEvaluatorsForTender(int tenderId) throws Exception {
        List<EvaluatorStatus> evaluators = new ArrayList<>();
        String sql = "SELECT e.employee_id, e.full_names, " +
                "CASE WHEN be.evaluation_id IS NOT NULL THEN TRUE ELSE FALSE END as has_evaluated " +
                "FROM view_employee_data e " +
                "LEFT JOIN BidEvaluations be ON e.employee_id = be.evaluator_id " +
                "AND be.tender_id = ? " +
                "WHERE e.role_name IN ('PROCUREMENT_OFFICER', 'BOARD_MEMBER') " +
                "AND e.active_status = TRUE";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tenderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EvaluatorStatus evaluator = new EvaluatorStatus();
                    evaluator.setEvaluatorId(rs.getInt("employee_id"));
                    evaluator.setName(rs.getString("full_names"));
                    evaluator.setHasEvaluated(rs.getBoolean("has_evaluated"));
                    evaluators.add(evaluator);
                }
            }
        }
        return evaluators;
    }

    /**
     * Check if all evaluators have evaluated all bids for a tender
     */
    public boolean haveAllEvaluatorsCompleted(int tenderId) throws  Exception{
        String sql = "SELECT COUNT(DISTINCT e.employee_id) as total_evaluators, " +
                "COUNT(DISTINCT be.evaluator_id) as completed_evaluators " +
                "FROM view_employee_data e " +
                "LEFT JOIN BidEvaluations be ON e.employee_id = be.evaluator_id " +
                "AND be.tender_id = ? " +
                "WHERE e.role_name IN ('PROCUREMENT_OFFICER', 'BOARD_MEMBER') " +
                "AND e.active_status = TRUE";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tenderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int totalEvaluators = rs.getInt("total_evaluators");

                    // Check if each evaluator has evaluated ALL bids
                    String checkAllBidsSql = "SELECT be.evaluator_id " +
                            "FROM BidEvaluations be " +
                            "WHERE be.tender_id = ? " +
                            "GROUP BY be.evaluator_id " +
                            "HAVING COUNT(DISTINCT be.bid_id) = (SELECT COUNT(*) FROM TenderBids WHERE tender_id = ?)";

                    try (PreparedStatement checkStmt = conn.prepareStatement(checkAllBidsSql)) {
                        checkStmt.setInt(1, tenderId);
                        checkStmt.setInt(2, tenderId);

                        try (ResultSet checkRs = checkStmt.executeQuery()) {
                            int fullyCompleted = 0;
                            while (checkRs.next()) {
                                fullyCompleted++;
                            }
                            return fullyCompleted == totalEvaluators;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Get count of completed evaluations for a bid
     */
    public int getCompletedEvaluationsCount(int bidId) throws Exception {
        String sql = "SELECT COUNT(*) FROM BidEvaluations WHERE bid_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bidId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Get total number of evaluators for a tender
     */
    public int getTotalEvaluatorsCount() throws Exception{
        String sql = "SELECT COUNT(*) FROM view_employee_data " +
                "WHERE role_name IN ('PROCUREMENT_OFFICER', 'BOARD_MEMBER') " +
                "AND active_status = TRUE";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Calculate final score for a bid
     */
    public double calculateFinalScore(int bidId) throws Exception {
        String sql = "SELECT AVG(weighted_total) FROM BidEvaluations WHERE bid_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bidId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0;
    }

    public List<BidEvaluation> getBidEvaluationsForBid(int bidId) throws Exception {
        List<BidEvaluation> bidEvaluations = new ArrayList<>();
        String sql = "SELECT * FROM BidEvaluations WHERE bid_id = ?";

        try(Connection conn = getDataSource().getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);){
            stmt.setInt(1, bidId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bidEvaluations.add(mapResultSetToEvaluation(rs));
                }
            }
        }
        return bidEvaluations;
    }

    private BidEvaluation mapResultSetToEvaluation(ResultSet rs) throws SQLException {
        BidEvaluation evaluation = new BidEvaluation();
        evaluation.setEvaluationId(rs.getInt("evaluation_id"));
        evaluation.setBidId(rs.getInt("bid_id"));
        evaluation.setTenderId(rs.getInt("tender_id"));
        evaluation.setEvaluatorId(rs.getInt("evaluator_id"));

        // Handle potential NULL values
        double priceScore = rs.getDouble("price_score");
        evaluation.setPriceScore(rs.wasNull() ? 0.0 : priceScore);

        double technicalScore = rs.getDouble("technical_score");
        evaluation.setTechnicalScore(rs.wasNull() ? 0.0 : technicalScore);

        double deliveryScore = rs.getDouble("delivery_score");
        evaluation.setDeliveryScore(rs.wasNull() ? 0.0 : deliveryScore);

        double weightedTotal = rs.getDouble("weighted_total");
        evaluation.setWeightedTotal(rs.wasNull() ? 0.0 : weightedTotal);

        evaluation.setEvaluatedAt(rs.getTimestamp("evaluated_at"));
        return evaluation;
    }

    public int getCompletedEvaluationCount() throws Exception{
        String sql = "SELECT COUNT(*) FROM TENDEROFFERS WHERE status = 'EVALUATED'";

        try (Connection conn = getDataSource().getConnection();
        Statement stmt = conn.createStatement()){
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}