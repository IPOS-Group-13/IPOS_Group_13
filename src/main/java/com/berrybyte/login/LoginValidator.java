package com.berrybyte.login;

/**
 * Stateless validation rules used by {@link LoginController}.
 * Extracted so they can be unit-tested without a JavaFX runtime.
 */
public final class LoginValidator {

    private LoginValidator() {
    }

    /**
     * Returns true if either the username or password is null / blank.
     */
    public static boolean isInputIncomplete(String username, String password) {
        return username == null || username.isBlank()
                || password == null || password.isBlank();
    }

    /**
     * Returns true if the role is null or blank (i.e. the account has no
     * recognisable role assigned).
     */
    public static boolean isUnknownRole(String role) {
        return role == null || role.isBlank();
    }

    /**
     * Returns true if the role represents a Merchant account, which is not
     * permitted to log in via the staff portal.
     */
    public static boolean isMerchantRole(String role) {
        return role != null && "MERCHANT".equalsIgnoreCase(role.trim());
    }
}
