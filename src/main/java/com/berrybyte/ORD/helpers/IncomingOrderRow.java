package com.berrybyte.ORD.helpers;

/**
 * Represents incoming order row.
 */
public class IncomingOrderRow {

    private final int orderId;
    private final String accountHolder;
    private final String date;
    private final double amount;
    private final String status;
/**
 * Creates a new IncomingOrderRow instance.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @param accountHolder account holder
 * @param date date
 * @param amount amount
 * @param status status
 */

    public IncomingOrderRow(int orderId, String accountHolder, String date,
                            double amount, String status) {
        this.orderId = orderId;
        this.accountHolder = accountHolder;
        this.date = date;
        this.amount = amount;
        this.status = status;
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
 * Returns account holder.
 *
 * @return result value
 */

    public String getAccountHolder() {
        return accountHolder;
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
 * Returns amount.
 *
 * @return result value
 */

    public double getAmount() {
        return amount;
    }
/**
 * Returns status.
 *
 * @return result value
 */

    public String getStatus() {
        return status;
    }
}
