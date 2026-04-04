package com.teesolutions.ipospu.utils;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Identifies 8-digit-only product ids from the PDF / local seed catalogue. When the shared database also
 * contains CA rows for the same drug name (e.g. P1002 vs 10000002), PU prefers the non-legacy row for display.
 */
public final class LegacySampleProductIds {
    private static final Pattern EIGHT_DIGITS = Pattern.compile("^\\d{8}$");

    private LegacySampleProductIds() {
    }

    public static boolean isLegacyEightDigitSampleId(String productId) {
        return productId != null && EIGHT_DIGITS.matcher(productId).matches();
    }

    public static String normalizeNameKey(String name) {
        return name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
    }
}
