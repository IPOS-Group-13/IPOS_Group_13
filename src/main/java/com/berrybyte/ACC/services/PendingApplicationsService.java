package com.berrybyte.ACC.services;

import com.berrybyte.ACC.model.PendingApplicationRow;
import com.berrybyte.common.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents pending applications service.
 */
public class PendingApplicationsService {
/**
 * Performs get pending applications.
 * This method coordinates the main operation for this action.
 *
 * @param keyword keyword
 * @return result value
 * @throws Exception when the operation fails
 */

    public List<PendingApplicationRow> getPendingApplications(String keyword) throws Exception {
        List<PendingApplicationRow> applications = new ArrayList<>();

        String sql = """
                SELECT id, director_name, company_name, company_registration_number,
                       phone_number, email, address,
                       DATE_FORMAT(submitted_at, '%Y-%m-%d %H:%i') AS submitted_at
                FROM pu_commercial_application_intake
                WHERE director_name LIKE ?
                   OR company_name LIKE ?
                   OR company_registration_number LIKE ?
                   OR phone_number LIKE ?
                   OR email LIKE ?
                ORDER BY submitted_at DESC
                """;

        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setString(5, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    applications.add(new PendingApplicationRow(
                            rs.getInt("id"),
                            rs.getString("director_name"),
                            rs.getString("company_name"),
                            rs.getString("company_registration_number"),
                            rs.getString("phone_number"),
                            rs.getString("email"),
                            rs.getString("submitted_at"),
                            rs.getString("address")
                    ));
                }
            }
        }

        return applications;
    }
/**
 * Performs reject application.
 * This method coordinates the main operation for this action.
 *
 * @param id id
 * @throws Exception when the operation fails
 */

    public void rejectApplication(int id) throws Exception {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid application ID.");
        }

        String sql = "DELETE FROM pu_commercial_application_intake WHERE id = ?";

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Application not found.");
            }
        }
    }
}
