package com.berrybyte.ORD.DTO;

public class OrderLine {

    private final int itemId;
    private final String description;
    private final String packageType;
    private final int unitsInPacks;
    private final double packsCost;
    private final int quantity;
    private final double lineTotal;

    public OrderLine(int itemId, String description, String packageType,
                     int unitsInPacks, double packsCost, int quantity, double lineTotal) {
        this.itemId = itemId;
        this.description = description;
        this.packageType = packageType;
        this.unitsInPacks = unitsInPacks;
        this.packsCost = packsCost;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
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

    public int getUnitsInPacks() {
        return unitsInPacks;
    }

    public double getPacksCost() {
        return packsCost;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLineTotal() {
        return lineTotal;
    }
}