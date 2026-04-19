package com.berrybyte.login;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    // --- isInputIncomplete ---

    @Test
    void isInputIncomplete_bothBlank_returnsTrue() {
        assertTrue(LoginValidator.isInputIncomplete("", ""));
    }

    @Test
    void isInputIncomplete_blankUsername_returnsTrue() {
        assertTrue(LoginValidator.isInputIncomplete("", "password123"));
    }

    @Test
    void isInputIncomplete_blankPassword_returnsTrue() {
        assertTrue(LoginValidator.isInputIncomplete("admin", ""));
    }

    @Test
    void isInputIncomplete_whitespaceOnly_returnsTrue() {
        assertTrue(LoginValidator.isInputIncomplete("   ", "   "));
    }

    @Test
    void isInputIncomplete_validCredentials_returnsFalse() {
        assertFalse(LoginValidator.isInputIncomplete("admin", "password123"));
    }

    // --- isUnknownRole ---

    @Test
    void isUnknownRole_nullRole_returnsTrue() {
        assertTrue(LoginValidator.isUnknownRole(null));
    }

    @Test
    void isUnknownRole_blankRole_returnsTrue() {
        assertTrue(LoginValidator.isUnknownRole("   "));
    }

    @Test
    void isUnknownRole_emptyRole_returnsTrue() {
        assertTrue(LoginValidator.isUnknownRole(""));
    }

    @Test
    void isUnknownRole_adminRole_returnsFalse() {
        assertFalse(LoginValidator.isUnknownRole("ADMIN"));
    }

    // --- isMerchantRole ---

    @Test
    void isMerchantRole_merchantUppercase_returnsTrue() {
        assertTrue(LoginValidator.isMerchantRole("MERCHANT"));
    }

    @Test
    void isMerchantRole_merchantLowercase_returnsTrue() {
        assertTrue(LoginValidator.isMerchantRole("merchant"));
    }

    @Test
    void isMerchantRole_merchantMixedCase_returnsTrue() {
        assertTrue(LoginValidator.isMerchantRole("Merchant"));
    }

    @Test
    void isMerchantRole_merchantWithWhitespace_returnsTrue() {
        assertTrue(LoginValidator.isMerchantRole("  MERCHANT  "));
    }

    @Test
    void isMerchantRole_adminRole_returnsFalse() {
        assertFalse(LoginValidator.isMerchantRole("ADMIN"));
    }

    @Test
    void isMerchantRole_nullRole_returnsFalse() {
        assertFalse(LoginValidator.isMerchantRole(null));
    }

    @Test
    void isMerchantRole_managerRole_returnsFalse() {
        assertFalse(LoginValidator.isMerchantRole("MANAGER"));
    }
}
