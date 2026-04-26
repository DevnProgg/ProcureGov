package com.ProcureGov.repository;

import com.ProcureGov.model.EmployeeData;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDataRepository extends BaseRepository {
    public List<EmployeeData> getAllEmployeeData() throws Exception {
        List<EmployeeData> list = new ArrayList<>();
        String sql = "SELECT * FROM view_employee_data";
        try (Connection conn = getDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new EmployeeData(
                        rs.getInt("account_id"), rs.getInt("user_id"), rs.getInt("employee_id"),
                        rs.getString("username"), rs.getString("role_name"), rs.getString("privilege_level"),
                        rs.getString("full_names"), rs.getString("phone_number"), rs.getString("gender"),
                        rs.getBoolean("active_status"), rs.getTimestamp("created_at")
                ));
            }
        }
        return list;
    }

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