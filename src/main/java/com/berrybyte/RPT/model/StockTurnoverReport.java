package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class StockTurnoverReport {
    private final String title;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDateTime generatedAt;
    private final List<StockTurnoverRow> rows;
    private final int totalQuantitySold;
    private final BigDecimal totalSalesValue;

    public StockTurnoverReport(String title,
                               LocalDate startDate,
                               LocalDate endDate,
                               LocalDateTime generatedAt,
                               List<StockTurnoverRow> rows,
                               int totalQuantitySold,
                               BigDecimal totalSalesValue) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedAt = generatedAt;
        this.rows = rows;
        this.totalQuantitySold = totalQuantitySold;
        this.totalSalesValue = totalSalesValue;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public List<StockTurnoverRow> getRows() {
        return rows;
    }

    public int getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public BigDecimal getTotalSalesValue() {
        return totalSalesValue;
    }

    @Override
    public String toString() {
        return "StockTurnoverReport{" +
                "title='" + title + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", generatedAt=" + generatedAt +
                ", rows=" + rows +
                ", totalQuantitySold=" + totalQuantitySold +
                ", totalSalesValue=" + totalSalesValue +
                '}';
    }
}