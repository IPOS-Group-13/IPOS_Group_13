package com.berrybyte.account;

final class PhoneNumberRules {

    private PhoneNumberRules() {
    }

    static String normalizeAndValidate(String phoneNumber) {
        String normalizedPhoneNumber = normalize(phoneNumber);

        if (normalizedPhoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        validateNormalized(normalizedPhoneNumber);

        return normalizedPhoneNumber;
    }

    static String normalize(String phoneNumber) {
        if (phoneNumber == null) {
            return "";
        }

        String normalized = phoneNumber.trim().replaceAll("\\s+", " ");
        if (normalized.startsWith("+")) {
            normalized = normalized.replaceFirst("^\\+\\d{1,3}\\s*", "");
        }
        return normalized.trim();
    }

    static void validateNormalized(String normalizedPhoneNumber) {
        if (!normalizedPhoneNumber.matches("[0-9 ]+")) {
            throw new IllegalArgumentException("Phone number must contain only numbers and spaces.");
        }

        int phoneDigits = normalizedPhoneNumber.replace(" ", "").length();
        if (phoneDigits < 7 || phoneDigits > 12) {
            throw new IllegalArgumentException("Enter a valid phone number using 7 to 12 digits.");
        }
    }
}
