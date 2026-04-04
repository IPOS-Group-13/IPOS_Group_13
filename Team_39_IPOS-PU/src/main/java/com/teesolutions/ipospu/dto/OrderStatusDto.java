package com.teesolutions.ipospu.dto;

public class OrderStatusDto {
    private final String orderId;
    private final String status;

    public OrderStatusDto(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }
}
