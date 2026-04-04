package com.teesolutions.ipospu.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityUtilTest {
    @Test
    void generatedPasswordMeetsLengthAndComplexity() {
        String password = SecurityUtil.generateInitialPassword();
        assertEquals(10, password.length());
        assertTrue(password.matches(".*[A-Z].*"));
        assertTrue(password.matches(".*[a-z].*"));
        assertTrue(password.matches(".*[0-9].*"));
        assertTrue(password.matches(".*[!@#$%^&*()\\-_=+].*"));
    }
}
