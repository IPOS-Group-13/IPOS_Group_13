package com.teesolutions.ipospu.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {
    @Test
    void nonCommercialNinthOrderGetsLoyaltyDiscount() {
        User user = new User(1, "cool@example.com", "NON_COMMERCIAL", false, 9);
        assertTrue(user.isEligibleForLoyaltyDiscount());
    }

    @Test
    void commercialUserNeverGetsNonCommercialLoyaltyRule() {
        User user = new User(2, "biz@ipos.local", "COMMERCIAL", false, 9);
        assertFalse(user.isEligibleForLoyaltyDiscount());
    }
}
