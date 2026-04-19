package com.berrybyte.RPT.model;

/**
 * Represents low stock item.
 */
public class LowStockItem {
    private final int itemId;
    private final String description;
    private final int availabilityPacks;
    private final int stockLimitPacks;
/**
 * Creates a new LowStockItem instance.
 * This method coordinates the main operation for this action.
 *
 * @param itemId item id
 * @param description description
 * @param availabilityPacks availability packs
 * @param stockLimitPacks stock limit packs
 */

    public LowStockItem(int itemId, String description, int availabilityPacks, int stockLimitPacks) {
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

/**
 * Performs to string.
 *
 * @return result value
 */
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
