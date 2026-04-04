package com.teesolutions.ipospu.utils;

import java.security.SecureRandom;

public class SecurityUtil {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_SYMBOLS = "!@#$%^&*()-_=+";

    // Combines all characters for the random filling phase
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + NUMBERS + SPECIAL_SYMBOLS;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a strict 10-character password containing at least one uppercase,
     * one lowercase, one number, and one special symbol as required by the IPOS brief.
     */
    public static String generateInitialPassword() {
        StringBuilder password = new StringBuilder(10);

        // 1. Guarantee at least one of each required type
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        password.append(SPECIAL_SYMBOLS.charAt(random.nextInt(SPECIAL_SYMBOLS.length())));

        // 2. Fill the remaining 6 characters randomly from all allowed characters
        for (int i = 4; i < 10; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // 3. Shuffle the string so the predictable format (Upper, Lower, Num, Symbol) isn't obvious
        return shuffleString(password.toString());
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = random.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }
}