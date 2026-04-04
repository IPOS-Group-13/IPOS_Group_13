package com.teesolutions.ipospu.dto;

public class CartLineDto {
    private final String productId;
    private final int quantity;

    public CartLineDto(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
