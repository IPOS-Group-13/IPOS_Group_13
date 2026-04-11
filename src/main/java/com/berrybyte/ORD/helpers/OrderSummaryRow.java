package com.berrybyte.ORD.helpers;

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

    public int getOrderId() {
        return orderId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getDispatchedDate() {
        return dispatchedDate;
    }

    public double getAmount() {
        return amount;
    }

    public String getDeliveredStatus() {
        return deliveredStatus;
    }

    public String getPaidStatus() {
        return paidStatus;
    }

    public String getCourierName() {
        return courierName;
    }

    public String getCourierRef() {
        return courierRef;
    }

    public String getExpectedDelivery() {
        return expectedDelivery;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }
}
