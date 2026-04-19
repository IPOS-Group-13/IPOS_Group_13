package com.berrybyte.ACC.services;

import com.berrybyte.ACC.model.DiscountTier;
import com.berrybyte.ACC.util.PhoneNumberRules;
import com.berrybyte.common.DatabaseConnection;

import java.sql.*;
import java.util.List;

/**
 * Represents merchant account service.
 */
public class MerchantAccountService {
/**
 * Executes the create merchant account workflow.
 * This method coordinates the main operation for this action.
 *
 * @param fullName full name
 * @param companyName company name
 * @param username username
 * @param password password
 * @param email email
 * @param phoneNumber phone number
 * @param address address
 * @param accountStatus account status
 * @param creditLimit credit limit
 * @param discountPlanType discount plan type
 * @param discountTiers discount tiers
 * @throws Exception when the operation fails
 */

    public void createMerchantAccount(String fullName, String companyName, String username, String password,
                                      String email, String phoneNumber, String address, String accountStatus,
                                      double creditLimit, String discountPlanType, List<DiscountTier> discountTiers) throws Exception {

        validateMerchantCreationDetails(
                fullName, companyName, username, password, email,
                phoneNumber, address, accountStatus, creditLimit,
                discountPlanType, discountTiers
        );

        DatabaseConnection connectNow = new DatabaseConnection();

        String insertUserSql = """
                INSERT INTO Users
                (Name, Username, Password, Email, PhoneNumber, Role)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String insertMerchantSql = """
                INSERT INTO MerchantAccounts
                (UserId, IPOSAccountNumber, CompanyName, Address, CreditLimit, OutstandingBalance, AccountStatus, IsActivated)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String insertDiscountPlanSql = """
                INSERT INTO DiscountPlans
                (MerchantId, PlanType, IsActive, CreatedByUserId)
                VALUES (?, ?, ?, NULL)
                """;

        String insertTierSql = """
                INSERT INTO DiscountPlanTiers
                (DiscountPlanId, MinOrderValue, MaxOrderValue, DiscountPercent)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = connectNow.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement userPs = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement merchantPs = conn.prepareStatement(insertMerchantSql, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement planPs = conn.prepareStatement(insertDiscountPlanSql, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement tierPs = conn.prepareStatement(insertTierSql))
            {
                userPs.setString(1, fullName.trim());
                userPs.setString(2, username);
                userPs.setString(3, password);
                userPs.setString(4, email);
                userPs.setString(5, PhoneNumberRules.normalizeAndValidate(phoneNumber));
                userPs.setString(6, "MERCHANT");
                userPs.executeUpdate();

                int userId;
                try (ResultSet rs = userPs.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to create merchant user");
                    }
                    userId = rs.getInt(1);
                }

                String accountNumber = generateNextMerchantAccountNumber(conn);

                merchantPs.setInt(1, userId);
                merchantPs.setString(2, accountNumber);
                merchantPs.setString(3, companyName);
                merchantPs.setString(4, address);
                merchantPs.setDouble(5, creditLimit);
                merchantPs.setDouble(6, 0.00);
                merchantPs.setString(7, accountStatus);
                merchantPs.setBoolean(8, true);
                merchantPs.executeUpdate();

                int merchantId;
                try (ResultSet rs = merchantPs.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to create merchant account");
                    }
                    merchantId = rs.getInt(1);
                }

                planPs.setInt(1, merchantId);
                planPs.setString(2, discountPlanType);
                planPs.setBoolean(3, true);
                planPs.executeUpdate();

                int discountPlanId;
                try (ResultSet rs = planPs.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to create discount plan");
                    }
                    discountPlanId = rs.getInt(1);
                }

                for (DiscountTier tier : discountTiers) {
                    tierPs.setInt(1, discountPlanId);
                    tierPs.setDouble(2, tier.getMinOrderValue());

                    if (tier.getMaxOrderValue() == null) {
                        tierPs.setNull(3, Types.DECIMAL);
                    } else {
                        tierPs.setDouble(3, tier.getMaxOrderValue());
                    }

                    tierPs.setDouble(4, tier.getDiscountPercent());
                    tierPs.executeUpdate();
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
/**
 * Executes the update merchant account details workflow.
 * This method coordinates the main operation for this action.
 *
 * @param userId user id
 * @param fullName full name
 * @param companyName company name
 * @param username username
 * @param password password
 * @param email email
 * @param phoneNumber phone number
 * @param address address
 * @param accountStatus account status
 * @param creditLimit credit limit
 * @throws Exception when the operation fails
 */

    public void updateMerchantAccountDetails(int userId, String fullName, String companyName, String username,
                                             String password, String email, String phoneNumber, String address,
                                             String accountStatus, double creditLimit) throws Exception {

        validateMerchantUpdateDetails(fullName, companyName, username, password, email, phoneNumber, address, accountStatus, creditLimit);

        DatabaseConnection connectNow = new DatabaseConnection();

        String updateUserSql = """
                UPDATE Users
                SET Name = ?, Username = ?, Password = ?, Email = ?, PhoneNumber = ?
                WHERE UserId = ? AND Role = 'MERCHANT'
                """;

        String updateMerchantSql = """
                UPDATE MerchantAccounts
                SET CompanyName = ?, Address = ?, CreditLimit = ?, AccountStatus = ?
                WHERE UserId = ?
                """;

        try (Connection conn = connectNow.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement userPs = conn.prepareStatement(updateUserSql);
                    PreparedStatement merchantPs = conn.prepareStatement(updateMerchantSql)
            ) {
                userPs.setString(1, fullName.trim());
                userPs.setString(2, username);
                userPs.setString(3, password);
                userPs.setString(4, email);
                userPs.setString(5, PhoneNumberRules.normalizeAndValidate(phoneNumber));
                userPs.setInt(6, userId);

                int userRows = userPs.executeUpdate();
                if (userRows == 0) {
                    throw new SQLException("No merchant user was updated.");
                }

                merchantPs.setString(1, companyName);
                merchantPs.setString(2, address);
                merchantPs.setDouble(3, creditLimit);
                merchantPs.setString(4, accountStatus);
                merchantPs.setInt(5, userId);

                int merchantRows = merchantPs.executeUpdate();
                if (merchantRows == 0) {
                    throw new SQLException("No merchant account was updated.");
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
/**
 * Executes the generate next merchant account number workflow.
 * This method coordinates the main operation for this action.
 *
 * @param conn conn
 * @return result value
 * @throws Exception when the operation fails
 */

    private String generateNextMerchantAccountNumber(Connection conn) throws Exception {
        String sql = """
                SELECT COALESCE(MAX(CAST(SUBSTRING(IPOSAccountNumber, 4) AS UNSIGNED)), 0) + 1 AS nextNumber
                FROM MerchantAccounts
                WHERE IPOSAccountNumber LIKE 'ACC%'
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int nextNumber = rs.getInt("nextNumber");
                return String.format("ACC%04d", nextNumber);
            }
        }
        return "ACC0001";
    }
