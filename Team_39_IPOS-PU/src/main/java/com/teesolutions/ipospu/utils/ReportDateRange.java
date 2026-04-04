package com.teesolutions.ipospu.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ReportDateRange {
    private ReportDateRange() {
    }

    public static LocalDateTime startOfDay(LocalDate date) {
        return normalize(date).atStartOfDay();
    }

    public static LocalDateTime exclusiveEndOfDay(LocalDate date) {
        return normalize(date).plusDays(1).atStartOfDay();
    }

    private static LocalDate normalize(LocalDate date) {
        return date == null ? LocalDate.now() : date;
    }
}
