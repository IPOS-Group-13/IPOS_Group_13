package com.teesolutions.ipospu.models;

import java.time.LocalDateTime;

public class Order {
    private String orderId;
    private int userId; // Links back to the User who made the purchase
    private double totalAmount;
    private String status; // "RECEIVED", "DISPATCHED", "DELIVERED", "VOID"
    private String trackingCode;
    private LocalDateTime orderDate;

    public Order(String orderId, int userId, double totalAmount, String status, String trackingCode, LocalDateTime orderDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.trackingCode = trackingCode;
        this.orderDate = orderDate;
    }

    // --- Getters ---
    public String getOrderId() { return orderId; }
    public int getUserId() { return userId; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getTrackingCode() { return trackingCode; }
    public LocalDateTime getOrderDate() { return orderDate; }

    // --- Setters ---
    public void setStatus(String status) { this.status = status; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
}