package com.berrybyte.account;

import com.berrybyte.common.DatabaseConnection;

import java.sql.*;
import java.util.List;

public class MerchantAccountService {

    public void createMerchantAccount(
            String fullName,
            String companyName,
            String username,
            String password,
            String email,
            String phoneNumber,
            String address,
            String accountStatus,
            double creditLimit,
            String discountPlanType,
            List<DiscountTier> discountTiers
    ) throws Exception {
        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Full name is required");
        if (companyName == null || companyName.isBlank()) throw new IllegalArgumentException("Company name is required");
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username is required");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password is required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");
        if (phoneNumber == null || phoneNumber.isBlank()) throw new IllegalArgumentException("Phone number is required");
        if (address == null || address.isBlank()) throw new IllegalArgumentException("Address is required");
        if (accountStatus == null || accountStatus.isBlank()) throw new IllegalArgumentException("Account status is required");
        if (discountPlanType == null || discountPlanType.isBlank()) throw new IllegalArgumentException("Discount plan type is required");
        if (discountTiers == null || discountTiers.isEmpty()) throw new IllegalArgumentException("At least one discount tier is required");
        if (creditLimit < 0) throw new IllegalArgumentException("Credit limit cannot be negative");

        String firstName = "";
        String lastName = "";

        if (fullName.contains(" ")) {
            String[] parts = fullName.trim().split(" ", 2);
            firstName = parts[0].trim();
            lastName = parts[1].trim();
        } else {
            firstName = fullName.trim();
        }

        DatabaseConnection connectNow = new DatabaseConnection();

        String insertUserSql = """
                INSERT INTO Users
                (Firstname, Lastname, Username, Password, IdNumber, Email, PhoneNumber, Role)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String insertMerchantSql = """
                INSERT INTO MerchantAccounts
                (UserId, IPOSAccountNumber, CompanyName, Address, CreditLimit, CurrentBalance, AccountStatus, IsActivated)
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
                    PreparedStatement tierPs = conn.prepareStatement(insertTierSql)
            ) {
                String generatedIdNumber = "M-" + System.currentTimeMillis();
                userPs.setString(1, firstName);
                userPs.setString(2, lastName);
                userPs.setString(3, username);
                userPs.setString(4, password);
                userPs.setString(5, generatedIdNumber);
                userPs.setString(6, email);
                userPs.setString(7, phoneNumber);
                userPs.setString(8, "MERCHANT");
                userPs.executeUpdate();

                int userId;
                try (ResultSet rs = userPs.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to create merchant user");
                    }
                    userId = rs.getInt(1);
                }

                String iposAccountNumber = "IPOS" + String.format("%06d", userId);

                merchantPs.setInt(1, userId);
                merchantPs.setString(2, iposAccountNumber);
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
}