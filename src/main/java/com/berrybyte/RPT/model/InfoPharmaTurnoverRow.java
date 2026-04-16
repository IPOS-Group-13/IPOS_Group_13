package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InfoPharmaTurnoverRow {
    private final int invoiceId;
    private final int orderId;
    private final int merchantId;
    private final LocalDate invoiceDate;
    private final BigDecimal totalAmount;
    private final BigDecimal amountPaid;
    private final BigDecimal outstandingBalance;
    private final String paymentStatus;

    public InfoPharmaTurnoverRow(int invoiceId,
                                 int orderId,
                                 int merchantId,
                                 LocalDate invoiceDate,
                                 BigDecimal totalAmount,
                                 BigDecimal amountPaid,
                                 BigDecimal outstandingBalance,
                                 String paymentStatus) {
        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.merchantId = merchantId;
        this.invoiceDate = invoiceDate;
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
}