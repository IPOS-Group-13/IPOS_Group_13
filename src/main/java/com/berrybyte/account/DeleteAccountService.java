package com.berrybyte.account;

import com.berrybyte.common.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DeleteAccountService {

    public List<UserAccountRow> searchUsers(String searchText) throws Exception {
        List<UserAccountRow> users = new ArrayList<>();

        String sql = """
                SELECT UserId, Name, Username, Role
                FROM Users
                WHERE Name LIKE ? OR Username LIKE ?
                ORDER BY Name, UserId
                """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String keyword = "%" + (searchText == null ? "" : searchText.trim()) + "%";
            ps.setString(1, keyword);
            ps.setString(2, keyword);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(new UserAccountRow(
                            rs.getInt("UserId"),
                            rs.getString("Name"),
                            rs.getString("Username"),
                            rs.getString("Role")
                    ));
                }
            }
        }

        return users;
    }

    public void deleteUserAccount(int userId) throws Exception {
        DatabaseConnection connectNow = new DatabaseConnection();

        String getMerchantSql = """
                SELECT MerchantId
                FROM MerchantAccounts
                WHERE UserId = ?
                """;

        String clearCreatedBySql = """
                UPDATE DiscountPlans
                SET CreatedByUserId = NULL
                WHERE CreatedByUserId = ?
                """;

        String deleteTiersSql = """
                DELETE dpt
                FROM DiscountPlanTiers dpt
                INNER JOIN DiscountPlans dp
                    ON dpt.DiscountPlanId = dp.DiscountPlanId
                WHERE dp.MerchantId = ?
                """;

        String deletePlansSql = """
                DELETE FROM DiscountPlans
                WHERE MerchantId = ?
                """;

        String deleteMerchantSql = """
                DELETE FROM MerchantAccounts
                WHERE UserId = ?
                """;

        String deleteUserSql = """
                DELETE FROM Users
                WHERE UserId = ?
                """;

        try (Connection conn = connectNow.getConnection()) {
            conn.setAutoCommit(false);

            try {
                Integer merchantId = null;

                try (PreparedStatement ps = conn.prepareStatement(getMerchantSql)) {
                    ps.setInt(1, userId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            merchantId = rs.getInt("MerchantId");
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(clearCreatedBySql)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                if (merchantId != null) {
                    try (PreparedStatement ps = conn.prepareStatement(deleteTiersSql)) {
                        ps.setInt(1, merchantId);
                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps = conn.prepareStatement(deletePlansSql)) {
                        ps.setInt(1, merchantId);
                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps = conn.prepareStatement(deleteMerchantSql)) {
                        ps.setInt(1, userId);
                        ps.executeUpdate();
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(deleteUserSql)) {
                    ps.setInt(1, userId);
                    int rowsAffected = ps.executeUpdate();

                    if (rowsAffected == 0) {
                        throw new Exception("No user account was deleted.");
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
