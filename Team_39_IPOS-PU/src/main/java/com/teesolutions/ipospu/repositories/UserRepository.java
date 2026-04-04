package com.teesolutions.ipospu.repositories;

import com.teesolutions.ipospu.models.User;
import com.teesolutions.ipospu.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

public class UserRepository {
    private static final String GUEST_CHECKOUT_EMAIL = "guest.checkout@ipos.local";

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, email, member_type, first_login_required, completed_order_count FROM users WHERE email = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("member_type"),
                            rs.getBoolean("first_login_required"),
                            rs.getInt("completed_order_count")
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find user by email", e);
        }
    }

    public Optional<Integer> authenticate(String loginInput, String passwordHash, String rawPassword) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(resolveAuthenticateSql(connection))) {
            ps.setString(1, loginInput);
            if (supportsLoginAlias(connection)) {
                ps.setString(2, loginInput);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                String stored = rs.getString("password_hash");
                if (passwordHash.equals(stored) || rawPassword.equals(stored)) {
                    return Optional.of(rs.getInt("id"));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to authenticate user", e);
        }
    }

    public Optional<User> findById(int userId) {
        String sql = "SELECT id, email, member_type, first_login_required, completed_order_count FROM users WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("member_type"),
                            rs.getBoolean("first_login_required"),
                            rs.getInt("completed_order_count")
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find user by id", e);
        }
    }

    public int createNonCommercialUser(String email, String passwordHash) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(resolveCreateNonCommercialSql(connection), Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new IllegalStateException("No generated key for user creation");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to create non-commercial user", e);
        }
    }

    public void updatePasswordAndClearFirstLogin(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ?, first_login_required = FALSE WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update user password", e);
        }
    }

    public void incrementCompletedOrders(int userId) {
        String sql = "UPDATE users SET completed_order_count = completed_order_count + 1 WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to increment completed orders", e);
        }
    }

    public int ensureGuestCheckoutUser(String passwordHash) {
        Optional<User> existing = findByEmail(GUEST_CHECKOUT_EMAIL);
        if (existing.isPresent()) {
            return existing.get().getUserId();
        }

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(resolveGuestInsertSql(connection), Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, GUEST_CHECKOUT_EMAIL);
            ps.setString(2, passwordHash);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new IllegalStateException("No generated key for guest checkout user");
        } catch (SQLException e) {
            Optional<User> recovered = findByEmail(GUEST_CHECKOUT_EMAIL);
            if (recovered.isPresent()) {
                return recovered.get().getUserId();
            }
            throw new IllegalStateException("Failed to ensure guest checkout user", e);
        }
    }

    private String resolveAuthenticateSql(Connection connection) throws SQLException {
        if (supportsLoginAlias(connection)) {
            return "SELECT id, password_hash FROM users WHERE email = ? OR login_alias = ?";
        }
        return "SELECT id, password_hash FROM users WHERE email = ?";
    }

    private String resolveCreateNonCommercialSql(Connection connection) throws SQLException {
        if (supportsLoginAlias(connection)) {
            return "INSERT INTO users (email, login_alias, password_hash, member_type, account_status, first_login_required, completed_order_count, created_at) " +
                    "VALUES (?, NULL, ?, 'NON_COMMERCIAL', 'ACTIVE', TRUE, 0, ?)";
        }
        return "INSERT INTO users (email, password_hash, member_type, account_status, first_login_required, completed_order_count, created_at) " +
                "VALUES (?, ?, 'NON_COMMERCIAL', 'ACTIVE', TRUE, 0, ?)";
    }

    private String resolveGuestInsertSql(Connection connection) throws SQLException {
        if (supportsLoginAlias(connection)) {
            return "INSERT INTO users (email, login_alias, password_hash, member_type, account_status, first_login_required, completed_order_count, created_at) " +
                    "VALUES (?, NULL, ?, 'GUEST', 'ACTIVE', FALSE, 0, ?)";
        }
        return "INSERT INTO users (email, password_hash, member_type, account_status, first_login_required, completed_order_count, created_at) " +
                "VALUES (?, ?, 'GUEST', 'ACTIVE', FALSE, 0, ?)";
    }

    private boolean supportsLoginAlias(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet columns = metaData.getColumns(connection.getCatalog(), null, "users", "login_alias")) {
            return columns.next();
        }
    }
}
