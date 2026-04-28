package com.ProcureGov.repository;

import com.ProcureGov.model.SupplierData;
import java.sql.*;

public class SupplierDataRepository extends BaseRepository{

    /**
     * Finds a specific supplier's data by their unique user_id.
     */
    public SupplierData findByUserId(int userId) throws Exception {
        String sql = "SELECT * FROM view_supplier_data WHERE user_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new SupplierData(
                            rs.getInt("account_id"),
                            rs.getInt("user_id"),
                            rs.getInt("supplier_id"),
                            rs.getString("username"),
                            rs.getString("role_name"),
                            rs.getString("business_name"),
                            rs.getString("email"),
                            rs.getString("address"),
                            rs.getString("phone_number"),
                            rs.getString("reg_number")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Finds a specific supplier's data by their unique supplier_id.
     */
    public SupplierData findBySupplierId(int userId) throws Exception {
        String sql = "SELECT * FROM view_supplier_data WHERE view_supplier_data.supplier_id = ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new SupplierData(
                            rs.getInt("account_id"),
                            rs.getInt("user_id"),
                            rs.getInt("supplier_id"),
                            rs.getString("username"),
                            rs.getString("role_name"),
                            rs.getString("business_name"),
                            rs.getString("email"),
                            rs.getString("address"),
                            rs.getString("phone_number"),
                            rs.getString("reg_number")
                    );
                }
            }
        }
        return null;
    }

}