/**
 * Executes the validate merchant creation details workflow.
 * This method coordinates the main operation for this action.
 *
 * @param fullName full name
 * @param companyName company name
 * @param username username
 * @param password password
 * @param email email
 * @param phoneNumber phone number
 * @param address address
 * @param accountStatus account status
 * @param creditLimit credit limit
 * @param discountPlanType discount plan type
 * @param discountTiers discount tiers
 */

    private void validateMerchantCreationDetails(String fullName, String companyName, String username, String password,
                                                 String email, String phoneNumber, String address, String accountStatus,
                                                 double creditLimit, String discountPlanType, List<DiscountTier> discountTiers) {

        validateMerchantUpdateDetails(fullName, companyName, username, password, email, phoneNumber, address, accountStatus, creditLimit);

        if (discountPlanType == null || discountPlanType.isBlank()) {
            throw new IllegalArgumentException("Discount plan type is required");
        }

        if (discountTiers == null || discountTiers.isEmpty()) {
            throw new IllegalArgumentException("At least one discount tier is required");
        }
    }
/**
 * Executes the validate merchant update details workflow.
 * This method coordinates the main operation for this action.
 *
 * @param fullName full name
 * @param companyName company name
 * @param username username
 * @param password password
 * @param email email
 * @param phoneNumber phone number
 * @param address address
 * @param accountStatus account status
 * @param creditLimit credit limit
 */

    private void validateMerchantUpdateDetails(String fullName, String companyName, String username, String password,
                                               String email, String phoneNumber, String address, String accountStatus,
                                               double creditLimit) {

        String normalizedPhoneNumber = PhoneNumberRules.normalize(phoneNumber);

        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Name is required");
        if (companyName == null || companyName.isBlank()) throw new IllegalArgumentException("Company name is required");
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username is required");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password is required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");
        if (normalizedPhoneNumber.isBlank()) throw new IllegalArgumentException("Phone number is required");
        if (address == null || address.isBlank()) throw new IllegalArgumentException("Address is required");
        if (accountStatus == null || accountStatus.isBlank()) throw new IllegalArgumentException("Account status is required");
        if (creditLimit < 0) throw new IllegalArgumentException("Credit limit cannot be negative");

        if (!fullName.matches("[A-Za-z ]+")) {
            throw new IllegalArgumentException("Name must contain only letters and spaces.");
        }
        if (!companyName.matches("[A-Za-z0-9 ]+")) {
            throw new IllegalArgumentException("Company name can only contain letters, numbers, and spaces.");
        }
        if (!username.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, and underscores.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        PhoneNumberRules.validateNormalized(normalizedPhoneNumber);
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (!accountStatus.equals("NORMAL")
                && !accountStatus.equals("SUSPENDED")
                && !accountStatus.equals("IN_DEFAULT")) {
            throw new IllegalArgumentException("Status must be NORMAL, SUSPENDED or IN_DEFAULT.");
        }
    }
