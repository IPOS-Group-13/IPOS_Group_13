package com.berrybyte.ACC.services;

import com.berrybyte.ACC.model.UserAccountRow;
import com.berrybyte.common.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents delete account service.
 */
public class DeleteAccountService {
/**
 * Performs get user account.
 * This method coordinates the main operation for this action.
 *
 * @param userId user id
 * @return result value
 * @throws Exception when the operation fails
 */

/**
 * Performs get user account.
 * This method coordinates the main operation for this action.
 *
 * @param userId user id
 * @return result value
 * @throws Exception when the operation fails
 */
    public UserAccountRow getUserAccount(int userId) throws Exception {
        String sql = """
                SELECT UserId, Name, Username, Role
                FROM Users
                WHERE UserId = ?
                """;

        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection conn = connectNow.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserAccountRow(
                            rs.getInt("UserId"),
                            rs.getString("Name"),
                            rs.getString("Username"),
                            rs.getString("Role"));
                }
            }
        }
        throw new Exception("Selected user could not be found.");
    }
/**
 * Executes the search users workflow.
 * This method coordinates the main operation for this action.
 *
 * @param searchText search text
 * @return result value
 * @throws Exception when the operation fails
 */

/**
 * Executes the search users workflow.
 * This method coordinates the main operation for this action.
 *
 * @param searchText search text
 * @return result value
 * @throws Exception when the operation fails
 */
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
                            rs.getString("Role")));
                }
            }
        }
        return users;
    }
/**
 * Executes the delete user account workflow.
 * This method coordinates the main operation for this action.
 *
 * @param userId user id
 * @throws Exception when the operation fails
 */

/**
 * Executes the delete user account workflow.
 * This method coordinates the main operation for this action.
 *
 * @param userId user id
 * @throws Exception when the operation fails
 */
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
