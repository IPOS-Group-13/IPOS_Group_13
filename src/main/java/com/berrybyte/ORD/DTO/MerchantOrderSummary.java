package com.berrybyte.ORD.DTO;

public class MerchantOrderSummary {

    private final int orderId;
    private final String orderedDate;
    private final double amount;
    private final String status;

    public MerchantOrderSummary(int orderId, String orderedDate, double amount, String status) {
        this.orderId = orderId;
        this.orderedDate = orderedDate;
        this.amount = amount;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getOrderedDate() {
        return orderedDate;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}