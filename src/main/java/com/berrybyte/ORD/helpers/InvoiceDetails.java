package com.berrybyte.ORD.helpers;

import java.util.List;

/**
 * Represents invoice details.
 */
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
/**
 * Creates a new InvoiceDetails instance.
 * This method coordinates the main operation for this action.
 *
 * @param invoiceId invoice id
 * @param orderId order id
 * @param merchantId merchant id
 * @param iposAccountNumber ipos account number
 * @param companyName company name
 * @param merchantName merchant name
 * @param email email
 * @param phoneNumber phone number
 * @param address address
 * @param invoiceDate invoice date
 * @param dueDate due date
 * @param totalAmount total amount
 * @param amountPaid amount paid
 * @param outstandingBalance outstanding balance
 * @param paymentStatus payment status
 * @param items items
 */

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
 * Returns ipos account number.
 *
 * @return result value
 */

    public String getIposAccountNumber() {
        return iposAccountNumber;
    }
/**
 * Returns company name.
 *
 * @return result value
 */

    public String getCompanyName() {
        return companyName;
    }
/**
 * Returns merchant name.
 *
 * @return result value
 */

    public String getMerchantName() {
        return merchantName;
    }
/**
 * Returns email.
 *
 * @return result value
 */

    public String getEmail() {
        return email;
    }
/**
 * Returns phone number.
 *
 * @return result value
 */

    public String getPhoneNumber() {
        return phoneNumber;
    }
/**
 * Returns address.
 *
 * @return result value
 */

    public String getAddress() {
        return address;
    }
/**
 * Returns invoice date.
 *
 * @return result value
 */

    public String getInvoiceDate() {
        return invoiceDate;
    }
/**
 * Returns due date.
 *
 * @return result value
 */

    public String getDueDate() {
        return dueDate;
    }
/**
 * Returns total amount.
 *
 * @return result value
 */

    public double getTotalAmount() {
        return totalAmount;
    }
/**
 * Returns amount paid.
 *
 * @return result value
 */

    public double getAmountPaid() {
        return amountPaid;
    }
/**
 * Returns outstanding balance.
 *
 * @return result value
 */

    public double getOutstandingBalance() {
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
 * Returns items.
 *
 * @return result value
 */

    public List<InvoiceLine> getItems() {
        return items;
    }
}
