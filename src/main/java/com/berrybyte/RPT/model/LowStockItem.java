package com.berrybyte.RPT.model;

public class LowStockItem {
    private final int itemId;
    private final String description;
    private final int availabilityPacks;
    private final int stockLimitPacks;

    public LowStockItem(int itemId, String description, int availabilityPacks, int stockLimitPacks) {
        this.itemId = itemId;
        this.description = description;
        this.availabilityPacks = availabilityPacks;
        this.stockLimitPacks = stockLimitPacks;
    }

    public int getItemId() {
        return itemId;
    }

    public String getDescription() {
        return description;
    }

    public int getAvailabilityPacks() {
        return availabilityPacks;
    }

    public int getStockLimitPacks() {
        return stockLimitPacks;
    }

    @Override
    public String toString() {
        return "LowStockItem{" +
                "itemId=" + itemId +
                ", description='" + description + '\'' +
                ", availabilityPacks=" + availabilityPacks +
                ", stockLimitPacks=" + stockLimitPacks +
                '}';
    }
}