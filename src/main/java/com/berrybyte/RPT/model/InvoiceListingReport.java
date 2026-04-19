package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents invoice listing report.
 */
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
/**
 * Creates a new InvoiceListingReport instance.
 * This method coordinates the main operation for this action.
 *
 * @param title title
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @param generatedAt generated at
 * @param rows rows
 * @param totalInvoices total invoices
 * @param totalInvoicedAmount total invoiced amount
 * @param totalOutstandingAmount total outstanding amount
 */

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
/**
 * Returns title.
 *
 * @return result value
 */

    public String getTitle() {
        return title;
    }
/**
 * Returns merchant id.
 *
 * @return result value
 */

    public Integer getMerchantId() {
        return merchantId;
    }
/**
 * Returns start date.
 *
 * @return result value
 */

    public LocalDate getStartDate() {
        return startDate;
    }
/**
 * Returns end date.
 *
 * @return result value
 */

    public LocalDate getEndDate() {
        return endDate;
    }
/**
 * Returns generated at.
 *
 * @return result value
 */

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
/**
 * Returns rows.
 *
 * @return result value
 */

    public List<InvoiceListingRow> getRows() {
        return rows;
    }
/**
 * Returns total invoices.
 *
 * @return result value
 */

    public int getTotalInvoices() {
        return totalInvoices;
    }
/**
 * Returns total invoiced amount.
 *
 * @return result value
 */

    public BigDecimal getTotalInvoicedAmount() {
        return totalInvoicedAmount;
    }
/**
 * Returns total outstanding amount.
 *
 * @return result value
 */

    public BigDecimal getTotalOutstandingAmount() {
        return totalOutstandingAmount;
    }

/**
 * Performs to string.
 *
 * @return result value
 */
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
