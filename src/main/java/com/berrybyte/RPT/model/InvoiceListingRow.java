package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    public int getInvoiceId() {
        return invoiceId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

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