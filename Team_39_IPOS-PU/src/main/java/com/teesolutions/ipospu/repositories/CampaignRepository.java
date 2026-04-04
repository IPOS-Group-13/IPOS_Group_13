package com.teesolutions.ipospu.repositories;

import com.teesolutions.ipospu.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

public class CampaignRepository {
    public boolean hasActiveCampaigns() {
        String sql = "SELECT COUNT(*) FROM campaigns WHERE status = 'ACTIVE' AND NOW() BETWEEN start_time AND end_time";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            syncCampaignStatuses(connection);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check active campaigns", e);
        }
    }

    public Map<String, Double> getActiveDiscountByProduct() {
        String sql = "SELECT ci.product_id, MAX(ci.discount_percent) AS discount " +
                "FROM campaign_items ci " +
                "JOIN campaigns c ON c.id = ci.campaign_id " +
                "WHERE c.status = 'ACTIVE' AND NOW() BETWEEN c.start_time AND c.end_time " +
                "GROUP BY ci.product_id";
        Map<String, Double> map = new HashMap<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            syncCampaignStatuses(connection);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("product_id"), rs.getDouble("discount"));
                }
            }
            return map;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to get active discounts", e);
        }
    }

    public int createCampaign(String name, LocalDateTime start, LocalDateTime end) {
        String insert = "INSERT INTO campaigns(name, start_time, end_time, status) VALUES(?, ?, ?, ?)";
        String status = resolveCampaignStatus(start, end);
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setTimestamp(2, utcTimestamp(start));
            ps.setTimestamp(3, utcTimestamp(end));
            ps.setString(4, status);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new IllegalStateException("Campaign key not generated");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create campaign", e);
        }
    }

    public boolean isProductInOverlappingCampaign(String productId, LocalDateTime newStart, LocalDateTime newEnd) {
        return isProductInOverlappingCampaign(productId, newStart, newEnd, null);
    }

    public boolean isProductInOverlappingCampaign(String productId, LocalDateTime newStart, LocalDateTime newEnd, Integer excludedCampaignId) {
        String sql = "SELECT COUNT(*) " +
                "FROM campaign_items ci JOIN campaigns c ON ci.campaign_id = c.id " +
                "WHERE ci.product_id = ? AND c.status <> 'CANCELLED' " +
                (excludedCampaignId != null ? "AND c.id <> ? " : "") +
                "AND NOT (? > c.end_time OR ? < c.start_time)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            syncCampaignStatuses(connection);
            ps.setString(1, productId);
            int nextIndex = 2;
            if (excludedCampaignId != null) {
                ps.setInt(nextIndex++, excludedCampaignId);
            }
            ps.setTimestamp(nextIndex++, utcTimestamp(newStart));
            ps.setTimestamp(nextIndex, utcTimestamp(newEnd));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed overlap check", e);
        }
    }

    public void addCampaignItem(int campaignId, String productId, double discountPercent) {
        String sql = "INSERT INTO campaign_items(campaign_id, product_id, discount_percent) VALUES(?, ?, ?)";
        String metricSql = "INSERT INTO campaign_metrics(campaign_id, product_id, campaign_hits, item_added_count, item_purchased_count) VALUES(?, ?, 0, 0, 0)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps1 = connection.prepareStatement(sql);
             PreparedStatement ps2 = connection.prepareStatement(metricSql)) {
            ps1.setInt(1, campaignId);
            ps1.setString(2, productId);
            ps1.setDouble(3, discountPercent);
            ps1.executeUpdate();

            ps2.setInt(1, campaignId);
            ps2.setString(2, productId);
            ps2.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to add campaign item", e);
        }
    }

    public void updateCampaign(int campaignId, String name, LocalDateTime start, LocalDateTime end) {
        String sql = "UPDATE campaigns SET name = ?, start_time = ?, end_time = ?, status = ? WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setTimestamp(2, utcTimestamp(start));
            ps.setTimestamp(3, utcTimestamp(end));
            ps.setString(4, resolveCampaignStatus(start, end));
            ps.setInt(5, campaignId);
            if (ps.executeUpdate() != 1) {
                throw new IllegalStateException("Campaign not found");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update campaign", e);
        }
    }

    public void replaceCampaignItems(int campaignId, Map<String, Double> discountsByProduct) {
        if (discountsByProduct == null || discountsByProduct.isEmpty()) {
            throw new IllegalArgumentException("Please provide at least one campaign item");
        }
        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                syncCampaignStatuses(connection);
                deleteRemovedRows(connection, "campaign_metrics", campaignId, discountsByProduct.keySet());
                deleteRemovedRows(connection, "campaign_items", campaignId, discountsByProduct.keySet());

                String upsertItemSql = "INSERT INTO campaign_items(campaign_id, product_id, discount_percent) VALUES(?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE discount_percent = VALUES(discount_percent)";
                String upsertMetricSql = "INSERT INTO campaign_metrics(campaign_id, product_id, campaign_hits, item_added_count, item_purchased_count) " +
                        "VALUES(?, ?, 0, 0, 0) ON DUPLICATE KEY UPDATE product_id = VALUES(product_id)";
                try (PreparedStatement itemPs = connection.prepareStatement(upsertItemSql);
                     PreparedStatement metricPs = connection.prepareStatement(upsertMetricSql)) {
                    for (Map.Entry<String, Double> entry : discountsByProduct.entrySet()) {
                        itemPs.setInt(1, campaignId);
                        itemPs.setString(2, entry.getKey());
                        itemPs.setDouble(3, entry.getValue());
                        itemPs.addBatch();

                        metricPs.setInt(1, campaignId);
                        metricPs.setString(2, entry.getKey());
                        metricPs.addBatch();
                    }
                    itemPs.executeBatch();
                    metricPs.executeBatch();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to replace campaign items", e);
        }
    }

    public void cancelCampaign(int campaignId) {
        String sql = "UPDATE campaigns SET status = 'CANCELLED' WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, campaignId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to cancel campaign", e);
        }
    }

    public void terminateCampaignEarly(int campaignId) {
        String sql = "UPDATE campaigns " +
                "SET start_time = CASE WHEN start_time > NOW() THEN NOW() ELSE start_time END, " +
                "end_time = NOW(), status = 'ENDED' WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, campaignId);
            if (ps.executeUpdate() != 1) {
                throw new IllegalStateException("Campaign not found");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to terminate campaign", e);
        }
    }

    public void deleteCampaign(int campaignId) {
        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement deleteMetrics = connection.prepareStatement("DELETE FROM campaign_metrics WHERE campaign_id = ?");
                 PreparedStatement deleteItems = connection.prepareStatement("DELETE FROM campaign_items WHERE campaign_id = ?");
                 PreparedStatement deleteCampaign = connection.prepareStatement("DELETE FROM campaigns WHERE id = ?")) {
                deleteMetrics.setInt(1, campaignId);
                deleteMetrics.executeUpdate();

                deleteItems.setInt(1, campaignId);
                deleteItems.executeUpdate();

                deleteCampaign.setInt(1, campaignId);
                if (deleteCampaign.executeUpdate() != 1) {
                    throw new IllegalStateException("Campaign not found");
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete campaign", e);
        }
    }

    public void incrementCampaignHits() {
        String sql = "UPDATE campaign_metrics cm " +
                "JOIN campaigns c ON c.id = cm.campaign_id " +
                "SET cm.campaign_hits = cm.campaign_hits + 1 " +
                "WHERE c.status = 'ACTIVE' AND NOW() BETWEEN c.start_time AND c.end_time";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            syncCampaignStatuses(connection);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to increment campaign hits", e);
        }
    }

    public void incrementAdded(String productId, int qty) {
        String sql = "UPDATE campaign_metrics cm " +
                "JOIN campaigns c ON c.id = cm.campaign_id " +
                "SET cm.item_added_count = cm.item_added_count + ? " +
                "WHERE cm.product_id = ? AND c.status = 'ACTIVE' AND NOW() BETWEEN c.start_time AND c.end_time";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            syncCampaignStatuses(connection);
            ps.setInt(1, qty);
            ps.setString(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to increment item added counter", e);
        }
    }

    public void incrementPurchased(String productId, int qty) {
        String sql = "UPDATE campaign_metrics cm " +
                "JOIN campaigns c ON c.id = cm.campaign_id " +
                "SET cm.item_purchased_count = cm.item_purchased_count + ? " +
                "WHERE cm.product_id = ? AND c.status = 'ACTIVE' AND NOW() BETWEEN c.start_time AND c.end_time";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            syncCampaignStatuses(connection);
            ps.setInt(1, qty);
            ps.setString(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to increment item purchased counter", e);
        }
    }

    public List<Map<String, Object>> listCampaigns() {
        String sql = "SELECT id, name, start_time, end_time, status FROM campaigns ORDER BY start_time DESC";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            syncCampaignStatuses(connection);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("name", rs.getString("name"));
                    row.put("start_time", rs.getTimestamp("start_time"));
                    row.put("end_time", rs.getTimestamp("end_time"));
                    row.put("status", rs.getString("status"));
                    rows.add(row);
                }
            }
            return rows;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to list campaigns", e);
        }
    }

    public List<Map<String, Object>> listPromotionItems() {
        String sql = "SELECT c.id AS campaign_id, c.name AS campaign_name, c.start_time, c.end_time, c.status, " +
                "ci.product_id, ci.discount_percent, p.name AS product_name, p.description, p.retail_price, p.stock_quantity " +
                "FROM campaigns c " +
                "LEFT JOIN campaign_items ci ON ci.campaign_id = c.id " +
                "LEFT JOIN products p ON p.id = ci.product_id " +
                "WHERE c.status IN ('ACTIVE', 'UPCOMING') " +
                "ORDER BY c.start_time, c.id, p.name";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            syncCampaignStatuses(connection);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("campaign_id", rs.getInt("campaign_id"));
                    row.put("campaign_name", rs.getString("campaign_name"));
                    row.put("start_time", rs.getTimestamp("start_time"));
                    row.put("end_time", rs.getTimestamp("end_time"));
                    row.put("status", rs.getString("status"));
                    row.put("product_id", rs.getString("product_id"));
                    row.put("discount_percent", rs.getObject("discount_percent"));
                    row.put("product_name", rs.getString("product_name"));
                    row.put("description", rs.getString("description"));
                    row.put("retail_price", rs.getObject("retail_price"));
                    row.put("stock_quantity", rs.getObject("stock_quantity"));
                    rows.add(row);
                }
            }
            return rows;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to list promotion items", e);
        }
    }

    public Optional<Map<String, Object>> findCampaign(int campaignId) {
        String sql = "SELECT id, name, start_time, end_time, status FROM campaigns WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            syncCampaignStatuses(connection);
            ps.setInt(1, campaignId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("name", rs.getString("name"));
                row.put("start_time", rs.getTimestamp("start_time"));
                row.put("end_time", rs.getTimestamp("end_time"));
                row.put("status", rs.getString("status"));
                return Optional.of(row);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find campaign", e);
        }
    }

    public List<Map<String, Object>> listCampaignItems(int campaignId) {
        String sql = "SELECT product_id, discount_percent FROM campaign_items WHERE campaign_id = ? ORDER BY product_id";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, campaignId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("product_id", rs.getString("product_id"));
                    row.put("discount_percent", rs.getDouble("discount_percent"));
                    rows.add(row);
                }
            }
            return rows;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to list campaign items", e);
        }
    }

    private void deleteRemovedRows(Connection connection, String tableName, int campaignId, Set<String> productIds) throws SQLException {
        StringBuilder sql = new StringBuilder("DELETE FROM ").append(tableName).append(" WHERE campaign_id = ?");
        if (!productIds.isEmpty()) {
            sql.append(" AND product_id NOT IN (");
            for (int i = 0; i < productIds.size(); i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append("?");
            }
            sql.append(")");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setInt(1, campaignId);
            int index = 2;
            for (String productId : productIds) {
                ps.setString(index++, productId);
            }
            ps.executeUpdate();
        }
    }

    private static Timestamp utcTimestamp(LocalDateTime ldt) {
        return Timestamp.from(ldt.atZone(ZoneOffset.UTC).toInstant());
    }

    private String resolveCampaignStatus(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        if (end.isBefore(now)) {
            return "ENDED";
        }
        if (start.isAfter(now)) {
            return "UPCOMING";
        }
        return "ACTIVE";
    }

    private void syncCampaignStatuses(Connection connection) throws SQLException {
        try (PreparedStatement ended = connection.prepareStatement(
                "UPDATE campaigns SET status = 'ENDED' WHERE status <> 'CANCELLED' AND end_time < NOW()");
             PreparedStatement upcoming = connection.prepareStatement(
                     "UPDATE campaigns SET status = 'UPCOMING' WHERE status <> 'CANCELLED' AND start_time > NOW()");
             PreparedStatement active = connection.prepareStatement(
                     "UPDATE campaigns SET status = 'ACTIVE' WHERE status <> 'CANCELLED' AND start_time <= NOW() AND end_time >= NOW()")) {
            ended.executeUpdate();
            upcoming.executeUpdate();
            active.executeUpdate();
        }
    }
}
