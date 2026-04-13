package com.berrybyte.ORD.helpers;

public class OrderRequestItem {

    private final int itemId;
    private final int quantity;

    public OrderRequestItem(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }
}