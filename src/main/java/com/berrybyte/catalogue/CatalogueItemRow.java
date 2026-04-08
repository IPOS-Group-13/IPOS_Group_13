package com.berrybyte.catalogue;

public class CatalogueItemRow {
    private final int itemId;
    private final String description;
    private final String packageType;
    private final String unit;
    private final int unitsInPack;
    private final String packageCost;
    private final int availabilityPacks;
    private final int stockLimitPacks;

    public CatalogueItemRow(int itemId,
                            String description,
                            String packageType,
                            String unit,
                            int unitsInPack,
                            double packageCost,
                            int availabilityPacks,
                            int stockLimitPacks) {
        this.itemId = itemId;
        this.description = description;
        this.packageType = packageType;
        this.unit = unit;
        this.unitsInPack = unitsInPack;
        this.packageCost = String.format("%.2f", packageCost);
        this.availabilityPacks = availabilityPacks;
        this.stockLimitPacks = stockLimitPacks;
    }

    public int getItemId() {
        return itemId;
    }

    public String getDescription() {
        return description;
    }

    public String getPackageType() {
        return packageType;
    }

    public String getUnit() {
        return unit;
    }

    public int getUnitsInPack() {
        return unitsInPack;
    }

    public String getPackageCost() {
        return packageCost;
    }

    public int getAvailabilityPacks() {
        return availabilityPacks;
    }

    public int getStockLimitPacks() {
        return stockLimitPacks;
    }
}
