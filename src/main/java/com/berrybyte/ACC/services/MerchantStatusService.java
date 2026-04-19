package com.berrybyte.ACC.services;

import com.berrybyte.common.DatabaseConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

/**
 * Represents merchant status service.
 */
public class MerchantStatusService {
/**
 * Executes the refresh all merchant statuses workflow.
 * This method coordinates the main operation for this action.
 *
 * @param today today
 * @throws Exception when the operation fails
 */

    public void refreshAllMerchantStatuses(LocalDate today) throws Exception {
        if (today == null) {
            throw new IllegalArgumentException("A date is required.");
        }

        try (Connection conn = new DatabaseConnection().getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {
                String sql = """
                        SELECT MerchantId
                        FROM MerchantAccounts
                        ORDER BY MerchantId
                        """;

                try (PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        refreshMerchantStatus(conn, rs.getInt("MerchantId"), today);
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        }
    }
/**
 * Executes the refresh merchant status workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param today today
 * @throws Exception when the operation fails
 */

/**
 * Executes the refresh merchant status workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param today today
 * @throws Exception when the operation fails
 */
    public void refreshMerchantStatus(int merchantId, LocalDate today) throws Exception {
        try (Connection conn = new DatabaseConnection().getConnection()) {
            refreshMerchantStatus(conn, merchantId, today);
        }
    }
/**
 * Executes the refresh merchant status workflow.
 * This method coordinates the main operation for this action.
 *
 * @param conn conn
 * @param merchantId merchant id
 * @param today today
 * @throws Exception when the operation fails
 */

/**
 * Executes the refresh merchant status workflow.
 * This method coordinates the main operation for this action.
 *
 * @param conn conn
 * @param merchantId merchant id
 * @param today today
 * @throws Exception when the operation fails
 */
    public void refreshMerchantStatus(Connection conn, int merchantId, LocalDate today) throws Exception {
        if (conn == null) {
            throw new IllegalArgumentException("A database connection is required.");
        }
        if (merchantId <= 0) {
            throw new IllegalArgumentException("Invalid merchant id.");
        }
        if (today == null) {
            throw new IllegalArgumentException("A date is required.");
        }

        String currentStatus = getCurrentStatus(conn, merchantId);
        MerchantStatusAssessment assessment = assessOutstandingDebt(conn, merchantId, today);
        String nextStatus = determineNextStatus(currentStatus, assessment);

        if (!nextStatus.equalsIgnoreCase(currentStatus)) {
            updateMerchantStatus(conn, merchantId, nextStatus);
        }
    }
/**
 * Performs restore default state.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @return result value
 * @throws Exception when the operation fails
 */

/**
 * Performs restore default state.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @return result value
 * @throws Exception when the operation fails
 */
    public boolean restoreDefaultState(int merchantId) throws Exception {
        if (merchantId <= 0) {
            throw new IllegalArgumentException("Invalid merchant id.");
        }

        try (Connection conn = new DatabaseConnection().getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {
                boolean restored = restoreDefaultState(conn, merchantId);
                conn.commit();
                return restored;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        }
    }
/**
 * Performs restore default state.
 * This method coordinates the main operation for this action.
 *
 * @param conn conn
 * @param merchantId merchant id
 * @return result value
 * @throws Exception when the operation fails
 */

/**
 * Performs restore default state.
 * This method coordinates the main operation for this action.
 *
 * @param conn conn
 * @param merchantId merchant id
 * @return result value
 * @throws Exception when the operation fails
 */
    public boolean restoreDefaultState(Connection conn, int merchantId) throws Exception {
        if (conn == null) {
            throw new IllegalArgumentException("A database connection is required.");
        }
        if (merchantId <= 0) {
            throw new IllegalArgumentException("Invalid merchant id.");
        }

        String currentStatusSql = """
                SELECT AccountStatus
                FROM MerchantAccounts
                WHERE MerchantId = ?
                FOR UPDATE
                """;

        try (PreparedStatement ps = conn.prepareStatement(currentStatusSql)) {
            ps.setInt(1, merchantId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Merchant account not found.");
                }

                String currentStatus = rs.getString("AccountStatus");
                if (!"IN_DEFAULT".equalsIgnoreCase(currentStatus)) {
                    return false;
                }
            }
        }

        updateMerchantStatus(conn, merchantId, "NORMAL");
        return true;
    }
/**
 * Performs get current status.
 * This method coordinates the main operation for this action.
 *
 * @param conn conn
 * @param merchantId merchant id
 * @return result value
 * @throws Exception when the operation fails
 */

/**
 * Performs get current status.
 * This method coordinates the main operation for this action.
 *
 * @param conn conn
 * @param merchantId merchant id
 * @return result value
 * @throws Exception when the operation fails
 */
    private String getCurrentStatus(Connection conn, int merchantId) throws Exception {
        String sql = """
                SELECT AccountStatus
                FROM MerchantAccounts
                WHERE MerchantId = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, merchantId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String accountStatus = rs.getString("AccountStatus");
                    return accountStatus == null || accountStatus.isBlank()
                            ? "NORMAL"
                            : accountStatus.trim().toUpperCase();
                }
            }
        }

        throw new IllegalArgumentException("Merchant account not found.");
    }
/**
 * Performs assess outstanding debt.
 * This method coordinates the main operation for this action.
 *
 * @param conn conn
 * @param merchantId merchant id
 * @param today today
 * @return result value
 * @throws Exception when the operation fails
 */

/**
 * Performs assess outstanding debt.
 * This method coordinates the main operation for this action.
 *
 * @param conn conn
 * @param merchantId merchant id
 * @param today today
 * @return result value
 * @throws Exception when the operation fails
 */
    private MerchantStatusAssessment assessOutstandingDebt(Connection conn, int merchantId, LocalDate today) throws Exception {
        String sql = """
                SELECT COALESCE(SUM(COALESCE(i.OutstandingBalance, i.TotalAmount - COALESCE(i.AmountPaid, 0))), 0) AS OutstandingDebt,
                       MAX(CASE WHEN DATEDIFF(?, i.DueDate) >= 29 THEN 1 ELSE 0 END) AS HasDefaultDebt,
                       MAX(CASE WHEN DATEDIFF(?, i.DueDate) BETWEEN 15 AND 28 THEN 1 ELSE 0 END) AS HasSuspensionDebt
                FROM Invoices i
                WHERE i.MerchantId = ?
                  AND i.DueDate < ?
                  AND UPPER(COALESCE(i.PaymentStatus, 'PENDING')) IN ('PENDING', 'PARTIAL')
                  AND COALESCE(i.OutstandingBalance, i.TotalAmount - COALESCE(i.AmountPaid, 0)) > 0
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(today));
            ps.setDate(2, Date.valueOf(today));
            ps.setInt(3, merchantId);
            ps.setDate(4, Date.valueOf(today));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MerchantStatusAssessment(
                            normalizeCurrency(rs.getBigDecimal("OutstandingDebt")),
                            rs.getInt("HasDefaultDebt") == 1,
                            rs.getInt("HasSuspensionDebt") == 1);
                }
            }
        }

        return new MerchantStatusAssessment(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), false, false);
    }
