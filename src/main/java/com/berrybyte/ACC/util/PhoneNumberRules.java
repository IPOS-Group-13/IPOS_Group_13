package com.berrybyte.ACC.util;

/**
 * Represents phone number rules.
 */
public final class PhoneNumberRules {
/**
 * Creates a new PhoneNumberRules instance.
 */

    private PhoneNumberRules() {
    }
/**
 * Performs normalize and validate.
 *
 * @param phoneNumber phone number
 * @return result value
 */

    public static String normalizeAndValidate(String phoneNumber) {
        String normalizedPhoneNumber = normalize(phoneNumber);

        if (normalizedPhoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        validateNormalized(normalizedPhoneNumber);

        return normalizedPhoneNumber;
    }
/**
 * Performs normalize.
 *
 * @param phoneNumber phone number
 * @return result value
 */

    public static String normalize(String phoneNumber) {
        if (phoneNumber == null) {
            return "";
        }

        return phoneNumber.trim().replaceAll("\\s+", "");
    }
/**
 * Executes the validate normalized workflow.
 * This method coordinates the main operation for this action.
 *
 * @param normalizedPhoneNumber normalized phone number
 */

    public static void validateNormalized(String normalizedPhoneNumber) {
        if (!normalizedPhoneNumber.matches("\\d+")) {
            throw new IllegalArgumentException("Phone number must contain only numbers.");
        }

        if (normalizedPhoneNumber.length() < 7 || normalizedPhoneNumber.length() > 12) {
            throw new IllegalArgumentException("Enter a valid phone number using 7 to 12 digits.");
        }
    }
}
