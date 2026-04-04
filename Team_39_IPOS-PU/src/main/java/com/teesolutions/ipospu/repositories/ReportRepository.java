package com.teesolutions.ipospu.repositories;

import com.teesolutions.ipospu.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ReportRepository {
    public List<Map<String, Object>> salesReport(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT oi.product_id, p.name, SUM(oi.quantity) AS qty_sold, " +
                "AVG(oi.unit_price) AS unit_price, SUM(oi.line_total) AS total_price " +
                "FROM order_items oi " +
                "JOIN orders o ON o.id = oi.order_id " +
                "JOIN products p ON p.id = oi.product_id " +
                "WHERE o.status <> 'VOID' AND o.created_at >= ? AND o.created_at < ? " +
                "GROUP BY oi.product_id, p.name ORDER BY p.name";
        return queryRows(sql, start, end);
    }

    public List<Map<String, Object>> campaignsReport(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT id, name, start_time, end_time, status FROM campaigns WHERE start_time >= ? AND start_time < ? ORDER BY start_time DESC";
        return queryRows(sql, start, end);
    }

    public List<Map<String, Object>> engagementReport(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT c.id AS campaign_id, c.name AS campaign_name, cm.product_id, p.name AS product_name, " +
                "cm.campaign_hits, cm.item_purchased_count, " +
                "CASE WHEN cm.campaign_hits = 0 THEN 0 ELSE ROUND((cm.item_purchased_count / cm.campaign_hits) * 100, 2) END AS conversion_rate " +
                "FROM campaign_metrics cm " +
                "JOIN campaigns c ON c.id = cm.campaign_id " +
                "JOIN products p ON p.id = cm.product_id " +
                "WHERE c.start_time < ? AND c.end_time >= ? " +
                "ORDER BY c.id, cm.product_id";
        return queryRows(sql, end, start);
    }

    private List<Map<String, Object>> queryRows(String sql, LocalDateTime start, LocalDateTime end) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                int count = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= count; i++) {
                        row.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
                    }
                    rows.add(row);
                }
            }
            return rows;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to execute report query", e);
        }
    }
}
