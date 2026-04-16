package com.berrybyte.RPT.helper;

public class ReportHelper {

    private ReportHelper() {
        // Utility class
    }

    public static String safeString(String value) {
        return value == null ? "" : value;
    }
}