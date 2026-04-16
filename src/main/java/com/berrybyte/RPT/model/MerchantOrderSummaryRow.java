package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MerchantOrderSummaryRow {
    private final int orderId;
    private final LocalDate orderDate;
    private final BigDecimal totalAmount;
    private final LocalDateTime dispatchDateTime;
    private final LocalDateTime deliveredDateTime;
    private final String orderStatus;
    private final String paymentStatus;

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

    public int getOrderId() {
        return orderId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getDispatchDateTime() {
        return dispatchDateTime;
    }

    public LocalDateTime getDeliveredDateTime() {
        return deliveredDateTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

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