package com.berrybyte.ORD;

import com.berrybyte.ORD.helpers.OrderRequestItem;
import com.berrybyte.ORD.helpers.OrderLine;
import com.berrybyte.ORD.helpers.IncomingOrderRow;
import com.berrybyte.ORD.Status.AcceptOrderStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderHelpersTest {

    @Test
    void testOrderItemData() {
        OrderRequestItem item = new OrderRequestItem(101, 3);
        assertEquals(101, item.getItemId());
        assertEquals(3, item.getQuantity());
    }

    @Test
    void testOrderLineData() {
        OrderLine line = new OrderLine(1, "Paracetamol", "box", 20, 0.10, 10, 1.00);
        assertEquals(1, line.getItemId());
        assertEquals("Paracetamol", line.getDescription());
        assertEquals(10, line.getQuantity());
        assertEquals(1.00, line.getLineTotal());
    }

    @Test
    void testIncomingOrderData() {
        IncomingOrderRow row = new IncomingOrderRow(1, "Pharmats", "15/02/2025", 500.0, "NEW");
        assertEquals(1, row.getOrderId());
        assertEquals("Pharmats", row.getAccountHolder());
        assertEquals("NEW", row.getStatus());
    }

    @Test
    void testSuccessStatus() {
        assertEquals(AcceptOrderStatus.SUCCESS, AcceptOrderStatus.valueOf("SUCCESS"));
    }

    @Test
    void testMissingOrder() {
        assertEquals(AcceptOrderStatus.ORDER_NOT_FOUND, AcceptOrderStatus.valueOf("ORDER_NOT_FOUND"));
    }

    @Test
    void testSuspendedMerchant() {
        assertEquals(AcceptOrderStatus.MERCHANT_SUSPENDED, AcceptOrderStatus.valueOf("MERCHANT_SUSPENDED"));
    }

    @Test
    void testLowStockStatus() {
        assertEquals(AcceptOrderStatus.INSUFFICIENT_STOCK, AcceptOrderStatus.valueOf("INSUFFICIENT_STOCK"));
    }
}