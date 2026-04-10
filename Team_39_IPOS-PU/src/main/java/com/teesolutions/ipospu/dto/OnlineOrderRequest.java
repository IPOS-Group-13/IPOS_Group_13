package com.teesolutions.ipospu.dto;

import java.util.Collections;
import java.util.List;

public class OnlineOrderRequest {
    private final String orderId;
    private final String deliveryAddress;
    private final String customerEmail;
    private final List<CartLineDto> lines;

    public OnlineOrderRequest(String orderId, String deliveryAddress, List<CartLineDto> lines) {
        this(orderId, deliveryAddress, null, lines);
    }

    public OnlineOrderRequest(String orderId, String deliveryAddress, String customerEmail, List<CartLineDto> lines) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.customerEmail = customerEmail;
        this.lines = lines == null ? Collections.emptyList() : Collections.unmodifiableList(lines);
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public List<CartLineDto> getLines() {
        return lines;
    }
}
