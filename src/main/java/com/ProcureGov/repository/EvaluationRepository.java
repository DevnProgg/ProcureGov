
package com.ProcureGov.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EvaluationRepository extends BaseRepository {

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