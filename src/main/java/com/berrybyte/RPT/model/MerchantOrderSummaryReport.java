package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MerchantOrderSummaryReport {
    private final String title;
    private final int merchantId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDateTime generatedAt;
    private final List<MerchantOrderSummaryRow> rows;
    private final int totalOrders;
    private final BigDecimal totalOrderValue;

    public MerchantOrderSummaryReport(String title,
                                      int merchantId,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      LocalDateTime generatedAt,
                                      List<MerchantOrderSummaryRow> rows,
                                      int totalOrders,
                                      BigDecimal totalOrderValue) {
        this.title = title;
        this.merchantId = merchantId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedAt = generatedAt;
        this.rows = rows;
        this.totalOrders = totalOrders;
        this.totalOrderValue = totalOrderValue;
    }

    public String getTitle() {
        return title;
    }

    public int getMerchantId() {
        return merchantId;
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

    public List<MerchantOrderSummaryRow> getRows() {
        return rows;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public BigDecimal getTotalOrderValue() {
        return totalOrderValue;
    }

    @Override
    public String toString() {
        return "MerchantOrderSummaryReport{" +
                "title='" + title + '\'' +
                ", merchantId=" + merchantId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", generatedAt=" + generatedAt +
                ", rows=" + rows +
                ", totalOrders=" + totalOrders +
                ", totalOrderValue=" + totalOrderValue +
                '}';
    }
}