package com.berrybyte.RPT.model;

import java.math.BigDecimal;

public class MerchantActivityItemRow {
    private final int itemId;
    private final String description;
    private final int quantity;
    private final BigDecimal unitCost;
    private final BigDecimal lineTotal;

    public MerchantActivityItemRow(int itemId,
                                   String description,
                                   int quantity,
                                   BigDecimal unitCost,
                                   BigDecimal lineTotal) {
        this.itemId = itemId;
        this.description = description;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.lineTotal = lineTotal;
    }

    public int getItemId() {
        return itemId;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    @Override
    public String toString() {
        return "MerchantActivityItemRow{" +
                "itemId=" + itemId +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", unitCost=" + unitCost +
                ", lineTotal=" + lineTotal +
                '}';
    }
}