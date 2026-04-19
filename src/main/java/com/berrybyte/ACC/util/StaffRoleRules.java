package com.berrybyte.ACC.util;

import java.util.Set;

/**
 * Represents staff role rules.
 */
public final class StaffRoleRules {

    private static final Set<String> RESERVED_STAFF_ROLES = Set.of(
            "ADMIN",
            "ADMINISTRATOR",
            "MANAGER",
            "DIRECTOR OF OPERATIONS",
            "MERCHANT");
/**
 * Creates a new StaffRoleRules instance.
 */

    private StaffRoleRules() {
    }
/**
 * Performs is reserved staff role.
 *
 * @param role role
 * @return result value
 */

    public static boolean isReservedStaffRole(String role) {
        if (role == null) {
            return false;
        }

        String normalizedRole = normalizeRole(role);
        return RESERVED_STAFF_ROLES.contains(normalizedRole);
    }
/**
 * Performs normalize role.
 *
 * @param role role
 * @return result value
 */

    public static String normalizeRole(String role) {
        return role == null ? "" : role.trim().replaceAll("\\s+", " ").toUpperCase();
    }
}
