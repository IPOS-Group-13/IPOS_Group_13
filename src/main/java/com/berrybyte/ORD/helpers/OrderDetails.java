package com.berrybyte.ORD.helpers;

/**
 * Represents order details.
 */
public class OrderDetails {

    private final int orderId;
    private final int merchantId;
    private final String iposAccountNumber;
    private final String accountHolder;
    private final String companyName;
    private final String address;
    private final String date;
    private final String status;
    private final double totalAmount;
/**
 * Creates a new OrderDetails instance.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @param merchantId merchant id
 * @param iposAccountNumber ipos account number
 * @param accountHolder account holder
 * @param companyName company name
 * @param address address
 * @param date date
 * @param status status
 * @param totalAmount total amount
 */

    public OrderDetails(int orderId, int merchantId, String iposAccountNumber,
                        String accountHolder, String companyName, String address,
                        String date, String status, double totalAmount) {
        this.orderId = orderId;
        this.merchantId = merchantId;
        this.iposAccountNumber = iposAccountNumber;
        this.accountHolder = accountHolder;
        this.companyName = companyName;
        this.address = address;
        this.date = date;
        this.status = status;
        this.totalAmount = totalAmount;
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
 * Returns account holder.
 *
 * @return result value
 */

    public String getAccountHolder() {
        return accountHolder;
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
 * Returns address.
 *
 * @return result value
 */

    public String getAddress() {
        return address;
    }
/**
 * Returns date.
 *
 * @return result value
 */

    public String getDate() {
        return date;
    }
/**
 * Returns status.
 *
 * @return result value
 */

    public String getStatus() {
        return status;
    }
/**
 * Returns total amount.
 *
 * @return result value
 */

    public double getTotalAmount() {
        return totalAmount;
    }
}
