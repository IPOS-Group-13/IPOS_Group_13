package com.teesolutions.ipospu.dto;

public class InventoryItemDto {
    private final String productId;
    private final String name;
    private final String description;
    private final double retailPrice;
    private final int stockQuantity;

    public InventoryItemDto(String productId, String name, String description, double retailPrice, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.retailPrice = retailPrice;
        this.stockQuantity = stockQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }
}
