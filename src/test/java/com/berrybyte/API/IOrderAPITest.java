package com.berrybyte.API;

import com.berrybyte.ORD.helpers.OrderDetails;
import com.berrybyte.ORD.services.SaOrderService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IOrderAPITest {

    private final IOrderAPI orderAPI = new SaOrderService();

    @Test
    void getOrderDetails_viaProvidedInterface_returnsCorrectOrder() throws Exception {

        System.out.println("Testing getOrderDetails()...");

        OrderDetails details = orderAPI.getOrderDetails(26);

        System.out.println("Check 1: order details not null...");
        assertNotNull(details);
        System.out.println("...success");

        System.out.println("Check 2: orderId is correct...");
        assertEquals(26, details.getOrderId());
        System.out.println("...success");

        System.out.println("Check 3: merchantId is correct...");
        assertEquals(16, details.getMerchantId());
        System.out.println("...success");
    }

    @Test
    void trackOrder_viaProvidedInterface_returnsCorrectStatus() throws Exception {

        System.out.println("Testing trackOrder()...");

        String status = orderAPI.trackOrder(26);

        System.out.println("Check 1: status is not null...");
        assertNotNull(status);
        System.out.println("...success");

        System.out.println("Check 2: status equals ACCEPTED...");
        assertEquals("ACCEPTED", status);
        System.out.println("...success");

    }
}