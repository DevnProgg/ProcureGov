
package com.ProcureGov.repository;

import com.ProcureGov.model.EvaluatorBidLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EvaluationRepository extends BaseRepository {

    /*
    Create a new bid evaluation log
     */
    public void create(EvaluatorBidLog log) throws Exception {
        String sql = "INSERT INTO EvaluatorBidLogs (bid_id, employee_id, price_score, technical_compliance_score, delivery_timeline_score, weighted_total) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try(Connection conn = getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);){
            stmt.setInt(1, log.getBid_id());
            stmt.setInt(2, log.getEmployee_id());
            stmt.setDouble(3, log.getPrice_score());
            stmt.setDouble(4, log.getTechnical_compliance_score());
            stmt.setDouble(5, log.getDelivery_timeline_score());
            stmt.setDouble(6, log.getWeighted_total());

            stmt.executeUpdate();
        }
    }

    public int getActiveEvaluationCount() throws Exception{
        String sql = "SELECT COUNT(DISTINCT tender_id) FROM tenderoffers WHERE status = 'UNDER_EVALUATION'";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        }
        return 0;
    }

    public int getCompletedEvaluationCount() throws  Exception {
        String sql = "SELECT COUNT(DISTINCT tender_id) FROM procure_gov.tenderoffers WHERE status = 'EVALUATED'";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public double getAverageEvaluationScore() throws Exception {
        String sql = "SELECT AVG(weighted_total) FROM evaluatorbidlogs";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
}