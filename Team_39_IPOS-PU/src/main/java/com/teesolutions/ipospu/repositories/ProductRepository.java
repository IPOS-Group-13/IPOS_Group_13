package com.teesolutions.ipospu.repositories;

import com.teesolutions.ipospu.dto.InventoryItemDto;
import com.teesolutions.ipospu.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {
    public List<InventoryItemDto> findActiveProducts(String keyword) {
        String sql = "SELECT id, name, description, retail_price, stock_quantity " +
                "FROM products WHERE is_active = TRUE AND stock_quantity > 0 " +
                "AND (LOWER(name) LIKE ? OR LOWER(description) LIKE ?) ORDER BY name";
        String pattern = "%" + keyword.toLowerCase() + "%";
        List<InventoryItemDto> products = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(new InventoryItemDto(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("retail_price"),
                            rs.getInt("stock_quantity")
                    ));
                }
            }
            return products;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch products", e);
        }
    }

    public Optional<InventoryItemDto> findById(String productId) {
        String sql = "SELECT id, name, description, retail_price, stock_quantity FROM products WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new InventoryItemDto(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("retail_price"),
                        rs.getInt("stock_quantity")
                ));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to fetch product by id", e);
        }
    }

    public boolean hasStock(String productId, int qty) {
        String sql = "SELECT stock_quantity FROM products WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("stock_quantity") >= qty;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed stock check", e);
        }
    }

    public boolean deductStockAtomically(Connection connection, String productId, int qty) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setString(2, productId);
            ps.setInt(3, qty);
            return ps.executeUpdate() == 1;
        }
    }
}
