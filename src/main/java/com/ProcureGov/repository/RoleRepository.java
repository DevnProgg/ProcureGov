package com.ProcureGov.repository;

import com.ProcureGov.model.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleRepository extends BaseRepository{
    public void save(Role role) throws Exception {
        String sql = "INSERT INTO Roles (name, privilege_level) VALUES (?, ?)";
        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, role.getName());
            stmt.setString(2, role.getPrivilege_level());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) role.setId(rs.getInt(1));
        }
    }

    public List<Role> findAll() throws Exception {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM Roles";
        try (Connection conn = getDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(new Role(rs.getInt("role_id"), rs.getString("name"), rs.getString("privilege_level")));
            }
        }
        return roles;
    }

    public int getSupplierRoleID() throws Exception {
        String sql = "SELECT role_id FROM Roles WHERE name = 'SUPPLIER' AND privilege_level = 'EXTERNAL'";
        try (Connection conn = getDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("role_id");
            }
        }
        return 0;
    }
}
