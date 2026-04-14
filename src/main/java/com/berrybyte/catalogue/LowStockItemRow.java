package com.berrybyte.catalogue;

public class LowStockItemRow {

    private final int itemId;
    private final String description;
    private final int availabilityPacks;
    private final int stockLimitPacks;

    public LowStockItemRow(int itemId, String description, int availabilityPacks, int stockLimitPacks) {
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
}
