package com.berrybyte.account;

public class DiscountTier {
    private final double minOrderValue;
    private final Double maxOrderValue;
    private final double discountPercent;

    public DiscountTier(double minOrderValue, Double maxOrderValue, double discountPercent) {
        this.minOrderValue = minOrderValue;
        this.maxOrderValue = maxOrderValue;
        this.discountPercent = discountPercent;
    }

    public double getMinOrderValue() {
        return minOrderValue;
    }

    public Double getMaxOrderValue() {
        return maxOrderValue;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }
}
