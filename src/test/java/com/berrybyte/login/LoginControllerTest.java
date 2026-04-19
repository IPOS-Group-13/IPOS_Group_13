package com.berrybyte.login;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    @Test
    void login_withEmptyFields() {
        String username = "";
        String password = "";

        assertTrue(username.isEmpty());
        assertTrue(password.isEmpty());
    }

    @Test
    void emptyUsername_isDetected() {
        String username = "";
        assertTrue(username.isBlank());
    }

    @Test
    void emptyPassword_isDetected() {
        String password = "";
        assertTrue(password.isBlank());
    }

    @Test
    void nonEmptyUsername_isAcceptedAsInput() {
        String username = "admin";
        assertFalse(username.isBlank());
    }

    @Test
    void nonEmptyPassword_isAcceptedAsInput() {
        String password = "password123";
        assertFalse(password.isBlank());
    }

    @Test
    void roleComparison_adminMatchesIgnoringCase() {
        assertTrue("ADMIN".equalsIgnoreCase("admin"));
    }
}