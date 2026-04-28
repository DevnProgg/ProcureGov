package com.ProcureGov.repository;

import com.ProcureGov.model.EmployeeData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmployeeDataRepository extends BaseRepository {
    public EmployeeData getEmployeeDataByID(int ID) throws Exception{
        String sql = "SELECT * FROM view_employee_data WHERE user_id = ?";

        try(Connection conn = getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, ID);
            try (ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return new EmployeeData(
                            rs.getInt("account_id"),
                            rs.getInt("user_id"),
                            rs.getInt("employee_id"),
                            rs.getString("username"),
                            rs.getString("role_name"),
                            rs.getString("privilege_level"),
                            rs.getString("full_names"),
                            rs.getString("phone_number"),
                            rs.getString("gender"),
                            rs.getBoolean("active_status"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        }
        return null;
    }
}