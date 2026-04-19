package com.berrybyte.ORD.helpers;

/**
 * Represents order request item.
 */
public class OrderRequestItem {

    private final int itemId;
    private final int quantity;
/**
 * Creates a new OrderRequestItem instance.
 *
 * @param itemId item id
 * @param quantity quantity
 */

    public OrderRequestItem(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
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
 * Returns quantity.
 *
 * @return result value
 */

    public int getQuantity() {
        return quantity;
    }
}
