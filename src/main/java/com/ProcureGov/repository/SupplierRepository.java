package com.ProcureGov.repository;

import com.ProcureGov.model.Supplier;
import java.sql.*;

public class SupplierRepository extends BaseRepository{

    public Supplier save(Supplier supplier) throws  Exception {
        String sql = "INSERT INTO Suppliers (business_name, email, address, phone_number, reg_number) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, supplier.getBusiness_name());
            stmt.setString(2, supplier.getEmail());
            stmt.setString(3, supplier.getAddress());
            stmt.setString(4, supplier.getPhone_number());
            stmt.setString(5, supplier.getReg_number());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) supplier.setSupplier_id(rs.getInt(1));
        }
        return supplier;
    }

    public Supplier save(Connection conn, Supplier supplier) throws Exception {
        String sql = "INSERT INTO Suppliers (business_name, email, address, phone_number, reg_number) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, supplier.getBusiness_name());
            stmt.setString(2, supplier.getEmail());
            stmt.setString(3, supplier.getAddress());
            stmt.setString(4, supplier.getPhone_number());
            stmt.setString(5, supplier.getReg_number());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) supplier.setSupplier_id(rs.getInt(1));
            }
        }
        return supplier;
    }

    public void update(Supplier supplier) throws Exception {
        String sql = "UPDATE Suppliers SET business_name=?, email=?, address=?, phone_number=?, reg_number=? WHERE supplier_id=?";
        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getBusiness_name());
            stmt.setString(2, supplier.getEmail());
            stmt.setString(3, supplier.getAddress());
            stmt.setString(4, supplier.getPhone_number());
            stmt.setString(5, supplier.getReg_number());
            stmt.setInt(6, supplier.getSupplier_id());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws Exception {
        String sql = "DELETE FROM Suppliers WHERE supplier_id = ?";
        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}