package com.berrybyte.ACC.services;

import com.berrybyte.ACC.model.DiscountTier;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;

public class MerchantAccountServiceTest {

    private final MerchantAccountService service = new MerchantAccountService();

    private Integer createdUserId = null;

    @Test
    void testEmptyName() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createMerchantAccount(
                        "", "Scade Nets", "alex123", "password123",
                        "alex@outlook.com", "07411562948", "4 Silicon Way",
                        "NORMAL", 3000.0, "FIXED",
                        List.of(new DiscountTier(0.0, null, 5.0))
                )
        );
    }

    @Test
    void testNameWithNumbers() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createMerchantAccount(
                        "Alex123", "Scade Nets", "alex123", "password123",
                        "alex@outlook.com", "07411562948", "4 Silicon Way",
                        "NORMAL", 3000.0, "FIXED",
                        List.of(new DiscountTier(0.0, null, 5.0))
                )
        );
    }

    @Test
    void testEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createMerchantAccount(
                        "Alex Davis", "Scade Nets", "", "password123",
                        "alex@outlook.com", "07411562948", "4 Silicon Way",
                        "NORMAL", 3000.0, "FIXED",
                        List.of(new DiscountTier(0.0, null, 5.0))
                )
        );
    }

    @Test
    void testShortPassword() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createMerchantAccount(
                        "Alex Davis", "Scade Nets", "alex123", "pas",
                        "alex@outlook.com", "07411562948", "4 Silicon Way",
                        "NORMAL", 3000.0, "FIXED",
                        List.of(new DiscountTier(0.0, null, 5.0))
                )
        );
    }

    @Test
    void testInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createMerchantAccount(
                        "Alex Davis", "Scade Nets", "alex123", "password123",
                        "alexoutlook.com", "07411562948", "4 Silicon Way",
                        "NORMAL", 3000.0, "FIXED",
                        List.of(new DiscountTier(0.0, null, 5.0))
                )
        );
    }

    @Test
    void testShortPhone() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createMerchantAccount(
                        "Alex Davis", "Scade Nets", "alex123", "password123",
                        "alex@outlook.com", "074", "4 Silicon Way",
                        "NORMAL", 3000.0, "FIXED",
                        List.of(new DiscountTier(0.0, null, 5.0))
                )
        );
    }

    @Test
    void testInvalidStatus() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createMerchantAccount(
                        "Alex Davis", "Scade Nets", "alex123", "password123",
                        "alex@outlook.com", "07411562948", "4 Silicon Way",
                        "NORM", 3000.0, "FIXED",
                        List.of(new DiscountTier(0.0, null, 5.0))
                )
        );
    }

    @Test
    void testNegativeCreditLimit() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createMerchantAccount(
                        "Alex Davis", "Scade Nets", "alex123", "password123",
                        "alex@outlook.com", "07411562948", "4 Silicon Way",
                        "NORMAL", -3000.0, "FIXED",
                        List.of(new DiscountTier(0.0, null, 5.0))
                )
        );
    }

    @Test
    void testEmptyDiscountPlanType() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createMerchantAccount(
                        "Alex Davis", "Scade Nets", "alex123", "password123",
                        "alex@outlook.com", "07411562948", "4 Silicon Way",
                        "NORMAL", 3000.0, "",
                        List.of(new DiscountTier(0.0, null, 5.0))
                )
        );
    }

    @Test
    void testEmptyDiscountTier() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createMerchantAccount(
                        "Alex Davis", "Scade Nets", "alex123", "password123",
                        "alex@outlook.com", "07411562948", "4 Silicon Way",
                        "NORMAL", 3000.0, "FIXED",
                        List.of()
                )
        );
    }

    @Test
    void updateNegativeCreditLimit() {
        assertThrows(IllegalArgumentException.class, () ->
                service.updateMerchantAccountDetails(
                        1, "Alex Davis", "Scade Nets", "alex123", "password123",
                        "alex@outlook.com", "07411562948", "4 Silicon Way",
                        "NORMAL", -1.0
                )
        );
    }

    @Test
    void updateRejectsInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () ->
                service.updateMerchantAccountDetails(
                        1, "Alex Davis", "Scade Nets", "alex123", "password123",
                        "alexoutlook.com", "07411562948", "4 Silicon Way",
                        "NORMAL", 3000.0
                )
        );
    }

    @Test
    void rejectInvalidMerchantId() {
        assertThrows(IllegalArgumentException.class, () ->
                service.updateMerchantFixedDiscountPlan(0, 10.0)
        );
    }

    @Test
    void rejectNegativeDiscountPercent() {
        assertThrows(IllegalArgumentException.class, () ->
                service.updateMerchantFixedDiscountPlan(1, -5.0)
        );
    }

    @Test
    void rejectHighDiscount() {
        assertThrows(IllegalArgumentException.class, () ->
                service.updateMerchantFixedDiscountPlan(1, 101.0)
        );
    }

    @Test
    void rejectDiscountNullTiers() {
        assertThrows(IllegalArgumentException.class, () ->
                service.updateMerchantFlexibleDiscountPlan(1, null)
        );
    }

    @Test
    void rejectDiscountEmptyTiers() {
        assertThrows(IllegalArgumentException.class, () ->
                service.updateMerchantFlexibleDiscountPlan(1, List.of())
        );
    }

    @Test
    void rejectInvalidFlexibleID() {
        assertThrows(IllegalArgumentException.class, () ->
                service.updateMerchantFlexibleDiscountPlan(0, List.of(new DiscountTier(0.0, null, 5.0)))
        );
    }


    @Test
    void createMerchantAccount_success() throws Exception {

        System.out.println("=================================");
        System.out.println("Testing createMerchantAccount SUCCESS case...");
        System.out.println("=================================");

        String suffix = String.valueOf(System.currentTimeMillis());

        String fullName = "Test User";
        String companyName = "TestCompany" + suffix;
        String username = "TEST_USER_" + suffix;
        String password = "password123";
        String email = "test" + suffix + "@example.com";
        String phone = "07123456789";
        String address = "Test Address";

        service.createMerchantAccount(
                fullName,
                companyName,
                username,
                password,
                email,
                phone,
                address,
                "NORMAL",
                500.0,
                "FIXED",
                List.of(new DiscountTier(0.0, null, 5.0))
        );

        try (var conn = new com.berrybyte.common.DatabaseConnection().getConnection();
             var ps = conn.prepareStatement(
                     "SELECT UserId FROM Users WHERE Username = ?"
             )) {

            ps.setString(1, username);

            try (var rs = ps.executeQuery()) {
                System.out.println("Check 1: Merchant should exist in database...");
                assertTrue(rs.next(), "Merchant should exist in database");
                createdUserId = rs.getInt("UserId");
                System.out.println("...success");
            }
        }

        System.out.println("Check 2: Created user ID should be captured...");
        assertNotNull(createdUserId, "Created user id should be captured");
        System.out.println("...success");
    }

    @Test
    void updateMerchantAccountDetails_success() throws Exception {

        System.out.println("=================================");
        System.out.println("Testing updateMerchantAccountDetails SUCCESS case...");
        System.out.println("=================================");

        String suffix = String.valueOf(System.currentTimeMillis());

        String fullName = "Test User";
        String companyName = "TestCompany" + suffix;
        String username = "TEST_USER_" + suffix;
        String password = "password123";
        String email = "test" + suffix + "@example.com";
        String phone = "07123456789";
        String address = "Test Address";

        service.createMerchantAccount(
                fullName,
                companyName,
                username,
                password,
                email,
                phone,
                address,
                "NORMAL",
                500.0,
                "FIXED",
                List.of(new DiscountTier(0.0, null, 5.0))
        );

        try (var conn = new com.berrybyte.common.DatabaseConnection().getConnection();
             var ps = conn.prepareStatement(
                     "SELECT UserId FROM Users WHERE Username = ?"
             )) {

            ps.setString(1, username);

            try (var rs = ps.executeQuery()) {
                System.out.println("Check 1: Merchant should exist before update...");
                assertTrue(rs.next(), "Merchant should exist before update");
                createdUserId = rs.getInt("UserId");
                System.out.println("...success");
            }
        }

        service.updateMerchantAccountDetails(
                createdUserId,
                "Updated User",
                "UpdatedCompany",
                username,
                password,
                email,
                phone,
                "Updated Address",
                "SUSPENDED",
                900.0
        );

        try (var conn = new com.berrybyte.common.DatabaseConnection().getConnection();
             var ps = conn.prepareStatement(
                     """
                     SELECT u.Name, ma.CompanyName, ma.Address, ma.AccountStatus, ma.CreditLimit
                     FROM Users u
                     JOIN MerchantAccounts ma ON u.UserId = ma.UserId
                     WHERE u.UserId = ?
                     """
             )) {

            ps.setInt(1, createdUserId);

            try (var rs = ps.executeQuery()) {
                System.out.println("Check 2: Updated merchant should still exist...");
                assertTrue(rs.next(), "Updated merchant should still exist");
                System.out.println("...success");

                System.out.println("Check 3: Name updated correctly...");
                assertEquals("Updated User", rs.getString("Name"));
                System.out.println("...success");

                System.out.println("Check 4: Company name updated correctly...");
                assertEquals("UpdatedCompany", rs.getString("CompanyName"));
                System.out.println("...success");

                System.out.println("Check 5: Address updated correctly...");
                assertEquals("Updated Address", rs.getString("Address"));
                System.out.println("...success");

                System.out.println("Check 6: Account status updated correctly...");
                assertEquals("SUSPENDED", rs.getString("AccountStatus"));
                System.out.println("...success");

                System.out.println("Check 7: Credit limit updated correctly...");
                assertEquals(900.0, rs.getDouble("CreditLimit"));
                System.out.println("...success");
            }
        }
    }

    @Test
    void updateMerchantFixedDiscountPlan_success() throws Exception {

        System.out.println("=================================");
        System.out.println("Testing updateMerchantFixedDiscountPlan SUCCESS case...");
        System.out.println("=================================");

        String suffix = String.valueOf(System.currentTimeMillis());

        String fullName = "Test User";
        String companyName = "TestCompany" + suffix;
        String username = "TEST_USER_" + suffix;
        String password = "password123";
        String email = "test" + suffix + "@example.com";
        String phone = "07123456789";
        String address = "Test Address";

        service.createMerchantAccount(
                fullName,
                companyName,
                username,
                password,
                email,
                phone,
                address,
                "NORMAL",
                500.0,
                "FIXED",
                List.of(new DiscountTier(0.0, null, 5.0))
        );

        try (var conn = new com.berrybyte.common.DatabaseConnection().getConnection();
             var ps = conn.prepareStatement(
                     """
                     SELECT ma.UserId, ma.MerchantId
                     FROM MerchantAccounts ma
                     JOIN Users u ON ma.UserId = u.UserId
                     WHERE u.Username = ?
                     """
             )) {

            ps.setString(1, username);

            try (var rs = ps.executeQuery()) {
                System.out.println("Check 1: Merchant should exist before updating discount plan...");
                assertTrue(rs.next(), "Merchant should exist before updating discount plan");
                createdUserId = rs.getInt("UserId");
                int merchantId = rs.getInt("MerchantId");
                System.out.println("...success");

                service.updateMerchantFixedDiscountPlan(merchantId, 15.0);

                try (var conn2 = new com.berrybyte.common.DatabaseConnection().getConnection();
                     var ps2 = conn2.prepareStatement(
                             """
                             SELECT dp.PlanType, dpt.DiscountPercent
                             FROM DiscountPlans dp
                             JOIN DiscountPlanTiers dpt ON dp.DiscountPlanId = dpt.DiscountPlanId
                             WHERE dp.MerchantId = ?
                               AND dp.IsActive = 1
                             ORDER BY dp.DiscountPlanId DESC
                             LIMIT 1
                             """
                     )) {

                    ps2.setInt(1, merchantId);

                    try (var rs2 = ps2.executeQuery()) {
                        System.out.println("Check 2: Updated active discount plan should exist...");
                        assertTrue(rs2.next(), "Updated active discount plan should exist");
                        System.out.println("...success");

                        System.out.println("Check 3: Plan type should be FIXED...");
                        assertEquals("FIXED", rs2.getString("PlanType"));
                        System.out.println("...success");

                        System.out.println("Check 4: Discount percent should be 15.0...");
                        assertEquals(15.0, rs2.getDouble("DiscountPercent"));
                        System.out.println("...success");
                    }
                }
            }
        }
    }

    @Test
    void updateMerchantFlexibleDiscountPlan_success() throws Exception {

        System.out.println("=================================");
        System.out.println("Testing updateMerchantFlexibleDiscountPlan SUCCESS case...");
        System.out.println("=================================");

        String suffix = String.valueOf(System.currentTimeMillis());

        String fullName = "Test User";
        String companyName = "TestCompany" + suffix;
        String username = "TEST_USER_" + suffix;
        String password = "password123";
        String email = "test" + suffix + "@example.com";
        String phone = "07123456789";
        String address = "Test Address";

        service.createMerchantAccount(
                fullName,
                companyName,
                username,
                password,
                email,
                phone,
                address,
                "NORMAL",
                500.0,
                "FIXED",
                List.of(new DiscountTier(0.0, null, 5.0))
        );

        int merchantId;

        try (var conn = new com.berrybyte.common.DatabaseConnection().getConnection();
             var ps = conn.prepareStatement(
                     """
                     SELECT ma.UserId, ma.MerchantId
                     FROM MerchantAccounts ma
                     JOIN Users u ON ma.UserId = u.UserId
                     WHERE u.Username = ?
                     """
             )) {

            ps.setString(1, username);

            try (var rs = ps.executeQuery()) {
                System.out.println("Check 1: Merchant should exist before updating flexible discount plan...");
                assertTrue(rs.next(), "Merchant should exist before updating flexible discount plan");
                createdUserId = rs.getInt("UserId");
                merchantId = rs.getInt("MerchantId");
                System.out.println("...success");
            }
        }

        List<DiscountTier> tiers = List.of(
                new DiscountTier(0.0, 100.0, 5.0),
                new DiscountTier(100.01, 500.0, 10.0)
        );

        service.updateMerchantFlexibleDiscountPlan(merchantId, tiers);

        try (var conn = new com.berrybyte.common.DatabaseConnection().getConnection();
             var ps = conn.prepareStatement(
                     """
                     SELECT dp.PlanType, dpt.MinOrderValue, dpt.MaxOrderValue, dpt.DiscountPercent
                     FROM DiscountPlans dp
                     JOIN DiscountPlanTiers dpt ON dp.DiscountPlanId = dpt.DiscountPlanId
                     WHERE dp.MerchantId = ?
                       AND dp.IsActive = 1
                     ORDER BY dpt.MinOrderValue ASC
                     """
             )) {

            ps.setInt(1, merchantId);

            try (var rs = ps.executeQuery()) {
                System.out.println("Check 2: First flexible discount tier should exist...");
                assertTrue(rs.next(), "First flexible discount tier should exist");
                System.out.println("...success");

                System.out.println("Check 3: Plan type should be FLEXIBLE...");
                assertEquals("FLEXIBLE", rs.getString("PlanType"));
                System.out.println("...success");

                System.out.println("Check 4: First tier minimum order value should be 0.0...");
                assertEquals(0.0, rs.getDouble("MinOrderValue"));
                System.out.println("...success");

                System.out.println("Check 5: First tier maximum order value should be 100.0...");
                assertEquals(100.0, rs.getDouble("MaxOrderValue"));
                System.out.println("...success");

                System.out.println("Check 6: First tier discount should be 5.0...");
                assertEquals(5.0, rs.getDouble("DiscountPercent"));
                System.out.println("...success");

                System.out.println("Check 7: Second flexible discount tier should exist...");
                assertTrue(rs.next(), "Second flexible discount tier should exist");
                System.out.println("...success");

                System.out.println("Check 8: Second tier minimum order value should be 100.01...");
                assertEquals(100.01, rs.getDouble("MinOrderValue"));
                System.out.println("...success");

                System.out.println("Check 9: Second tier maximum order value should be 500.0...");
                assertEquals(500.0, rs.getDouble("MaxOrderValue"));
                System.out.println("...success");

                System.out.println("Check 10: Second tier discount should be 10.0...");
                assertEquals(10.0, rs.getDouble("DiscountPercent"));
                System.out.println("...success");
            }
        }
    }

    @Test
    void createMerchantAccount_storesPhoneNumber() throws Exception {

        System.out.println("=================================");
        System.out.println("Testing createMerchantAccount phone storage case...");
        System.out.println("=================================");

        String suffix = String.valueOf(System.currentTimeMillis());
        String username = "TEST_USER_" + suffix;

        service.createMerchantAccount(
                "Test User",
                "TestCompany" + suffix,
                username,
                "password123",
                "test" + suffix + "@example.com",
                "07123 456 789",
                "Test Address",
                "NORMAL",
                500.0,
                "FIXED",
                List.of(new DiscountTier(0.0, null, 5.0))
        );

        try (var conn = new com.berrybyte.common.DatabaseConnection().getConnection();
             var ps = conn.prepareStatement(
                     """
                     SELECT u.UserId, u.PhoneNumber
                     FROM MerchantAccounts ma
                     JOIN Users u ON ma.UserId = u.UserId
                     WHERE u.Username = ?
                     """
             )) {

            ps.setString(1, username);

            try (var rs = ps.executeQuery()) {
                System.out.println("Check 1: Merchant should exist...");
                assertTrue(rs.next());
                createdUserId = rs.getInt("UserId");
                System.out.println("...success");

                String storedPhone = rs.getString("PhoneNumber");

                System.out.println("Check 2: Stored phone number is not null...");
                assertNotNull(storedPhone);
                System.out.println("...success");

                System.out.println("Check 3: Stored phone number is not blank...");
                assertFalse(storedPhone.isBlank());
                System.out.println("...success");
            }
        }
    }

    // NOTE FOR MY TEAM: This will clean up any of the tests you run in this test file
    // and let you know which User was deleted.
    // If you do not want it deleted, just comment the @AfterEach and cleanup method out
    @AfterEach
    void cleanup() throws Exception {
        if (createdUserId != null) {
            System.out.println("Cleaning up test user: " + createdUserId);
            new DeleteAccountService().deleteUserAccount(createdUserId);
            createdUserId = null;
        }
    }
}