/**
 * Executes the update merchant fixed discount plan workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param discountPercent discount percent
 * @throws Exception when the operation fails
 */

    public void updateMerchantFixedDiscountPlan(int merchantId, double discountPercent) throws Exception {
        if (merchantId <= 0) {
            throw new IllegalArgumentException("Invalid merchant id.");
        }
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100.");
        }

        DatabaseConnection connectNow = new DatabaseConnection();

        String replaceOldPlansSql = """
                UPDATE DiscountPlans
                SET IsActive = 0
                WHERE MerchantId = ? AND IsActive = 1
                """;

        String insertNewPlanSql = """
                INSERT INTO DiscountPlans
                (MerchantId, PlanType, IsActive, CreatedByUserId)
                VALUES (?, ?, ?, NULL)
                """;

        String insertTierSql = """
                INSERT INTO DiscountPlanTiers
                (DiscountPlanId, MinOrderValue, MaxOrderValue, DiscountPercent)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = connectNow.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement deactivatePs = conn.prepareStatement(replaceOldPlansSql);
                    PreparedStatement planPs = conn.prepareStatement(insertNewPlanSql, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement tierPs = conn.prepareStatement(insertTierSql)
            ) {
                deactivatePs.setInt(1, merchantId);
                deactivatePs.executeUpdate();

                planPs.setInt(1, merchantId);
                planPs.setString(2, "FIXED");
                planPs.setBoolean(3, true);
                planPs.executeUpdate();

                int discountPlanId;
                try (ResultSet rs = planPs.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to create updated fixed discount plan.");
                    }
                    discountPlanId = rs.getInt(1);
                }
                tierPs.setInt(1, discountPlanId);
                tierPs.setDouble(2, 0.0);
                tierPs.setNull(3, Types.DECIMAL);
                tierPs.setDouble(4, discountPercent);
                tierPs.executeUpdate();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
/**
 * Executes the update merchant flexible discount plan workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param tiers tiers
 * @throws Exception when the operation fails
 */

    public void updateMerchantFlexibleDiscountPlan(int merchantId, List<DiscountTier> tiers) throws Exception {
        if (merchantId <= 0) {
            throw new IllegalArgumentException("Invalid merchant id.");
        }
        if (tiers == null || tiers.isEmpty()) {
            throw new IllegalArgumentException("Enter at least one complete discount tier.");
        }
        DatabaseConnection connectNow = new DatabaseConnection();

        String replaceOldPlansSql = """
                UPDATE DiscountPlans
                SET IsActive = 0
                WHERE MerchantId = ? AND IsActive = 1
                """;

        String insertNewPlanSql = """
                INSERT INTO DiscountPlans
                (MerchantId, PlanType, IsActive, CreatedByUserId)
                VALUES (?, ?, ?, NULL)
                """;

        String insertTierSql = """
                INSERT INTO DiscountPlanTiers
                (DiscountPlanId, MinOrderValue, MaxOrderValue, DiscountPercent)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = connectNow.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement deactivatePs = conn.prepareStatement(replaceOldPlansSql);
                    PreparedStatement planPs = conn.prepareStatement(insertNewPlanSql, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement tierPs = conn.prepareStatement(insertTierSql)
            ) {
                deactivatePs.setInt(1, merchantId);
                deactivatePs.executeUpdate();

                planPs.setInt(1, merchantId);
                planPs.setString(2, "FLEXIBLE");
                planPs.setBoolean(3, true);
                planPs.executeUpdate();

                int discountPlanId;
                try (ResultSet rs = planPs.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to create updated flexible discount plan.");
                    }
                    discountPlanId = rs.getInt(1);
                }

                for (DiscountTier tier : tiers) {
                    tierPs.setInt(1, discountPlanId);
                    tierPs.setDouble(2, tier.getMinOrderValue());

                    if (tier.getMaxOrderValue() == null) {
                        tierPs.setNull(3, Types.DECIMAL);
                    } else {
                        tierPs.setDouble(3, tier.getMaxOrderValue());
                    }
                    tierPs.setDouble(4, tier.getDiscountPercent());
                    tierPs.executeUpdate();
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
