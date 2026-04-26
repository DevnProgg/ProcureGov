package com.ProcureGov.repository;

import com.ProcureGov.model.SupplierData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDataRepository extends BaseRepository{

    /**
     * Retrieves all supplier information consolidated from the Accounts,
     * Users, and Suppliers tables via the view_supplier_data view.
     */
    public List<SupplierData> findAll() throws Exception {
        List<SupplierData> list = new ArrayList<>();
        String sql = "SELECT * FROM view_supplier_data";

        try (Connection conn = getDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SupplierData data = new SupplierData(
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
                list.add(data);
            }
        }
        return list;
    }

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
}