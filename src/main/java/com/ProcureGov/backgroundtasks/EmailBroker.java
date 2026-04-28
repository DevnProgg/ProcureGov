package com.ProcureGov.backgroundtasks;

import com.ProcureGov.util.EmailUtility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmailBroker {
    private static final EmailUtility emailUtility = new EmailUtility();

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class EmailQueue {
        private int emailID;
        private String supplierEmail, subject, emailBody;
        private Timestamp queuedAt;
    }

    static class EmailDAO {
        private final Connection conn;

        public EmailDAO(Connection conn) {
            this.conn = conn;
        }

        public List<EmailQueue> retrieveEmailQueue() throws SQLException {
            List<EmailQueue> emailQueue = new ArrayList<>();
            String sql = "SELECT * FROM EmailMessageQueue WHERE sent = FALSE";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EmailQueue eq = new EmailQueue();
                    eq.setEmailID(rs.getInt("email_id"));
                    eq.setSupplierEmail(rs.getString("supplier_email"));
                    eq.setSubject(rs.getString("subject"));
                    eq.setEmailBody(rs.getString("email_body"));
                    eq.setQueuedAt(rs.getTimestamp("queued_at"));
                    emailQueue.add(eq);
                }
            }
            return emailQueue;
        }

        public void markAsSent(int emailID) throws SQLException {
            String sql = "UPDATE EmailMessageQueue SET sent = TRUE WHERE email_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, emailID);
                stmt.executeUpdate();
            }
        }

        public void markAsFailed(int emailID, String errorMessage) throws SQLException {
            String sql = "UPDATE EmailMessageQueue SET sent = FALSE, error_message = ?, retry_count = retry_count + 1 WHERE email_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, errorMessage);
                stmt.setInt(2, emailID);
                stmt.executeUpdate();
            }
        }
    }

    public static void EmailBrokerExecutor(Connection conn) {
        EmailDAO emailDAO = new EmailDAO(conn);
        try {
            List<EmailQueue> mq = emailDAO.retrieveEmailQueue();

            for (EmailQueue task : mq) {
                try {
                    boolean sent = emailUtility.sendEmail(task.getSupplierEmail(),
                            task.getSubject(),
                            task.getEmailBody());
                    if (sent) {
                        emailDAO.markAsSent(task.getEmailID());
                        System.out.println("Email sent successfully to: " + task.getSupplierEmail());
                    } else {
                        emailDAO.markAsFailed(task.getEmailID(), "Failed to send email");
                        System.err.println("Failed to send email to: " + task.getSupplierEmail());
                    }
                } catch (Exception e) {
                    emailDAO.markAsFailed(task.getEmailID(), e.getMessage());
                    System.err.println("Error sending email to " + task.getSupplierEmail() + ": " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in EmailBrokerExecutor: " + e.getMessage());
        }
    }
}