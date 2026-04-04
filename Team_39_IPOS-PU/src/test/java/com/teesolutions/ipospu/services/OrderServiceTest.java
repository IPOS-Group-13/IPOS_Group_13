package com.teesolutions.ipospu.services;

import com.teesolutions.ipospu.dto.PaymentRequest;
import com.teesolutions.ipospu.models.CartItem;
import com.teesolutions.ipospu.models.User;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class OrderServiceTest {
    @Test
    void calculateCartTotalAppliesLoyaltyDiscountOnTenthOrder() {
        OrderService orderService = new OrderService();
        User user = new User(1, "cool@example.com", "NON_COMMERCIAL", false, 9);
        List<CartItem> items = List.of(
                new CartItem("10000001", "Paracetamol", 10.0, 2, 0),
                new CartItem("10000002", "Aspirin", 5.0, 1, 0)
        );

        double total = orderService.calculateCartTotal(items, user);

        assertEquals(22.5, total, 0.0001);
    }

    @Test
    void checkoutFailsWhenGuestEmailIsMissing() {
        OrderService orderService = new OrderService();
        List<CartItem> items = List.of(new CartItem("10000001", "Paracetamol", 10.0, 1, 0));
        PaymentRequest request = new PaymentRequest("guest", 0, "DEBIT", "1234", "5678", futureExpiry());

        OrderService.CheckoutResult result = orderService.checkout(null, "   ", items, "Address", request);

        assertFalse(result.isSuccess());
        assertEquals("Please provide a valid email address", result.getMessage());
    }

    @Test
    void checkoutFailsWhenCartIsEmpty() {
        OrderService orderService = new OrderService();
        User user = new User(1, "cool@example.com", "NON_COMMERCIAL", false, 0);
        PaymentRequest request = new PaymentRequest("member", 0, "DEBIT", "1234", "5678", futureExpiry());

        OrderService.CheckoutResult result = orderService.checkout(user, user.getEmail(), List.of(), "Address", request);

        assertFalse(result.isSuccess());
        assertEquals("Your cart is empty", result.getMessage());
    }

    @Test
    void checkoutFailsWhenDeliveryAddressIsBlank() {
        OrderService orderService = new OrderService();
        User user = new User(1, "cool@example.com", "NON_COMMERCIAL", false, 0);
        List<CartItem> items = List.of(new CartItem("10000001", "Paracetamol", 10.0, 1, 0));
        PaymentRequest request = new PaymentRequest("member", 0, "DEBIT", "1234", "5678", futureExpiry());

        OrderService.CheckoutResult result = orderService.checkout(user, user.getEmail(), items, "   ", request);

        assertFalse(result.isSuccess());
        assertEquals("Please provide a delivery address", result.getMessage());
    }

    private String futureExpiry() {
        return YearMonth.now().plusMonths(6).format(DateTimeFormatter.ofPattern("MM/yy"));
    }
}
