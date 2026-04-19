package com.berrybyte.ORD.helpers;

/**
 * Represents order summary row.
 */
public class OrderSummaryRow {

    private final int orderId;
    private final String merchantName;
    private final String dispatchedDate;
    private final double amount;
    private final String deliveredStatus;
    private final String paidStatus;
    private final String courierName;
    private final String courierRef;
    private final String expectedDelivery;
    private final String deliveryDate;
/**
 * Creates a new OrderSummaryRow instance.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @param merchantName merchant name
 * @param dispatchedDate dispatched date
 * @param amount amount
 * @param deliveredStatus delivered status
 * @param paidStatus paid status
 * @param courierName courier name
 * @param courierRef courier ref
 * @param expectedDelivery expected delivery
 * @param deliveryDate delivery date
 */

    public OrderSummaryRow(int orderId,
                           String merchantName,
                           String dispatchedDate,
                           double amount,
                           String deliveredStatus,
                           String paidStatus,
                           String courierName,
                           String courierRef,
                           String expectedDelivery,
                           String deliveryDate) {
        this.orderId = orderId;
        this.merchantName = merchantName;
        this.dispatchedDate = dispatchedDate;
        this.amount = amount;
        this.deliveredStatus = deliveredStatus;
        this.paidStatus = paidStatus;
        this.courierName = courierName;
        this.courierRef = courierRef;
        this.expectedDelivery = expectedDelivery;
        this.deliveryDate = deliveryDate;
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
 * Returns merchant name.
 *
 * @return result value
 */

    public String getMerchantName() {
        return merchantName;
    }
/**
 * Returns dispatched date.
 *
 * @return result value
 */

    public String getDispatchedDate() {
        return dispatchedDate;
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
 * Returns delivered status.
 *
 * @return result value
 */

    public String getDeliveredStatus() {
        return deliveredStatus;
    }
/**
 * Returns paid status.
 *
 * @return result value
 */

    public String getPaidStatus() {
        return paidStatus;
    }
/**
 * Returns courier name.
 *
 * @return result value
 */

    public String getCourierName() {
        return courierName;
    }
/**
 * Returns courier ref.
 *
 * @return result value
 */

    public String getCourierRef() {
        return courierRef;
    }
/**
 * Returns expected delivery.
 *
 * @return result value
 */

    public String getExpectedDelivery() {
        return expectedDelivery;
    }
/**
 * Returns delivery date.
 *
 * @return result value
 */

    public String getDeliveryDate() {
        return deliveryDate;
    }
}
