package com.berrybyte.ORD.helpers;

import java.util.List;

public class InvoiceDetails {

    private final int invoiceId;
    private final int orderId;
    private final int merchantId;
    private final String iposAccountNumber;
    private final String companyName;
    private final String merchantName;
    private final String email;
    private final String phoneNumber;
    private final String address;
    private final String invoiceDate;
    private final String dueDate;
    private final double totalAmount;
    private final double amountPaid;
    private final double outstandingBalance;
    private final String paymentStatus;
    private final List<InvoiceLine> items;

    public InvoiceDetails(int invoiceId, int orderId, int merchantId,
                          String iposAccountNumber, String companyName,
                          String merchantName, String email,
                          String phoneNumber, String address,
                          String invoiceDate, String dueDate,
                          double totalAmount, double amountPaid,
                          double outstandingBalance, String paymentStatus,
                          List<InvoiceLine> items) {
        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.merchantId = merchantId;
        this.iposAccountNumber = iposAccountNumber;
        this.companyName = companyName;
        this.merchantName = merchantName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.outstandingBalance = outstandingBalance;
        this.paymentStatus = paymentStatus;
        this.items = items;
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

    public String getIposAccountNumber() {
        return iposAccountNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public double getOutstandingBalance() {
        return outstandingBalance;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public List<InvoiceLine> getItems() {
        return items;
    }
}
