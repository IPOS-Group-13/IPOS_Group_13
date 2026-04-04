package com.teesolutions.ipospu.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportDateRangeTest {
    @Test
    void startOfDayUsesMidnightForSelectedDate() {
        LocalDateTime start = ReportDateRange.startOfDay(LocalDate.of(2026, 3, 30));

        assertEquals(LocalDateTime.of(2026, 3, 30, 0, 0), start);
    }

    @Test
    void exclusiveEndOfDayAdvancesToNextMidnight() {
        LocalDateTime endExclusive = ReportDateRange.exclusiveEndOfDay(LocalDate.of(2026, 3, 30));

        assertEquals(LocalDateTime.of(2026, 3, 31, 0, 0), endExclusive);
    }
}
