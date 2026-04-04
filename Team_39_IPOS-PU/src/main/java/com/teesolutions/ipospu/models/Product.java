package com.teesolutions.ipospu.models;

public class Product {
    private String productId;
    private String name;
    private String description;
    private double retailPrice;
    private int stockQuantity;
    private double vatRate; // The brief specifies a configurable VAT rate

    public Product(String productId, String name, String description, double retailPrice, int stockQuantity, double vatRate) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.retailPrice = retailPrice;
        this.stockQuantity = stockQuantity;
        this.vatRate = vatRate;
    }

    // --- Getters ---
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getRetailPrice() { return retailPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public double getVatRate() { return vatRate; }

    // --- Setters ---
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public void setRetailPrice(double retailPrice) { this.retailPrice = retailPrice; }

    // --- Business Logic ---
    /**
     * Calculates the final price including the VAT rate.
     */
    public double getPriceWithVat() {
        return retailPrice + (retailPrice * vatRate);
    }
}