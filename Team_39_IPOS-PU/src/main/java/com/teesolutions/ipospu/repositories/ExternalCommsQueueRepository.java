package com.teesolutions.ipospu.repositories;

import com.teesolutions.ipospu.dto.ExternalCommsQueueEntry;
import com.teesolutions.ipospu.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class ExternalCommsQueueRepository {

    private static final AtomicBoolean LOGGED_MISSING_TABLE = new AtomicBoolean();

    public List<ExternalCommsQueueEntry> fetchPending(int limit) {
        String sql = """
                SELECT id, recipient_email, subject, body, purpose, source_system, reference_key
                FROM external_comms_queue
                WHERE consumed_at IS NULL
                ORDER BY id ASC
                LIMIT ?
                """;
        List<ExternalCommsQueueEntry> out = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, Math.min(limit, 200)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new ExternalCommsQueueEntry(
                            rs.getInt("id"),
                            rs.getString("recipient_email"),
                            rs.getString("subject"),
                            rs.getString("body"),
                            rs.getString("purpose"),
                            rs.getString("source_system"),
                            rs.getString("reference_key")
                    ));
                }
            }
            return out;
        } catch (SQLException e) {
            if (isMissingTable(e)) {
                if (LOGGED_MISSING_TABLE.compareAndSet(false, true)) {
                    System.err.println(
                            "[PU] external_comms_queue is missing — run docs/sql/pu_external_comms_queue.sql on the database (SHARED deployments).");
                }
                return List.of();
            }
            throw new IllegalStateException("Failed to read external_comms_queue", e);
        }
    }

    public void markConsumed(int id) {
        String sql = "UPDATE external_comms_queue SET consumed_at = ? WHERE id = ? AND consumed_at IS NULL";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to mark external_comms_queue row consumed: " + id, e);
        }
    }

    private static boolean isMissingTable(SQLException e) {
        if (e.getErrorCode() == 1146) {
            return true;
        }
        String m = e.getMessage();
        return m != null && (m.contains("doesn't exist") || m.contains("Unknown table"));
    }
}
