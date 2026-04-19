package com.berrybyte.RPT.model;

import java.math.BigDecimal;

/**
 * Represents stock turnover row.
 */
public class StockTurnoverRow {
    private final int itemId;
    private final String description;
    private final int quantitySold;
    private final BigDecimal salesValue;
/**
 * Creates a new StockTurnoverRow instance.
 * This method coordinates the main operation for this action.
 *
 * @param itemId item id
 * @param description description
 * @param quantitySold quantity sold
 * @param salesValue sales value
 */

    public StockTurnoverRow(int itemId, String description, int quantitySold, BigDecimal salesValue) {
        this.itemId = itemId;
        this.description = description;
        this.quantitySold = quantitySold;
        this.salesValue = salesValue;
    }
/**
 * Returns item id.
 *
 * @return result value
 */

    public int getItemId() {
        return itemId;
    }
/**
 * Returns description.
 *
 * @return result value
 */

    public String getDescription() {
        return description;
    }
/**
 * Returns quantity sold.
 *
 * @return result value
 */

    public int getQuantitySold() {
        return quantitySold;
    }
/**
 * Returns sales value.
 *
 * @return result value
 */

    public BigDecimal getSalesValue() {
        return salesValue;
    }

/**
 * Performs to string.
 *
 * @return result value
 */
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
