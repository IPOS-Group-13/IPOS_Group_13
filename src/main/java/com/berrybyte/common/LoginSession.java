package com.berrybyte.common;

/**
 * Represents login session.
 */
public final class LoginSession {

    private static String currentRole;
    private static Integer currentUserId;
/**
 * Creates a new LoginSession instance.
 */

    private LoginSession() {
    }
/**
 * Sets current user id.
 *
 * @param userId user id
 */

    public static void setCurrentUserId(Integer userId) {
        currentUserId = userId;
    }
/**
 * Returns current user id.
 *
 * @return result value
 */

    public static Integer getCurrentUserId() {
        return currentUserId;
    }
/**
 * Sets current role.
 *
 * @param role role
 */

    public static void setCurrentRole(String role) {
        currentRole = role == null ? null : role.trim().toUpperCase();
    }
/**
 * Returns current role.
 *
 * @return result value
 */

    public static String getCurrentRole() {
        return currentRole;
    }
/**
 * Performs clear.
 *
 */

    public static void clear() {
        currentRole = null;
        currentUserId = null;
    }
}
