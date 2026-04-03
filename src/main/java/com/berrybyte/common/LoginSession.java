package com.berrybyte.common;

public final class LoginSession {

    private static String currentRole;

    private LoginSession() {
    }

    public static void setCurrentRole(String role) {
        currentRole = role == null ? null : role.trim().toUpperCase();
    }

    public static String getCurrentRole() {
        return currentRole;
    }

    public static void clear() {
        currentRole = null;
    }
}
