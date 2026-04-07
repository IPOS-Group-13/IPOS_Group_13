package com.berrybyte.common;

public final class LoginSession {

    private static String currentRole;
    private static Integer currentUserId;

    private LoginSession() {
    }

    public static void setCurrentUserId(Integer userId) {
        currentUserId = userId;
    }

    public static Integer getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentRole(String role) {
        currentRole = role == null ? null : role.trim().toUpperCase();
    }

    public static String getCurrentRole() {
        return currentRole;
    }

    public static void clear() {
        currentRole = null;
        currentUserId = null;
    }
}
