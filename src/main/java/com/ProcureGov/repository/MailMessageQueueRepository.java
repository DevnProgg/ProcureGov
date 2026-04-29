package com.ProcureGov.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class MailMessageQueueRepository extends BaseRepository{

    public void Enqueue (String supplierEmail, String subject, String body) throws Exception{
        String sql = "INSERT INTO EmailMessageQueue (supplier_email, subject, email_body) VALUES (?, ?, ?)";
        try(Connection conn = getDataSource().getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, supplierEmail);
            stmt.setString(2, subject);
            stmt.setString(3, body);
            stmt.executeUpdate();
        }
    }

    public void Enqueue(Connection conn, String supplierEmail, String subject, String body) throws Exception {
        String sql = "INSERT INTO EmailMessageQueue (supplier_email, subject, email_body) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplierEmail);
            stmt.setString(2, subject);
            stmt.setString(3, body);
            stmt.executeUpdate();
        }
    }
}
