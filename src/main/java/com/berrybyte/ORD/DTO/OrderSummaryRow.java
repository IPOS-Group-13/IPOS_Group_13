package com.berrybyte.ORD.DTO;

public class OrderSummaryRow {

    private final int orderId;
    private final String orderedDate;
    private final String dispatchedDate;
    private final double amount;
    private final String deliveredStatus;
    private final String paidStatus;
    private final String courierName;
    private final String courierRef;
    private final String expectedDelivery;

    public OrderSummaryRow(int orderId,
                           String orderedDate,
                           String dispatchedDate,
                           double amount,
                           String deliveredStatus,
                           String paidStatus,
                           String courierName,
                           String courierRef,
                           String expectedDelivery) {
        this.orderId = orderId;
        this.orderedDate = orderedDate;
        this.dispatchedDate = dispatchedDate;
        this.amount = amount;
        this.deliveredStatus = deliveredStatus;
        this.paidStatus = paidStatus;
        this.courierName = courierName;
        this.courierRef = courierRef;
        this.expectedDelivery = expectedDelivery;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getOrderedDate() {
        return orderedDate;
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
}
