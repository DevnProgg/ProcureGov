package com.ProcureGov.backgroundtasks;


import com.ProcureGov.repository.TenderOfferRepository;

import javax.sql.DataSource;
import java.sql.*;

import static java.lang.IO.println;

public class TenderStatusManager {
    private static DataSource dataSource;
    private static TenderOfferRepository tenderOfferRepository;

    // Initialize once
    public static void init(DataSource ds) {
        dataSource = ds;
        tenderOfferRepository = new TenderOfferRepository();
    }

    //close expired tenders
    public static void closeExpiredTenders() {
        System.out.println("Starting to close expired tenders...");

        String selectSql = "SELECT tender_id FROM TenderOffers " +
                "WHERE expiry_datetime <= ? " +
                "AND status = 'OPEN' " +
                "ORDER BY publish_datetime DESC";

        int closedCount = 0;

        // Use a single consistent timestamp
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement selectStmt = conn.prepareStatement(selectSql)
            ) {

                selectStmt.setTimestamp(1, now);

                try (ResultSet rs = selectStmt.executeQuery()) {

                    while (rs.next()) {
                        int tenderId = rs.getInt("tender_id");

                        int updated = updateStatusIfExpired(
                                conn, tenderId, now
                        );

                        if (updated > 0) {
                            closedCount++;

                            if (closedCount % 2 == 0) {
                                System.out.println("Closed " + closedCount + " tenders so far...");
                            }
                        } else {
                            System.out.println("Skipped tender " + tenderId +
                                    " (no longer eligible for closing)");
                        }
                    }
                }

                conn.commit();
                System.out.println("Successfully closed " + closedCount + " expired tenders");

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Error closing expired tenders: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //place under evaluation
    public static void placeTenderUnderEvaluation(int tenderId){
        println("Placing tender under evaluation...");
        try {
            tenderOfferRepository.updateStatus(tenderId, "UNDER_EVALUATION");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //mark evaluated
    public static void placeTenderEvaluated(int TenderID){
        println("Placing tender evaluated...");
        try{
            tenderOfferRepository.updateStatus(TenderID, "EVALUATED");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //mark tender as awarded
    public static void placeTenderCompleted(int TenderID){
        println("Placing tender completed...");
        try{
            tenderOfferRepository.updateStatus(TenderID, "AWARDED");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int updateStatusIfExpired(Connection conn, int tenderId, Timestamp now) throws SQLException {
        String sql = "UPDATE TenderOffers " +
                "SET status = ? " +
                "WHERE tender_id = ? " +
                "AND expiry_datetime <= ? " +
                "AND status = 'OPEN'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "CLOSED");
            stmt.setInt(2, tenderId);
            stmt.setTimestamp(3, now);

            return stmt.executeUpdate();
        }
    }

}
