package com.teesolutions.ipospu.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthServiceTest {
    @Test
    void registerRejectsInvalidEmailBeforeDatabaseLookup() {
        AuthService authService = new AuthService();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.registerNonCommercial("not-an-email")
        );

        assertEquals("Please provide a valid email address", ex.getMessage());
    }

    @Test
    void requireValidEmailTrimsWhitespace() {
        AuthService authService = new AuthService();

        String normalized = authService.requireValidEmail("  cool1@example.com  ");

        assertEquals("cool1@example.com", normalized);
    }

    @Test
    void companyRegistrationNumberIsNormalizedToUppercase() {
        AuthService authService = new AuthService();

        String normalized = authService.requireCompanyRegistrationNumber(" sc123456 ");

        assertEquals("SC123456", normalized);
    }

    @Test
    void companyRegistrationNumberRejectsUnexpectedFormat() {
        AuthService authService = new AuthService();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.requireCompanyRegistrationNumber("ABC-123")
        );

        assertEquals(
                "Company registration number must be 8 digits, 2 letters + 6 digits, or UK + 8 digits (optional suffix, e.g. UK10003429COMPH)",
                ex.getMessage()
        );
    }

    @Test
    void companyRegistrationNumberAcceptsUniversitySampleUkFormat() {
        AuthService authService = new AuthService();

        String normalized = authService.requireCompanyRegistrationNumber("UK10003429CompH");

        assertEquals("UK10003429COMPH", normalized);
    }
}
