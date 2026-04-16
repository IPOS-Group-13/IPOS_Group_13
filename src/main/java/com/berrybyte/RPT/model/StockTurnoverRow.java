package com.berrybyte.RPT.model;

import java.math.BigDecimal;

public class StockTurnoverRow {
    private final int itemId;
    private final String description;
    private final int quantitySold;
    private final BigDecimal salesValue;

    public StockTurnoverRow(int itemId, String description, int quantitySold, BigDecimal salesValue) {
        this.itemId = itemId;
        this.description = description;
        this.quantitySold = quantitySold;
        this.salesValue = salesValue;
    }

    public int getItemId() {
        return itemId;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public BigDecimal getSalesValue() {
        return salesValue;
    }

    @Override
    public String toString() {
        return "StockTurnoverRow{" +
                "itemId=" + itemId +
                ", description='" + description + '\'' +
                ", quantitySold=" + quantitySold +
                ", salesValue=" + salesValue +
                '}';
    }
}