package com.ProcureGov.repository;

import com.ProcureGov.model.User;
import java.sql.*;

public class UserRepository extends BaseRepository{
    public User create(User user) throws Exception {
        String sql = "INSERT INTO Users (employee_id, supplier_id) VALUES (?, ?)";
        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Handle nullable foreign keys to satisfy SQL CHK_USER_TYPE
            if (user.getEmployee_id() > 0) stmt.setInt(1, user.getEmployee_id());
            else stmt.setNull(1, Types.INTEGER);

            if (user.getSupplier_id() > 0) stmt.setInt(2, user.getSupplier_id());
            else stmt.setNull(2, Types.INTEGER);

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) user.setUser_id(rs.getInt(1));
        }
        return user;
    }
}