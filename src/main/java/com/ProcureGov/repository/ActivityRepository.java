package com.ProcureGov.repository;

import com.ProcureGov.model.ActivityItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityRepository extends BaseRepository {

    public List<ActivityItem> findRecentActivity(int limit) throws  Exception {
        List<ActivityItem> activities = new ArrayList<>();

        String sql = "SELECT * FROM bidevaluations ORDER BY evaluated_at DESC LIMIT ?";

        try (Connection conn = getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(mapResultSetToActivity(rs));
                }
            }
        }

        return activities;
    }

    private ActivityItem mapResultSetToActivity(ResultSet rs) throws SQLException {
        ActivityItem activity = new ActivityItem();
        activity.setId(rs.getInt("id"));
        activity.setTitle(rs.getString("title"));
        activity.setDescription(rs.getString("description"));
        activity.setType(rs.getString("type"));
        activity.setCategory(rs.getString("category"));
        activity.setTimestamp(rs.getTimestamp("timestamp"));
        activity.setUserId(rs.getInt("user_id"));
        return activity;
    }
}