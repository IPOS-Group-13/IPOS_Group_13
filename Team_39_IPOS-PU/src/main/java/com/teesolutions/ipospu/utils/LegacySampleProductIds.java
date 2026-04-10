package com.teesolutions.ipospu.utils;

import java.util.Locale;
import java.util.regex.Pattern;


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
