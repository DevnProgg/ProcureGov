package com.ProcureGov.repository;

import com.ProcureGov.dto.LoginResult;
import com.ProcureGov.model.Account;
import com.ProcureGov.util.PasswordEncryption;

import java.sql.*;

public class AccountRepository extends BaseRepository {

    public Account createAccount(Account acc) throws Exception{
        String sql = "INSERT INTO Accounts (role_id, user_id, username, password_hash, active_status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, acc.getRole_id());
            stmt.setInt(2, acc.getUser_id());
            stmt.setString(3, acc.getUsername());
            stmt.setString(4, PasswordEncryption.hashPassword(acc.getPassword_hash()));
            stmt.setBoolean(5, acc.isActive_status());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) acc.setAccount_id(rs.getInt(1));
        }
        return acc;
    }

    public LoginResult loginToAccount(String username, String password) throws Exception {
        String sql = "SELECT a.user_id, a.password_hash, r.name AS role_name " +
                "FROM Accounts a " +
                "JOIN Roles r ON a.role_id = r.role_id " +
                "WHERE a.username = ? AND a.active_status = 1";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (PasswordEncryption.verifyPassword(password, rs.getString("password_hash"))) {
                        return new LoginResult(rs.getInt("user_id"), rs.getString("role_name"));
                    }
                }
            }
        }
        return null;
    }
}