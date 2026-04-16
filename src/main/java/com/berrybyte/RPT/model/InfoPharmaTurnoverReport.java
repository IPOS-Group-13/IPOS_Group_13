package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class InfoPharmaTurnoverReport {
    private final String title;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDateTime generatedAt;
    private final List<InfoPharmaTurnoverRow> rows;
    private final int totalInvoices;
    private final BigDecimal totalInvoicedAmount;
    private final BigDecimal totalPaidAmount;
    private final BigDecimal totalOutstandingAmount;
    private final int paidInvoiceCount;
    private final int unpaidInvoiceCount;
    private final int partialInvoiceCount;

    public InfoPharmaTurnoverReport(String title,
                                    LocalDate startDate,
                                    LocalDate endDate,
                                    LocalDateTime generatedAt,
                                    List<InfoPharmaTurnoverRow> rows,
                                    int totalInvoices,
                                    BigDecimal totalInvoicedAmount,
                                    BigDecimal totalPaidAmount,
                                    BigDecimal totalOutstandingAmount,
                                    int paidInvoiceCount,
                                    int unpaidInvoiceCount,
                                    int partialInvoiceCount) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedAt = generatedAt;
        this.rows = rows;
        this.totalInvoices = totalInvoices;
        this.totalInvoicedAmount = totalInvoicedAmount;
        this.totalPaidAmount = totalPaidAmount;
        this.totalOutstandingAmount = totalOutstandingAmount;
        this.paidInvoiceCount = paidInvoiceCount;
        this.unpaidInvoiceCount = unpaidInvoiceCount;
        this.partialInvoiceCount = partialInvoiceCount;
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

    public List<InfoPharmaTurnoverRow> getRows() {
        return rows;
    }

    public int getTotalInvoices() {
        return totalInvoices;
    }

    public BigDecimal getTotalInvoicedAmount() {
        return totalInvoicedAmount;
    }

    public BigDecimal getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public BigDecimal getTotalOutstandingAmount() {
        return totalOutstandingAmount;
    }

    public int getPaidInvoiceCount() {
        return paidInvoiceCount;
    }

    public int getUnpaidInvoiceCount() {
        return unpaidInvoiceCount;
    }

    public int getPartialInvoiceCount() {
        return partialInvoiceCount;
    }
}