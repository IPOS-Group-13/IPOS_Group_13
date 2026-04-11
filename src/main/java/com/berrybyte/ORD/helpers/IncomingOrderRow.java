package com.berrybyte.ORD.helpers;

public class IncomingOrderRow {

    private final int orderId;
    private final String accountHolder;
    private final String date;
    private final double amount;
    private final String status;

    public IncomingOrderRow(int orderId, String accountHolder, String date,
                            double amount, String status) {
        this.orderId = orderId;
        this.accountHolder = accountHolder;
        this.date = date;
        this.amount = amount;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}