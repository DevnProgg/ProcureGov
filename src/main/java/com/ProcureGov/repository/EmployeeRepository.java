package com.ProcureGov.repository;

import com.ProcureGov.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class EmployeeRepository extends BaseRepository {
    public Employee create(Employee emp) throws Exception {
        String sql = "INSERT INTO Employees (full_names, phone_number, gender) VALUES (?, ?, ?)";
        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, emp.getFull_names());
            stmt.setString(2, emp.getPhone_number());
            stmt.setString(3, emp.getGender());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) emp.setEmployee_id(rs.getInt(1));
        }
        return emp;
    }
}