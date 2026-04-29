package com.ProcureGov.repository;


import com.ProcureGov.dto.BidDetailDTO;
import com.ProcureGov.dto.BidStatsDTO;
import com.ProcureGov.dto.BidSummaryDTO;
import com.ProcureGov.model.TenderBid;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TenderBidRepository extends BaseRepository {

    /**
     * Get total count of all bids
     */
    public int getTotalBidCount() throws Exception {
        String sql = "SELECT COUNT(*) FROM TenderBids";

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
     * Get bid count for current month
     */
    public int getBidCountForCurrentMonth() throws Exception {
        String sql = "SELECT COUNT(*) FROM TenderBids " +
                "WHERE MONTH(submitted_at) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(submitted_at) = YEAR(CURRENT_DATE())";

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
     * Get bid count for last month
     */
    public int getBidCountForLastMonth() throws Exception {
        String sql = "SELECT COUNT(*) FROM TenderBids " +
                "WHERE MONTH(submitted_at) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH)) " +
                "AND YEAR(submitted_at) = YEAR(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH))";

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
     * Get bid count for a specific tender
     */
    public int getBidCountForTender(int tenderId) throws Exception {
        String sql = "SELECT COUNT(*) FROM TenderBids WHERE tender_id = ?";

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
     * Check if a supplier has already bid on a specific tender
     */
    public boolean hasSupplierBidOnTender(int supplierId, int tenderId) throws Exception {
        String sql = "SELECT COUNT(*) FROM TenderBids WHERE supplier_id = ? AND tender_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplierId);
            stmt.setInt(2, tenderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Find all bids for a specific tender
     */
    public List<TenderBid> findByTenderId(int tenderId) throws Exception{
        List<TenderBid> bids = new ArrayList<>();

        String sql = "SELECT * FROM TenderBids WHERE tender_id = ? ORDER BY submitted_at DESC";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tenderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bids.add(mapResultSetToTenderBid(rs));
                }
            }

        }

        return bids;
    }

    /**
     * Find detailed bids for a tender including supplier information
     */
    public List<BidDetailDTO> findDetailedBidsByTenderId(int tenderId) throws Exception{
        List<BidDetailDTO> bids = new ArrayList<>();

        String sql = "SELECT " +
                "    b.bid_id, " +
                "    b.tender_id, " +
                "    b.supplier_id, " +
                "    b.delivery_days, " +
                "    b.compliance_statement, " +
                "    b.document_file_path, " +
                "    b.submitted_at, " +
                "    s.business_name, " +
                "    s.reg_number, " +
                "    s.email, " +
                "    s.phone_number, " +
                "    e.weighted_total AS evaluation_score, " +
                "    e.price_score," +
                "    e.delivery_score," +
                "    e.technical_score," +
                "    b.price," +
                "IF(a.award_id IS NOT NULL, true, false) AS is_awarded " +
                "FROM TenderBids b " +
                "INNER JOIN Suppliers s ON b.supplier_id = s.supplier_id " +
                "LEFT JOIN bidevaluations e ON b.bid_id = e.bid_id " +
                "LEFT JOIN Awards a ON b.bid_id = a.bid_id " +
                "WHERE b.tender_id = ? " +
                "ORDER BY b.submitted_at DESC";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tenderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BidDetailDTO bid = new BidDetailDTO();
                    bid.setBidId(rs.getInt("bid_id"));
                    bid.setTenderId(rs.getInt("tender_id"));
                    bid.setSupplierId(rs.getInt("supplier_id"));
                    bid.setDeliveryDays(rs.getInt("delivery_days"));
                    bid.setComplianceStatement(rs.getString("compliance_statement"));
                    bid.setDocumentFilePath(rs.getString("document_file_path"));
                    bid.setSubmittedAt(rs.getTimestamp("submitted_at"));
                    bid.setBusinessName(rs.getString("business_name"));
                    bid.setRegNumber(rs.getString("reg_number"));
                    bid.setEmail(rs.getString("email"));
                    bid.setPhoneNumber(rs.getString("phone_number"));
                    bid.setAwarded(rs.getBoolean("is_awarded"));
                    bid.setPrice(rs.getDouble("price"));
                    bid.setDeliveryScore(rs.getDouble("delivery_score"));
                    bid.setTechnicalScore(rs.getDouble("technical_score"));
                    bid.setPriceScore(rs.getDouble("price_score"));

                    double score = rs.getDouble("evaluation_score");
                    if (!rs.wasNull()) {
                        bid.setEvaluationScore(score);
                    }

                    bids.add(bid);
                }
            }
        }

        return bids;
    }

    /**
     * Find bids by supplier ID with full details
     */
    public List<BidSummaryDTO> findBidsBySupplierId(int supplierId) throws Exception{
        List<BidSummaryDTO> bids = new ArrayList<>();

        String sql = "SELECT " +
                "    b.bid_id, " +
                "    b.tender_id, " +
                "    t.title AS tender_title, " +
                "    t.reference_number AS tender_reference, " +
                "    t.status AS tender_status, " +
                "    b.submitted_at, " +
                "    CASE " +
                "        WHEN a.award_id IS NOT NULL THEN 'AWARDED' " +
                "        WHEN e.log_id IS NOT NULL THEN 'EVALUATED' " +
                "        ELSE 'PENDING' " +
                "    END AS evaluation_status, " +
                "    e.weighted_total AS total_score " +
                "FROM TenderBids b " +
                "INNER JOIN TenderOffers t ON b.tender_id = t.tender_id " +
                "LEFT JOIN EvaluatorBidLogs e ON b.bid_id = e.bid_id " +
                "LEFT JOIN Awards a ON b.bid_id = a.bid_id " +
                "WHERE b.supplier_id = ? " +
                "ORDER BY b.submitted_at DESC";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplierId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BidSummaryDTO bid = new BidSummaryDTO();
                    bid.setBidId(rs.getInt("bid_id"));
                    bid.setTenderId(rs.getInt("tender_id"));
                    bid.setTenderTitle(rs.getString("tender_title"));
                    bid.setTenderReference(rs.getString("tender_reference"));
                    bid.setTenderStatus(rs.getString("tender_status"));
                    bid.setSubmittedAt(rs.getTimestamp("submitted_at"));
                    bid.setEvaluationStatus(rs.getString("evaluation_status"));

                    double totalScore = rs.getDouble("total_score");
                    if (!rs.wasNull()) {
                        bid.setTotalScore(totalScore);
                    }

                    bids.add(bid);
                }
            }
        }
        return bids;
    }

    /**
     * Save a new bid
     */
    public boolean save(TenderBid bid) throws Exception{
        String sql = "INSERT INTO TenderBids (tender_id, supplier_id, delivery_days, compliance_statement, document_file_path, submitted_at, price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, bid.getTender_id());
            stmt.setInt(2, bid.getSupplier_id());
            stmt.setInt(3, bid.getDelivery_days());
            stmt.setString(4, bid.getCompliance_statement());
            stmt.setString(5, bid.getDocument_file_path());
            stmt.setTimestamp(6, bid.getSubmitted_at() != null ? bid.getSubmitted_at() : new Timestamp(System.currentTimeMillis()));
            stmt.setDouble(7, bid.getPrice());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        bid.setBid_id(rs.getInt(1));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Find bid by ID
     */
    public TenderBid findById(int bidId) throws Exception{
        String sql = "SELECT * FROM TenderBids WHERE bid_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bidId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTenderBid(rs);
                }
            }
        }
        return null;
    }

    /**
     * Update bid information
     */
    public boolean update(TenderBid bid) throws Exception{
        String sql = "UPDATE TenderBids SET delivery_days = ?, compliance_statement = ?, document_file_path = ? WHERE bid_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bid.getDelivery_days());
            stmt.setString(2, bid.getCompliance_statement());
            stmt.setString(3, bid.getDocument_file_path());
            stmt.setInt(4, bid.getBid_id());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }


    /**
     * Get bid statistics for a supplier
     */
    public BidStatsDTO getBidStatsBySupplier(int supplierId) throws Exception {
        BidStatsDTO stats = new BidStatsDTO();

        String sql = "SELECT " +
                "    COUNT(*) AS total_bids, " +
                "    COUNT(DISTINCT b.tender_id) AS unique_tenders, " +
                "    SUM(IF(a.award_id IS NOT NULL, 1, 0)) AS won_bids, " +
                "    SUM(IF(e.log_id IS NOT NULL AND a.award_id IS NULL, 1, 0)) AS evaluated_bids, " +
                "    SUM(IF(e.log_id IS NULL AND a.award_id IS NULL, 1, 0)) AS pending_bids, " +
                "    AVG(e.weighted_total) AS avg_score " +
                "FROM TenderBids b " +
                "LEFT JOIN EvaluatorBidLogs e ON b.bid_id = e.bid_id " +
                "LEFT JOIN Awards a ON b.bid_id = a.bid_id " +
                "WHERE b.supplier_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplierId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalBids(rs.getInt("total_bids"));
                    stats.setUniqueTenders(rs.getInt("unique_tenders"));
                    stats.setWonBids(rs.getInt("won_bids"));
                    stats.setEvaluatedBids(rs.getInt("evaluated_bids"));
                    stats.setPendingBids(rs.getInt("pending_bids"));

                    double avgScore = rs.getDouble("avg_score");
                    if (!rs.wasNull()) {
                        stats.setAverageScore(avgScore);
                    }
                }
            }
        }
        return stats;
    }

    /**
     * Map ResultSet to TenderBid object
     */
    private TenderBid mapResultSetToTenderBid(ResultSet rs) throws SQLException {
        TenderBid bid = new TenderBid();
        bid.setBid_id(rs.getInt("bid_id"));
        bid.setTender_id(rs.getInt("tender_id"));
        bid.setSupplier_id(rs.getInt("supplier_id"));
        bid.setDelivery_days(rs.getInt("delivery_days"));
        bid.setCompliance_statement(rs.getString("compliance_statement"));
        bid.setDocument_file_path(rs.getString("document_file_path"));
        bid.setSubmitted_at(rs.getTimestamp("submitted_at"));
        return bid;
    }

    public boolean delete(int bidId, int supplierId) {
        String sql = "DELETE FROM TenderBids WHERE bid_id = ? AND supplier_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bidId);
            stmt.setInt(2, supplierId);

            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (Exception ex) {
            return false;
        }
    }
}