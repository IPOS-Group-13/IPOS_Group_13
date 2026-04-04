package com.teesolutions.ipospu.repositories;

import com.teesolutions.ipospu.dto.CommercialApplicationDto;
import com.teesolutions.ipospu.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CommercialApplicationRepository {
    public boolean save(CommercialApplicationDto dto) {
        String sql = "INSERT INTO commercial_applications(company_registration_number, director_name, business_type, address, email, submitted_at, submission_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'SUBMITTED_TO_SA')";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dto.getCompanyRegistrationNumber());
            ps.setString(2, dto.getDirectorName());
            ps.setString(3, dto.getBusinessType());
            ps.setString(4, dto.getAddress());
            ps.setString(5, dto.getEmail());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save commercial application", e);
        }
    }
}
