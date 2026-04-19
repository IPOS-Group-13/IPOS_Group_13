package com.berrybyte.ORD.services;

import com.berrybyte.ORD.helpers.IncomingOrderRow;
import com.berrybyte.ORD.helpers.OrderDetails;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SaOrderServiceTest {

    private final SaOrderService service = new SaOrderService();

    @Test
    void getOrdersForReview_returnsList() throws Exception {
        List<IncomingOrderRow> orders = service.getOrdersForReview();

        assertNotNull(orders);
        assertFalse(orders.isEmpty(), "Orders list should not be empty");
    }

    @Test
    void searchOrdersForReview_returnsCorrectOrder() throws Exception {
        List<IncomingOrderRow> orders = service.searchOrdersForReview("26");

        assertNotNull(orders);
        assertFalse(orders.isEmpty());

        boolean found = orders.stream()
                .anyMatch(order -> order.getOrderId() == 26);

        assertTrue(found, "Order 26 should be returned in search results");
    }

    @Test
    void getOrderDetails_returnsCorrectOrder() throws Exception {
        OrderDetails details = service.getOrderDetails(26);

        assertNotNull(details, "Order details should not be null");
        assertEquals(26, details.getOrderId());
        assertEquals(16, details.getMerchantId());
    }

    @Test
    void trackOrder_returnsCorrectStatus() throws Exception {
        String status = service.trackOrder(26);

        assertNotNull(status, "Status should not be null");
        assertEquals("ACCEPTED", status);
    }

    @Test
    void getOrderItems_returnsItemsForOrder() throws Exception {
        var items = service.getOrderItems(26);

        assertNotNull(items, "Order items should not be null");
        assertFalse(items.isEmpty(), "Order 26 should have at least one item");

        boolean found = items.stream()
                .anyMatch(item -> item.getItemId() == 10000001);

        assertTrue(found, "Order 26 should contain item 10000001");
    }
}