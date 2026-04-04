package com.teesolutions.ipospu.repositories;

import com.teesolutions.ipospu.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CommsRepository {
    public boolean saveOutboundEmail(String recipientEmail, String subject, String body, String purpose) {
        String sql = "INSERT INTO email_outbox(recipient_email, subject, body, purpose, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, recipientEmail);
            ps.setString(2, subject);
            ps.setString(3, body);
            ps.setString(4, purpose);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save outbound email", e);
        }
    }
}
