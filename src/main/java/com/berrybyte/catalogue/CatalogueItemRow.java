package com.berrybyte.catalogue;

/**
 * Represents catalogue item row.
 */
public class CatalogueItemRow {
    private final int itemId;
    private final String description;
    private final String packageType;
    private final String unit;
    private final int unitsInPack;
    private final String packageCost;
    private final int availabilityPacks;
    private final int stockLimitPacks;
/**
 * Creates a new CatalogueItemRow instance.
 * This method coordinates the main operation for this action.
 *
 * @param itemId item id
 * @param description description
 * @param packageType package type
 * @param unit unit
 * @param unitsInPack units in pack
 * @param packageCost package cost
 * @param availabilityPacks availability packs
 * @param stockLimitPacks stock limit packs
 */

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
 * Returns unit.
 *
 * @return result value
 */

    public String getUnit() {
        return unit;
    }
/**
 * Returns units in pack.
 *
 * @return result value
 */

    public int getUnitsInPack() {
        return unitsInPack;
    }
/**
 * Returns package cost.
 *
 * @return result value
 */

    public String getPackageCost() {
        return packageCost;
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
