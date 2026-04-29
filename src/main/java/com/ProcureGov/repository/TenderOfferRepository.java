package com.ProcureGov.repository;

import com.ProcureGov.dto.CategoryStatsDTO;
import com.ProcureGov.dto.TenderStatsDTO;
import com.ProcureGov.model.TenderOffer;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TenderOfferRepository extends BaseRepository {

    public void create(TenderOffer offer) throws Exception {
        String sql = "INSERT INTO TenderOffers (reference_number, title, description, expiry_datetime, created_by, status, category, estimated_value, notice_file_path, publish_datetime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, offer.getReference_number());
            stmt.setString(2, offer.getTitle());
            stmt.setString(3, offer.getDescription());
            stmt.setTimestamp(4, new Timestamp(offer.getExpiry_datetime().getTime()));
            stmt.setInt(5, offer.getCreated_by());
            stmt.setString(6, offer.getStatus());
            stmt.setString(7, offer.getCategory());
            stmt.setDouble(8, offer.getEstimated_value());
            stmt.setString(9, offer.getNotice_file_path());
            stmt.setTimestamp(10, offer.getPublish_datetime() != null ?
                    new Timestamp(offer.getPublish_datetime().getTime()) :
                    new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) offer.setTender_id(rs.getInt(1));
        }
    }

    /**
     * Get comprehensive tender statistics for dashboard
     */
    public TenderStatsDTO getTenderStats() throws Exception{
        TenderStatsDTO stats = new TenderStatsDTO();

        String sql = "SELECT " +
                "    COUNT(*) AS total_tenders, " +
                "    SUM(IF(status = 'OPEN' AND expiry_datetime > NOW(), 1, 0)) AS open_tenders, " +
                "    SUM(IF(status = 'CLOSED' OR expiry_datetime <= NOW(), 1, 0)) AS closed_tenders, " +
                "    SUM(IF(status = 'UNDER_EVALUATION', 1, 0)) AS under_evaluation_tenders, " +
                "    SUM(IF(status = 'AWARDED', 1, 0)) AS awarded_tenders, " +
                "    SUM(estimated_value) AS total_estimated_value, " +
                "    (SELECT COALESCE(SUM(awarded_value), 0) FROM Awards) AS awarded_value, " +
                "    (SELECT COUNT(*) FROM TenderBids) AS total_bids_submitted, " +
                "    (SELECT COUNT(DISTINCT supplier_id) FROM TenderBids) AS active_suppliers " +
                "FROM TenderOffers";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                stats.setTotalTenders(rs.getInt("total_tenders"));
                stats.setOpenTenders(rs.getInt("open_tenders"));
                stats.setClosedTenders(rs.getInt("closed_tenders"));
                stats.setUnderEvaluationTenders(rs.getInt("under_evaluation_tenders"));
                stats.setAwardedTenders(rs.getInt("awarded_tenders"));
                stats.setTotalEstimatedValue(rs.getDouble("total_estimated_value"));
                stats.setAwardedValue(rs.getDouble("awarded_value"));
                stats.setTotalBidsSubmitted(rs.getInt("total_bids_submitted"));
                stats.setActiveSuppliers(rs.getInt("active_suppliers"));
            }
        }
        return stats;
    }

    /**
     * Get detailed tender statistics by category
     */
    public List<CategoryStatsDTO> getTenderStatsByCategory() throws Exception{
        List<CategoryStatsDTO> categoryStats = new ArrayList<>();

        String sql = "SELECT " +
                "    category, " +
                "    COUNT(*) AS tender_count, " +
                "    SUM(IF(status = 'OPEN' AND expiry_datetime > NOW(), 1, 0)) AS open_count, " +
                "    SUM(estimated_value) AS total_value, " +
                "    AVG(estimated_value) AS avg_value " +
                "FROM TenderOffers " +
                "GROUP BY category " +
                "ORDER BY tender_count DESC";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CategoryStatsDTO stats = new CategoryStatsDTO();
                stats.setCategory(rs.getString("category"));
                stats.setTenderCount(rs.getInt("tender_count"));
                stats.setOpenCount(rs.getInt("open_count"));
                stats.setTotalValue(rs.getDouble("total_value"));
                stats.setAvgValue(rs.getDouble("avg_value"));
                categoryStats.add(stats);
            }
        }
        return categoryStats;
    }

    /**
     * Find open tenders excluding drafts with limit
     */
    public List<TenderOffer> findOpenTendersExcludingDrafts(int limit) throws Exception{
        List<TenderOffer> tenders = new ArrayList<>();

        String sql = "SELECT * FROM TenderOffers " +
                "WHERE status = 'OPEN' " +
                "AND expiry_datetime > NOW() " +
                "ORDER BY publish_datetime DESC " +
                "LIMIT ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tenders.add(mapResultSetToTenderOffer(rs));
                }
            }
        }
        return tenders;
    }

    /**
     * Find all open tenders excluding drafts
     */
    public List<TenderOffer> findAllOpenTendersExcludingDrafts() throws  Exception{
        List<TenderOffer> tenders = new ArrayList<>();

        String sql = "SELECT * FROM TenderOffers " +
                "WHERE status = 'OPEN' " +
                "AND expiry_datetime > NOW() " +
                "ORDER BY publish_datetime DESC";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tenders.add(mapResultSetToTenderOffer(rs));
            }
        }
        return tenders;
    }

    /**
     * Find all tenders excluding drafts
     */
    public List<TenderOffer> findAllExcludingDrafts() throws Exception{
        List<TenderOffer> tenders = new ArrayList<>();

        String sql = "SELECT * FROM TenderOffers " +
                "WHERE status != 'DRAFT' " +
                "ORDER BY publish_datetime DESC";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tenders.add(mapResultSetToTenderOffer(rs));
            }
        }
        return tenders;
    }

    /**
     * Find filtered tenders excluding drafts
     */
    public List<TenderOffer> findFilteredTendersExcludingDrafts(String status, String category) throws Exception{
        List<TenderOffer> tenders = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM TenderOffers WHERE status != 'DRAFT' ");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isEmpty()) {
            sql.append("AND status = ? ");
            params.add(status);
        }

        if (category != null && !category.isEmpty()) {
            sql.append("AND category = ? ");
            params.add(category);
        }

        sql.append("ORDER BY publish_datetime DESC");

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tenders.add(mapResultSetToTenderOffer(rs));
                }
            }
        }

        return tenders;
    }

    public int getDraftCount() throws Exception{
        String sql = "SELECT COUNT(*) FROM TenderOffers WHERE status = 'DRAFT'";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<TenderOffer> findTendersClosingWithin(int hours) throws Exception{
        List<TenderOffer> tenders = new ArrayList<>();

        String sql = "SELECT * FROM TenderOffers WHERE status = 'OPEN' AND expiry_datetime <= DATE_ADD(NOW(), INTERVAL ? HOUR)";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, hours);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tenders.add(mapResultSetToTenderOffer(rs));
                }
            }
        }
        return tenders;
    }

    public int getPendingEvaluationCount() throws Exception{
        String sql = "SELECT COUNT(*) FROM TenderOffers WHERE status = 'CLOSED' AND tender_id NOT IN (SELECT DISTINCT tender_id FROM Awards)";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getStaleDraftCount(int days) throws Exception{
        String sql = "SELECT COUNT(*) FROM TenderOffers WHERE status = 'DRAFT' AND publish_datetime IS NULL AND expiry_datetime < DATE_SUB(NOW(), INTERVAL ? DAY)";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, days);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<TenderOffer> findDraftTenders() throws Exception{
        List<TenderOffer> tenders = new ArrayList<>();

        String sql = "SELECT * FROM TenderOffers WHERE status = 'DRAFT'";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tenders.add(mapResultSetToTenderOffer(rs));
            }
        }
        return tenders;
    }

    /**
     * Find open tenders with limit
     */
    public List<TenderOffer> findOpenTenders(int limit) throws Exception {
        List<TenderOffer> tenders = new ArrayList<>();

        String sql = "SELECT * FROM TenderOffers " +
                "WHERE status = 'OPEN' " +
                "ORDER BY publish_datetime DESC " +
                "LIMIT ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tenders.add(mapResultSetToTenderOffer(rs));
                }
            }
        }
        return tenders;
    }

    /**
     * Find all open tenders
     */
    public List<TenderOffer> findAllOpenTenders() throws Exception{
        List<TenderOffer> tenders = new ArrayList<>();

        String sql = "SELECT * FROM TenderOffers " +
                "WHERE status = 'OPEN' " +
                "AND expiry_datetime > NOW() " +
                "ORDER BY publish_datetime DESC";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tenders.add(mapResultSetToTenderOffer(rs));
            }
        }
        return tenders;
    }

    /**
     * Find tender by ID
     */
    public TenderOffer findById(int tenderId) throws Exception{
        String sql = "SELECT * FROM TenderOffers WHERE tender_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tenderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTenderOffer(rs);
                }
            }
        }
        return null;
    }

    /**
     * Update tender status
     */
    public void updateStatus(int tenderId, String status) throws Exception {
        String sql = "UPDATE TenderOffers SET status = ? WHERE tender_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, tenderId);
            stmt.executeUpdate();
        }
    }

    /**
     * Map ResultSet to TenderOffer object
     */
    private TenderOffer mapResultSetToTenderOffer(ResultSet rs) throws SQLException {
        TenderOffer tender = new TenderOffer();
        tender.setTender_id(rs.getInt("tender_id"));
        tender.setReference_number(rs.getString("reference_number"));
        tender.setTitle(rs.getString("title"));
        tender.setDescription(rs.getString("description"));
        tender.setStatus(rs.getString("status"));
        tender.setCategory(rs.getString("category"));
        tender.setNotice_file_path(rs.getString("notice_file_path"));
        tender.setPublish_datetime(rs.getTimestamp("publish_datetime"));
        tender.setExpiry_datetime(rs.getTimestamp("expiry_datetime"));
        tender.setEstimated_value(rs.getDouble("estimated_value"));
        tender.setCreated_by(rs.getInt("created_by"));
        return tender;
    }

    // Add these methods to TenderOfferRepository.java

    /**
     * Find all tenders
     */
    public List<TenderOffer> findAll() throws Exception {
        List<TenderOffer> tenders = new ArrayList<>();

        String sql = "SELECT * FROM TenderOffers ORDER BY publish_datetime DESC";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tenders.add(mapResultSetToTenderOffer(rs));
            }
        }
        return tenders;
    }

    /**
     * Find filtered tenders by status and/or category
     */
    public List<TenderOffer> findFilteredTenders(String status, String category) throws Exception{
        List<TenderOffer> tenders = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM TenderOffers WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isEmpty()) {
            sql.append("AND status = ? ");
            params.add(status);
        }

        if (category != null && !category.isEmpty()) {
            sql.append("AND category = ? ");
            params.add(category);
        }

        sql.append("ORDER BY publish_datetime DESC");

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tenders.add(mapResultSetToTenderOffer(rs));
                }
            }
        }
        return tenders;
    }

    /**
     * Get all unique categories
     */
    public List<String> findAllCategories() throws Exception{
        List<String> categories = new ArrayList<>();

        String sql = "SELECT DISTINCT category FROM TenderOffers WHERE category IS NOT NULL ORDER BY category";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        }
        return categories;
    }

    /*

    Get all tenders by status
     */
    public List<TenderOffer> getTendersByStatus(String status) throws Exception {
        List<TenderOffer> tenders = new ArrayList<>();

        String sql = "SELECT * FROM TenderOffers WHERE status = ? ORDER BY publish_datetime DESC";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tenders.add(mapResultSetToTenderOffer(rs));
                }
            }
        }

        return tenders;
    }

    /**
     * Update tender offer record
     */
    public void update(TenderOffer tender) throws Exception {
        String sql = "UPDATE TenderOffers SET reference_number = ?, title = ?, description = ?, publish_datetime = ?, expiry_datetime = ?, created_by = ?, status = ?, category = ?, estimated_value = ?, notice_file_path = ? WHERE tender_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tender.getReference_number());
            stmt.setString(2, tender.getTitle());
            stmt.setString(3, tender.getDescription());

            // publish_datetime may be null for drafts
            if (tender.getPublish_datetime() != null) {
                stmt.setTimestamp(4, new Timestamp(tender.getPublish_datetime().getTime()));
            } else {
                stmt.setTimestamp(4, null);
            }

            stmt.setTimestamp(5, tender.getExpiry_datetime() != null ? new Timestamp(tender.getExpiry_datetime().getTime()) : null);
            stmt.setInt(6, tender.getCreated_by());
            stmt.setString(7, tender.getStatus());
            stmt.setString(8, tender.getCategory());
            stmt.setDouble(9, tender.getEstimated_value());
            stmt.setString(10, tender.getNotice_file_path());
            stmt.setInt(11, tender.getTender_id());

            stmt.executeUpdate();
        }
    }
}