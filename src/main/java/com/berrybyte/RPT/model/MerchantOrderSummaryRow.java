package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents merchant order summary row.
 */
public class MerchantOrderSummaryRow {
    private final int orderId;
    private final LocalDate orderDate;
    private final BigDecimal totalAmount;
    private final LocalDateTime dispatchDateTime;
    private final LocalDateTime deliveredDateTime;
    private final String orderStatus;
    private final String paymentStatus;
/**
 * Creates a new MerchantOrderSummaryRow instance.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @param orderDate order date
 * @param totalAmount total amount
 * @param dispatchDateTime dispatch date time
 * @param deliveredDateTime delivered date time
 * @param orderStatus order status
 * @param paymentStatus payment status
 */

    public MerchantOrderSummaryRow(int orderId,
                                   LocalDate orderDate,
                                   BigDecimal totalAmount,
                                   LocalDateTime dispatchDateTime,
                                   LocalDateTime deliveredDateTime,
                                   String orderStatus,
                                   String paymentStatus) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.dispatchDateTime = dispatchDateTime;
        this.deliveredDateTime = deliveredDateTime;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
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
 * Returns order date.
 *
 * @return result value
 */

    public LocalDate getOrderDate() {
        return orderDate;
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
 * Returns dispatch date time.
 *
 * @return result value
 */

    public LocalDateTime getDispatchDateTime() {
        return dispatchDateTime;
    }
/**
 * Returns delivered date time.
 *
 * @return result value
 */

    public LocalDateTime getDeliveredDateTime() {
        return deliveredDateTime;
    }
/**
 * Returns order status.
 *
 * @return result value
 */

    public String getOrderStatus() {
        return orderStatus;
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
        return "MerchantOrderSummaryRow{" +
                "orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", dispatchDateTime=" + dispatchDateTime +
                ", deliveredDateTime=" + deliveredDateTime +
                ", orderStatus='" + orderStatus + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
