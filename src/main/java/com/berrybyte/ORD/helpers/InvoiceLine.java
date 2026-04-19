package com.berrybyte.ORD.helpers;

/**
 * Represents invoice line.
 */
public class InvoiceLine {

    private final int itemId;
    private final String description;
    private final String packageType;
    private final int unitsInPacks;
    private final double packsCost;
    private final int quantity;
    private final double lineTotal;
/**
 * Creates a new InvoiceLine instance.
 * This method coordinates the main operation for this action.
 *
 * @param itemId item id
 * @param description description
 * @param packageType package type
 * @param unitsInPacks units in packs
 * @param packsCost packs cost
 * @param quantity quantity
 * @param lineTotal line total
 */

    public InvoiceLine(int itemId, String description, String packageType,
                       int unitsInPacks, double packsCost, int quantity, double lineTotal) {
        this.itemId = itemId;
        this.description = description;
        this.packageType = packageType;
        this.unitsInPacks = unitsInPacks;
        this.packsCost = packsCost;
        this.quantity = quantity;
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
 * Returns package type.
 *
 * @return result value
 */

    public String getPackageType() {
        return packageType;
    }
/**
 * Returns units in packs.
 *
 * @return result value
 */

    public int getUnitsInPacks() {
        return unitsInPacks;
    }
/**
 * Returns packs cost.
 *
 * @return result value
 */

    public double getPacksCost() {
        return packsCost;
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
 * Returns line total.
 *
 * @return result value
 */

    public double getLineTotal() {
        return lineTotal;
    }
}
