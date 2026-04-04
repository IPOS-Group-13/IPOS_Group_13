package com.teesolutions.ipospu.repositories;

import com.teesolutions.ipospu.dto.CartLineDto;
import com.teesolutions.ipospu.models.Order;
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
import java.util.Optional;

public class OrderRepository {
    public void createOrderWithItems(
            String orderId,
            int userId,
            double totalAmount,
            String status,
            String trackingCode,
            String deliveryAddress,
            List<CartLineDto> lines,
            Map<String, Double> unitPriceByProduct,
            Map<String, Double> discountByProduct
    ) {
        String orderSql = "INSERT INTO orders(id, user_id, total_amount, status, tracking_code, delivery_address, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items(order_id, product_id, quantity, unit_price, discount_percent, line_total) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement orderPs = connection.prepareStatement(orderSql);
                 PreparedStatement itemPs = connection.prepareStatement(itemSql)) {
                orderPs.setString(1, orderId);
                orderPs.setInt(2, userId);
                orderPs.setDouble(3, totalAmount);
                orderPs.setString(4, status);
                orderPs.setString(5, trackingCode);
                orderPs.setString(6, deliveryAddress);
                orderPs.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                orderPs.executeUpdate();

                for (CartLineDto line : lines) {
                    double unit = unitPriceByProduct.getOrDefault(line.getProductId(), 0.0);
                    double discount = discountByProduct.getOrDefault(line.getProductId(), 0.0);
                    double discountedUnit = unit * (1 - discount / 100.0);
                    double lineTotal = discountedUnit * line.getQuantity();
                    itemPs.setString(1, orderId);
                    itemPs.setString(2, line.getProductId());
                    itemPs.setInt(3, line.getQuantity());
                    itemPs.setDouble(4, unit);
                    itemPs.setDouble(5, discount);
                    itemPs.setDouble(6, lineTotal);
                    itemPs.addBatch();
                }
                itemPs.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create order and items", e);
        }
    }

    public List<Order> findOrdersByUser(int userId) {
        String sql = "SELECT id, user_id, total_amount, status, tracking_code, created_at FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(new Order(
                            rs.getString("id"),
                            rs.getInt("user_id"),
                            rs.getDouble("total_amount"),
                            rs.getString("status"),
                            rs.getString("tracking_code"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
            }
            return orders;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to query user orders", e);
        }
    }

    public void markOrderStatus(String orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update order status", e);
        }
    }

    public Optional<Order> findOrderByTracking(String customerEmail, String trackingCode) {
        String sql = "SELECT o.id, o.user_id, o.total_amount, o.status, o.tracking_code, o.created_at " +
                "FROM orders o " +
                "JOIN payments p ON p.order_id = o.id " +
                "WHERE LOWER(p.payee_details) = LOWER(?) AND UPPER(o.tracking_code) = UPPER(?) " +
                "ORDER BY o.created_at DESC LIMIT 1";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, customerEmail);
            ps.setString(2, trackingCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new Order(
                        rs.getString("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getString("tracking_code"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to lookup order by tracking code", e);
        }
    }
}
