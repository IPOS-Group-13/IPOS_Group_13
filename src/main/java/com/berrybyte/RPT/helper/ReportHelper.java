package com.berrybyte.RPT.helper;

/**
 * Represents report helper.
 */
public class ReportHelper {
/**
 * Creates a new ReportHelper instance.
 */

    private ReportHelper() {
        // Utility class
    }
/**
 * Performs safe string.
 *
 * @param value value
 * @return result value
 */

    public static String safeString(String value) {
        return value == null ? "" : value;
    }
}
