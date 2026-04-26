package com.ProcureGov.backgroundtasks;


import com.ProcureGov.repository.TenderOfferRepository;

import javax.sql.DataSource;
import java.sql.*;

import static com.ProcureGov.repository.BaseRepository.getDataSource;
import static java.lang.IO.print;
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

        String sql = "SELECT tender_id FROM TenderOffers " +
                "WHERE expiry_datetime <= NOW() " +
                "AND status = 'OPEN' " +
                "ORDER BY publish_datetime DESC";

        int closedCount = 0;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                ResultSet rs = stmt.executeQuery();

                // Process all expired tenders in a batch for efficiency
                StringBuilder batchIds = new StringBuilder();

                while (rs.next()) {
                    int tenderId = rs.getInt("tender_id");
                    tenderOfferRepository.updateStatus(tenderId, "CLOSED");
                    closedCount ++;

                    // Log progress
                    if (closedCount % 2 == 0) {
                        System.out.println("Closed " + closedCount + " tenders so far...");
                    }
                }

                conn.commit(); // Commit transaction
                System.out.println("Successfully closed " + closedCount + " expired tenders");

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Error closing expired tenders: " + e.getMessage());
            e.printStackTrace();
            //TODO : add proper logging
        } catch (Exception e) {
            System.err.println("Error closing expired tenders: " + e.getMessage());
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
}
