package com.berrybyte.catalogue;

/**
 * Represents low stock item row.
 */
public class LowStockItemRow {

    private final int itemId;
    private final String description;
    private final int availabilityPacks;
    private final int stockLimitPacks;
/**
 * Creates a new LowStockItemRow instance.
 * This method coordinates the main operation for this action.
 *
 * @param itemId item id
 * @param description description
 * @param availabilityPacks availability packs
 * @param stockLimitPacks stock limit packs
 */

    public LowStockItemRow(int itemId, String description, int availabilityPacks, int stockLimitPacks) {
        this.itemId = itemId;
        this.description = description;
        this.availabilityPacks = availabilityPacks;
        this.stockLimitPacks = stockLimitPacks;
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
 * Returns availability packs.
 *
 * @return result value
 */

    public int getAvailabilityPacks() {
        return availabilityPacks;
    }
/**
 * Returns stock limit packs.
 *
 * @return result value
 */

    public int getStockLimitPacks() {
        return stockLimitPacks;
    }
}
