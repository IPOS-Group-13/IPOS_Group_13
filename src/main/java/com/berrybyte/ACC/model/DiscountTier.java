package com.berrybyte.ACC.model;

/**
 * Represents discount tier.
 */
public class DiscountTier {
    private final double minOrderValue;
    private final Double maxOrderValue;
    private final double discountPercent;

/**
 * Creates a new DiscountTier instance.
 * This method coordinates the main operation for this action.
 *
 * @param minOrderValue min order value
 * @param maxOrderValue max order value
 * @param discountPercent discount percent
 */
    public DiscountTier(double minOrderValue, Double maxOrderValue, double discountPercent) {
        this.minOrderValue = minOrderValue;
        this.maxOrderValue = maxOrderValue;
        this.discountPercent = discountPercent;
    }

/**
 * Returns min order value.
 *
 * @return result value
 */
    public double getMinOrderValue() {
        return minOrderValue;
    }

/**
 * Returns max order value.
 *
 * @return result value
 */
    public Double getMaxOrderValue() {
        return maxOrderValue;
    }

/**
 * Returns discount percent.
 *
 * @return result value
 */
    public double getDiscountPercent() {
        return discountPercent;
    }
}
