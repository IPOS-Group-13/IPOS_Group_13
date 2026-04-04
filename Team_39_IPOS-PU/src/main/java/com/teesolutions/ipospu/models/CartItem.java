package com.teesolutions.ipospu.models;

public class CartItem {
    private final String productId;
    private final String productName;
    private final double unitPrice;
    private int quantity;
    private double discountPercent;

    public CartItem(String productId, String productName, double unitPrice, int quantity, double discountPercent) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discountPercent = discountPercent;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getLineTotal() {
        return quantity * unitPrice * (1 - discountPercent / 100.0);
    }
}
