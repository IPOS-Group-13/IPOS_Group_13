package com.teesolutions.ipospu.services;

import com.teesolutions.ipospu.repositories.ReportRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReportService {
    private final ReportRepository reportRepository = new ReportRepository();

    public List<Map<String, Object>> salesReport(LocalDateTime start, LocalDateTime end) {
        validateDateRange(start, end);
        return reportRepository.salesReport(start, end);
    }

    public List<Map<String, Object>> campaignsReport(LocalDateTime start, LocalDateTime end) {
        validateDateRange(start, end);
        return reportRepository.campaignsReport(start, end);
    }

    public List<Map<String, Object>> engagementReport(LocalDateTime start, LocalDateTime end) {
        validateDateRange(start, end);
        return reportRepository.engagementReport(start, end);
    }

    private void validateDateRange(LocalDateTime start, LocalDateTime endExclusive) {
        if (start == null || endExclusive == null || !start.isBefore(endExclusive)) {
            throw new IllegalArgumentException("Report start date must be on or before the end date");
        }
    }
}
