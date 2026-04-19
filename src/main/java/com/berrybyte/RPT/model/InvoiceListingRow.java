package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents invoice listing row.
 */
public class InvoiceListingRow {
    private final int invoiceId;
    private final int orderId;
    private final int merchantId;
    private final LocalDate invoiceDate;
    private final LocalDate dueDate;
    private final BigDecimal totalAmount;
    private final BigDecimal amountPaid;
    private final BigDecimal outstandingBalance;
    private final String paymentStatus;
/**
 * Creates a new InvoiceListingRow instance.
 * This method coordinates the main operation for this action.
 *
 * @param invoiceId invoice id
 * @param orderId order id
 * @param merchantId merchant id
 * @param invoiceDate invoice date
 * @param dueDate due date
 * @param totalAmount total amount
 * @param amountPaid amount paid
 * @param outstandingBalance outstanding balance
 * @param paymentStatus payment status
 */

    public InvoiceListingRow(int invoiceId,
                             int orderId,
                             int merchantId,
                             LocalDate invoiceDate,
                             LocalDate dueDate,
                             BigDecimal totalAmount,
                             BigDecimal amountPaid,
                             BigDecimal outstandingBalance,
                             String paymentStatus) {
        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.merchantId = merchantId;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.outstandingBalance = outstandingBalance;
        this.paymentStatus = paymentStatus;
    }
/**
 * Returns invoice id.
 *
 * @return result value
 */

    public int getInvoiceId() {
        return invoiceId;
    }
/**
 * Returns order id.
 *
 * @return result value
 */

    public int getOrderId() {
        return orderId;
    }
/**
 * Returns merchant id.
 *
 * @return result value
 */

    public int getMerchantId() {
        return merchantId;
    }
/**
 * Returns invoice date.
 *
 * @return result value
 */

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }
/**
 * Returns due date.
 *
 * @return result value
 */

    public LocalDate getDueDate() {
        return dueDate;
    }
/**
 * Returns total amount.
 *
 * @return result value
 */

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
/**
 * Returns amount paid.
 *
 * @return result value
 */

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }
/**
 * Returns outstanding balance.
 *
 * @return result value
 */

    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }
/**
 * Returns payment status.
 *
 * @return result value
 */

    public String getPaymentStatus() {
        return paymentStatus;
    }

/**
 * Performs to string.
 *
 * @return result value
 */
    @Override
    public String toString() {
        return "InvoiceListingRow{" +
                "invoiceId=" + invoiceId +
                ", orderId=" + orderId +
                ", merchantId=" + merchantId +
                ", invoiceDate=" + invoiceDate +
                ", dueDate=" + dueDate +
                ", totalAmount=" + totalAmount +
                ", amountPaid=" + amountPaid +
                ", outstandingBalance=" + outstandingBalance +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
