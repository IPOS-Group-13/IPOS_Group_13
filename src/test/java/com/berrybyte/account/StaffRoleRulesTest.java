package com.berrybyte.account;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StaffRoleRulesTest {

    @Test
    void checkAdmin() {
        assertTrue(StaffRoleRules.isReservedStaffRole("ADMIN"));
    }

    @Test
    void checkManager() {
        assertTrue(StaffRoleRules.isReservedStaffRole("MANAGER"));
    }

    @Test
    void checkMerchant() {
        assertTrue(StaffRoleRules.isReservedStaffRole("MERCHANT"));
    }

    @Test
    void checkNotReserved() {
        assertFalse(StaffRoleRules.isReservedStaffRole("DIRECTOR"));
    }

    @Test
    void checkNullRole() {
        assertFalse(StaffRoleRules.isReservedStaffRole(null));
    }

    @Test
    void checkLowercaseAdmin() {
        assertTrue(StaffRoleRules.isReservedStaffRole("admin"));
    }

    @Test
    void checkDifferentCase() {
        assertTrue(StaffRoleRules.isReservedStaffRole("Manager"));
    }

    @Test
    void checkSpace() {
        assertEquals("ADMIN", StaffRoleRules.normalizeRole("  admin  "));
    }

    @Test
    void checkNullEmpty() {
        assertEquals("", StaffRoleRules.normalizeRole(null));
    }
}