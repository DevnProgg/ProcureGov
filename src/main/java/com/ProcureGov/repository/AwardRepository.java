package com.ProcureGov.repository;

import com.ProcureGov.dto.AwardDTO;
import com.ProcureGov.model.Award;
import com.ProcureGov.model.SupplierData;
import com.ProcureGov.model.TenderOffer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AwardRepository extends BaseRepository{

    /**
     * Create a new award
     */
    public boolean createAward(Award award) {
        String sql = "INSERT INTO Awards (tender_id, bid_id, awarded_value, officer_justification, awarded_by) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, award.getTender_id());
            pstmt.setInt(2, award.getBid_id());
            pstmt.setDouble(3, award.getAwarded_value());
            pstmt.setString(4, award.getOfficer_justification());
            pstmt.setInt(5, award.getAwarded_by());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        award.setAward_id(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get award by ID
     */
    public Award getAwardById(int awardId) {
        String sql = "SELECT * FROM Awards WHERE award_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, awardId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAwardFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Get award by tender ID
     */
    public Award getAwardByTenderId(int tenderId) {
        String sql = "SELECT * FROM Awards WHERE tender_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tenderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAwardFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Get all awards
     */
    public List<AwardDTO> getAllAwards() {
        String sql = "SELECT " +
                "   a.award_id, " +
                "   a.tender_id, " +
                "   a.bid_id, " +
                "   a.awarded_value, " +
                "   a.officer_justification, " +
                "   a.award_date, " +
                "   a.awarded_by, " +
                "   e.full_names AS awarded_by_name, " +
                "   t.reference_number AS tender_reference, " +
                "   t.title AS tender_title, " +
                "   t.category AS tender_category, " +
                "   s.supplier_id, " +
                "   s.business_name AS supplier_business_name, " +
                "   s.email AS supplier_email, " +
                "   s.phone_number AS supplier_phone, " +
                "   tb.price AS bid_price, " +
                "   tb.delivery_days, " +
                "   AVG(ebl.weighted_total) AS final_score " +
                "FROM Awards a " +
                "JOIN TenderOffers t ON t.tender_id = a.tender_id " +
                "JOIN TenderBids tb ON tb.bid_id = a.bid_id " +
                "JOIN Suppliers s ON s.supplier_id = tb.supplier_id " +
                "JOIN Employees e ON e.employee_id = a.awarded_by " +
                "LEFT JOIN EvaluatorBidLogs ebl ON ebl.bid_id = tb.bid_id " +
                "GROUP BY a.award_id, t.tender_id, s.supplier_id, tb.bid_id ";

        List<AwardDTO> awards = new ArrayList<>();

        try (Connection conn = getDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                awards.add(extractAwardDTOFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return awards;
    }

    /**
     * Get awards by supplier ID (for supplier portal)
     */
    public List<AwardDTO> getAwardsBySupplierId(int supplierId) {
        String sql = "SELECT " +
                "   a.award_id, " +
                "   a.tender_id, " +
                "   a.bid_id, " +
                "   a.awarded_value, " +
                "   a.officer_justification, " +
                "   a.award_date, " +
                "   a.awarded_by, " +
                "   e.full_names AS awarded_by_name, " +
                "   t.reference_number AS tender_reference, " +
                "   t.title AS tender_title, " +
                "   t.category AS tender_category, " +
                "   s.supplier_id, " +
                "   s.business_name AS supplier_business_name, " +
                "   s.email AS supplier_email, " +
                "   s.phone_number AS supplier_phone, " +
                "   tb.price AS bid_price, " +
                "   tb.delivery_days, " +
                "   AVG(ebl.weighted_total) AS final_score " +
                "FROM Awards a " +
                "JOIN TenderOffers t ON t.tender_id = a.tender_id " +
                "JOIN TenderBids tb ON tb.bid_id = a.bid_id " +
                "JOIN Suppliers s ON s.supplier_id = tb.supplier_id " +
                "JOIN Employees e ON e.employee_id = a.awarded_by " +
                "LEFT JOIN EvaluatorBidLogs ebl ON ebl.bid_id = tb.bid_id " +
                "WHERE s.supplier_id = ? " +
                "GROUP BY a.award_id, t.tender_id, s.supplier_id, tb.bid_id ";

        List<AwardDTO> awards = new ArrayList<>();

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    awards.add(extractAwardDTOFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return awards;
    }

    /**
     * Get recent awards (for dashboard)
     */
    public List<AwardDTO> getRecentAwards(int limit) {
        String sql = "SELECT " +
                "   a.award_id, " +
                "   a.tender_id, " +
                "   a.bid_id, " +
                "   a.awarded_value, " +
                "   a.officer_justification, " +
                "   a.award_date, " +
                "   a.awarded_by, " +
                "   e.full_names AS awarded_by_name, " +
                "   t.reference_number AS tender_reference, " +
                "   t.title AS tender_title, " +
                "   t.category AS tender_category, " +
                "   s.supplier_id, " +
                "   s.business_name AS supplier_business_name, " +
                "   s.email AS supplier_email, " +
                "   s.phone_number AS supplier_phone, " +
                "   tb.price AS bid_price, " +
                "   tb.delivery_days, " +
                "   AVG(ebl.weighted_total) AS final_score " +
                "FROM Awards a " +
                "JOIN TenderOffers t ON t.tender_id = a.tender_id " +
                "JOIN TenderBids tb ON tb.bid_id = a.bid_id " +
                "JOIN Suppliers s ON s.supplier_id = tb.supplier_id " +
                "JOIN Employees e ON e.employee_id = a.awarded_by " +
                "LEFT JOIN EvaluatorBidLogs ebl ON ebl.bid_id = tb.bid_id " +
                "GROUP BY a.award_id, t.tender_id, s.supplier_id, tb.bid_id " +
                "LIMIT ?";

        List<AwardDTO> awards = new ArrayList<>();

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    awards.add(extractAwardDTOFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return awards;
    }

    /**
     * Check if a tender already has an award
     */
    public boolean hasAwardForTender(int tenderId) {
        String sql = "SELECT COUNT(*) FROM Awards WHERE tender_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tenderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Delete award
     */
    public boolean deleteAward(int awardId) {
        String sql = "DELETE FROM Awards WHERE award_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, awardId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate award notice number
     */
    public String generateAwardNoticeNumber(int awardId, int year) {
        return String.format("PG-AWARD-%04d-%d", awardId, year);
    }

    /**
     * Generate contract number
     */
    public String generateContractNumber(int tenderId, int supplierId, int year) {
        return String.format("PG-CONT-%04d-%04d-%d", tenderId, supplierId, year);
    }

    // Helper methods for extracting data from ResultSet

    private Award extractAwardFromResultSet(ResultSet rs) throws SQLException {
        Award award = new Award();
        award.setAward_id(rs.getInt("award_id"));
        award.setTender_id(rs.getInt("tender_id"));
        award.setBid_id(rs.getInt("bid_id"));
        award.setAwarded_value(rs.getDouble("awarded_value"));
        award.setOfficer_justification(rs.getString("officer_justification"));
        award.setAward_date(rs.getTimestamp("award_date"));
        award.setAwarded_by(rs.getInt("awarded_by"));
        return award;
    }

    private AwardDTO extractAwardDTOFromResultSet(ResultSet rs) throws SQLException {
        AwardDTO dto = new AwardDTO();
        dto.setAwardId(rs.getInt("award_id"));
        dto.setTenderId(rs.getInt("tender_id"));
        dto.setBidId(rs.getInt("bid_id"));
        dto.setAwardedValue(rs.getBigDecimal("awarded_value"));
        dto.setOfficerJustification(rs.getString("officer_justification"));
        dto.setAwardDate(rs.getTimestamp("award_date"));
        dto.setAwardedBy(rs.getInt("awarded_by"));
        dto.setAwardedByName(rs.getString("awarded_by_name"));
        dto.setTenderReference(rs.getString("tender_reference"));
        dto.setTenderTitle(rs.getString("tender_title"));
        dto.setTenderCategory(rs.getString("tender_category"));
        dto.setSupplierId(rs.getInt("supplier_id"));
        dto.setSupplierBusinessName(rs.getString("supplier_business_name"));
        dto.setSupplierEmail(rs.getString("supplier_email"));
        dto.setSupplierPhone(rs.getString("supplier_phone"));
        dto.setBidPrice(rs.getBigDecimal("bid_price"));
        dto.setDeliveryDays(rs.getInt("delivery_days"));

        double finalScore = rs.getDouble("final_score");
        dto.setFinalScore(rs.wasNull() ? null : finalScore);

        // Generate notice numbers based on award date
        if (dto.getAwardDate() != null) {
            int year = dto.getAwardDate().toLocalDateTime().getYear();
            dto.setAwardNoticeNumber(String.format("PG-AWARD-%04d-%d", dto.getAwardId(), year));
            dto.setContractNumber(String.format("PG-CONT-%04d-%04d-%d",
                    dto.getTenderId(), dto.getSupplierId(), year));
        }

        return dto;
    }

    /**
     * Get supplier information by bid ID
     * This method joins through TenderBids -> Suppliers to get the supplier who submitted the bid
     */
    public SupplierData getSupplierByBidId(int bidId) {
        String sql = "SELECT vsd.* FROM view_supplier_data vsd " +
                "JOIN TenderBids tb ON vsd.supplier_id = tb.supplier_id " +
                "WHERE tb.bid_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bidId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSupplierData(rs);
                }
            }

        } catch (Exception e) {
            System.err.println("Error fetching supplier by bid ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Alternative method: Get supplier by tender ID (through award)
     */
    public SupplierData getSupplierByTenderId(int tenderId) {
        String sql = "SELECT vsd.* FROM view_supplier_data vsd " +
                "JOIN Awards a ON a.tender_id = ? " +
                "JOIN TenderBids tb ON a.bid_id = tb.bid_id " +
                "WHERE vsd.supplier_id = tb.supplier_id";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tenderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSupplierData(rs);
                }
            }

        } catch (Exception e) {
            System.err.println("Error fetching supplier by tender ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get tender details by tender ID
     */
    public TenderOffer getTenderById(int tenderId) {
        String sql = "SELECT * FROM TenderOffers WHERE tender_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tenderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTender(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching tender by ID: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * Update email notification status
     */
    public void updateEmailStatus(int awardId, String status, String errorMessage) {
        String sql = "UPDATE Awards SET email_notification_status = ?, " +
                "email_sent_at = ?, email_error_message = ? " +
                "WHERE award_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setTimestamp(2, "SENT".equals(status) ? new Timestamp(System.currentTimeMillis()) : null);
            stmt.setString(3, errorMessage);
            stmt.setInt(4, awardId);

            stmt.executeUpdate();

        } catch (Exception e) {
            System.err.println("Error updating email status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get email notification status for an award
     */
    public String getEmailStatus(int awardId) {
        String sql = "SELECT email_notification_status FROM Awards WHERE award_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, awardId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email_notification_status");
                }
            }

        } catch (Exception e) {
            System.err.println("Error fetching email status: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Update email sent status (simplified version)
     */
    public void updateEmailSentStatus(int awardId, boolean sent) {
        String status = sent ? "SENT" : "FAILED";
        updateEmailStatus(awardId, status, null);
    }

    /**
     * Log activity for audit trail
     */
    public void logActivity(int awardId, String activityType, String status,
                            String description, int createdBy) {
        String sql = "INSERT INTO activity_logs (award_id, activity_type, status, description, created_by) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, awardId);
            stmt.setString(2, activityType);
            stmt.setString(3, status);
            stmt.setString(4, description);
            stmt.setInt(5, createdBy);

            stmt.executeUpdate();

        } catch (Exception e) {
            System.err.println("Error logging activity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper mapping methods

    private SupplierData mapResultSetToSupplierData(ResultSet rs) throws SQLException {
        SupplierData supplier = new SupplierData();
        supplier.setAccount_id(rs.getInt("account_id"));
        supplier.setUser_id(rs.getInt("user_id"));
        supplier.setSupplier_id(rs.getInt("supplier_id"));
        supplier.setUsername(rs.getString("username"));
        supplier.setRole_name(rs.getString("role_name"));
        supplier.setBusiness_name(rs.getString("business_name"));
        supplier.setEmail(rs.getString("email"));
        supplier.setAddress(rs.getString("address"));
        supplier.setPhone_number(rs.getString("phone_number"));
        supplier.setReg_number(rs.getString("reg_number"));
        return supplier;
    }

    private TenderOffer mapResultSetToTender(ResultSet rs) throws SQLException {
        TenderOffer tender = new TenderOffer();
        tender.setTender_id(rs.getInt("tender_id"));
        tender.setReference_number(rs.getString("reference_number"));
        tender.setTitle(rs.getString("title"));
        tender.setDescription(rs.getString("description"));
        tender.setPublish_datetime(rs.getTimestamp("publish_datetime") != null ?
                rs.getTimestamp("publish_datetime") : null);
        tender.setExpiry_datetime(rs.getTimestamp("expiry_datetime"));
        tender.setCreated_by(rs.getInt("created_by"));
        tender.setStatus(rs.getString("status"));
        tender.setCategory(rs.getString("category"));
        tender.setEstimated_value(rs.getDouble("estimated_value"));
        tender.setNotice_file_path(rs.getString("notice_file_path"));
        return tender;
    }

}