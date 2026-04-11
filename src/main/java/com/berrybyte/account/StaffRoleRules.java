package com.berrybyte.account;

import java.util.Set;

public final class StaffRoleRules {

    private static final Set<String> RESERVED_STAFF_ROLES = Set.of(
            "ADMIN",
            "ADMINISTRATOR",
            "MANAGER",
            "DIRECTOR OF OPERATIONS",
            "MERCHANT"
    );
    private StaffRoleRules() {
    }

    public static boolean isReservedStaffRole(String role) {
        if (role == null) {
            return false;
        }

        String normalizedRole = normalizeRole(role);
        return RESERVED_STAFF_ROLES.contains(normalizedRole);
    }

    public static String normalizeRole(String role) {
        return role == null
                ? ""
                : role.trim().replaceAll("\\s+", " ").toUpperCase();
    }
}
