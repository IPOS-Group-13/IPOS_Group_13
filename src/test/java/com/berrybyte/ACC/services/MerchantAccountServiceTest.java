package com.berrybyte.ACC.services;

import com.berrybyte.ACC.model.DiscountTier;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MerchantAccountServiceTest {

    private final MerchantAccountService service = new MerchantAccountService();

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
}