/**
 * Performs determine next status.
 *
 * @param currentStatus current status
 * @param assessment assessment
 * @return result value
 */

/**
 * Performs determine next status.
 *
 * @param currentStatus current status
 * @param assessment assessment
 * @return result value
 */
    private String determineNextStatus(String currentStatus, MerchantStatusAssessment assessment) {
        boolean isCurrentlyInDefault = "IN_DEFAULT".equalsIgnoreCase(currentStatus);

        if (isCurrentlyInDefault) {
            return "IN_DEFAULT";
        }

        if (assessment.hasDefaultDebt()) {
            return "IN_DEFAULT";
        }
        if (assessment.hasSuspensionDebt()) {
            return "SUSPENDED";
        }
        return "NORMAL";
    }
/**
 * Executes the update merchant status workflow.
 * This method coordinates the main operation for this action.
 *
 * @param conn conn
 * @param merchantId merchant id
 * @param accountStatus account status
 * @throws Exception when the operation fails
 */

/**
 * Executes the update merchant status workflow.
 * This method coordinates the main operation for this action.
 *
 * @param conn conn
 * @param merchantId merchant id
 * @param accountStatus account status
 * @throws Exception when the operation fails
 */
    private void updateMerchantStatus(Connection conn, int merchantId, String accountStatus) throws Exception {
        String sql = """
                UPDATE MerchantAccounts
                SET AccountStatus = ?
                WHERE MerchantId = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountStatus);
            ps.setInt(2, merchantId);
            ps.executeUpdate();
        }
    }
/**
 * Performs normalize currency.
 *
 * @param value value
 * @return result value
 */

/**
 * Performs normalize currency.
 *
 * @param value value
 * @return result value
 */
    private BigDecimal normalizeCurrency(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }

/**
 * Represents immutable data for merchant status assessment.
 */
    private record MerchantStatusAssessment(BigDecimal outstandingDebt,
                                            boolean hasDefaultDebt,
                                            boolean hasSuspensionDebt) { }
}
