package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class InvoiceListingReport {
    private final String title;
    private final Integer merchantId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDateTime generatedAt;
    private final List<InvoiceListingRow> rows;
    private final int totalInvoices;
    private final BigDecimal totalInvoicedAmount;
    private final BigDecimal totalOutstandingAmount;

    public InvoiceListingReport(String title,
                                Integer merchantId,
                                LocalDate startDate,
                                LocalDate endDate,
                                LocalDateTime generatedAt,
                                List<InvoiceListingRow> rows,
                                int totalInvoices,
                                BigDecimal totalInvoicedAmount,
                                BigDecimal totalOutstandingAmount) {
        this.title = title;
        this.merchantId = merchantId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedAt = generatedAt;
        this.rows = rows;
        this.totalInvoices = totalInvoices;
        this.totalInvoicedAmount = totalInvoicedAmount;
        this.totalOutstandingAmount = totalOutstandingAmount;
    }

    public String getTitle() {
        return title;
    }

    public Integer getMerchantId() {
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

    public List<InvoiceListingRow> getRows() {
        return rows;
    }

    public int getTotalInvoices() {
        return totalInvoices;
    }

    public BigDecimal getTotalInvoicedAmount() {
        return totalInvoicedAmount;
    }

    public BigDecimal getTotalOutstandingAmount() {
        return totalOutstandingAmount;
    }

    @Override
    public String toString() {
        return "InvoiceListingReport{" +
                "title='" + title + '\'' +
                ", merchantId=" + merchantId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", generatedAt=" + generatedAt +
                ", rows=" + rows +
                ", totalInvoices=" + totalInvoices +
                ", totalInvoicedAmount=" + totalInvoicedAmount +
                ", totalOutstandingAmount=" + totalOutstandingAmount +
                '}';
    }
}