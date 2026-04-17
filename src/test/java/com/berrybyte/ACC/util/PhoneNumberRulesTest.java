package com.berrybyte.ACC.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhoneNumberRulesTest {

    @Test
    void acceptsLocalNumber() {
        assertEquals("07411 562948", PhoneNumberRules.normalizeAndValidate(" 07411   562948 "));
    }

    @Test
    void acceptsNumberWithCountryCode() {
        assertEquals("7123456789", PhoneNumberRules.normalizeAndValidate("+44 7123456789"));
    }

    @Test
    void rejectsTooShortNumber() {
        assertThrows(IllegalArgumentException.class, () -> PhoneNumberRules.normalizeAndValidate("074"));
    }

    @Test
    void rejectsNonNumericCharacters() {
        assertThrows(IllegalArgumentException.class, () -> PhoneNumberRules.normalizeAndValidate("07123-456789"));
    }
}
