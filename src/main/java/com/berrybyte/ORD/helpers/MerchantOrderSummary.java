package com.berrybyte.ORD.helpers;

/**
 * Represents merchant order summary.
 */
public class MerchantOrderSummary {

    private final int orderId;
    private final String orderedDate;
    private final double amount;
    private final String status;
/**
 * Creates a new MerchantOrderSummary instance.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @param orderedDate ordered date
 * @param amount amount
 * @param status status
 */

    public MerchantOrderSummary(int orderId, String orderedDate, double amount, String status) {
        this.orderId = orderId;
        this.orderedDate = orderedDate;
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
 * Returns ordered date.
 *
 * @return result value
 */

    public String getOrderedDate() {
        return orderedDate;
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
