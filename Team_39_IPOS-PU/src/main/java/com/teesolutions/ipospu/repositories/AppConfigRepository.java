package com.teesolutions.ipospu.repositories;

import com.teesolutions.ipospu.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class AppConfigRepository {

    public Optional<String> findValue(String configKey) {
        String sql = "SELECT config_value FROM app_config WHERE config_key = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, configKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString("config_value"));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to read app_config: " + configKey, e);
        }
    }

    
    public double getDouble(String configKey, double defaultValue) {
        Optional<String> raw = findValue(configKey);
        if (raw.isEmpty() || raw.get().isBlank()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(raw.get().trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
