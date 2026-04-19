package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents info pharma turnover report.
 */
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
/**
 * Creates a new InfoPharmaTurnoverReport instance.
 * This method coordinates the main operation for this action.
 *
 * @param title title
 * @param startDate start date
 * @param endDate end date
 * @param generatedAt generated at
 * @param rows rows
 * @param totalInvoices total invoices
 * @param totalInvoicedAmount total invoiced amount
 * @param totalPaidAmount total paid amount
 * @param totalOutstandingAmount total outstanding amount
 * @param paidInvoiceCount paid invoice count
 * @param unpaidInvoiceCount unpaid invoice count
 * @param partialInvoiceCount partial invoice count
 */

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
/**
 * Returns title.
 *
 * @return result value
 */

    public String getTitle() {
        return title;
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

    public List<InfoPharmaTurnoverRow> getRows() {
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
 * Returns total paid amount.
 *
 * @return result value
 */

    public BigDecimal getTotalPaidAmount() {
        return totalPaidAmount;
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
 * Returns paid invoice count.
 *
 * @return result value
 */

    public int getPaidInvoiceCount() {
        return paidInvoiceCount;
    }
/**
 * Returns unpaid invoice count.
 *
 * @return result value
 */

    public int getUnpaidInvoiceCount() {
        return unpaidInvoiceCount;
    }
/**
 * Returns partial invoice count.
 *
 * @return result value
 */

    public int getPartialInvoiceCount() {
        return partialInvoiceCount;
    }
}
