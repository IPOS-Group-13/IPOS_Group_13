package com.berrybyte.RPT.model;

import java.math.BigDecimal;

/**
 * Represents merchant activity item row.
 */
public class MerchantActivityItemRow {
    private final int itemId;
    private final String description;
    private final int quantity;
    private final BigDecimal unitCost;
    private final BigDecimal lineTotal;
/**
 * Creates a new MerchantActivityItemRow instance.
 * This method coordinates the main operation for this action.
 *
 * @param itemId item id
 * @param description description
 * @param quantity quantity
 * @param unitCost unit cost
 * @param lineTotal line total
 */

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
 * Returns quantity.
 *
 * @return result value
 */

    public int getQuantity() {
        return quantity;
    }
/**
 * Returns unit cost.
 *
 * @return result value
 */

    public BigDecimal getUnitCost() {
        return unitCost;
    }
/**
 * Returns line total.
 *
 * @return result value
 */

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

/**
 * Performs to string.
 *
 * @return result value
 */
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
