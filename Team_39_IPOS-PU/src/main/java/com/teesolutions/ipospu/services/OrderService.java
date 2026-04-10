package com.teesolutions.ipospu.services;

import com.teesolutions.ipospu.dto.CartLineDto;
import com.teesolutions.ipospu.dto.OnlineOrderRequest;
import com.teesolutions.ipospu.dto.OnlineOrderResult;
import com.teesolutions.ipospu.dto.OrderStatusDto;
import com.teesolutions.ipospu.dto.PaymentRequest;
import com.teesolutions.ipospu.dto.PaymentResult;
import com.teesolutions.ipospu.api.I_InventoryAPI;
import com.teesolutions.ipospu.config.InventoryApiFactory;
import com.teesolutions.ipospu.dto.StockReservationResult;
import com.teesolutions.ipospu.models.CartItem;
import com.teesolutions.ipospu.models.Order;
import com.teesolutions.ipospu.models.User;
import com.teesolutions.ipospu.repositories.OrderRepository;
import com.teesolutions.ipospu.repositories.PaymentRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Collections;
import java.util.Optional;

public class OrderService {
    private final I_InventoryAPI inventoryApiClient = InventoryApiFactory.create();
    private final PaymentService paymentService = new PaymentService();
    private final PaymentRepository paymentRepository = new PaymentRepository();
    private final OrderRepository orderRepository = new OrderRepository();
    private final CommsService commsService = new CommsService();
    private final AuthService authService = new AuthService();
    private final CampaignService campaignService = new CampaignService();

    public double calculateCartTotal(List<CartItem> items, User user) {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getLineTotal();
        }
        if (user != null && user.isEligibleForLoyaltyDiscount()) {
            total = total * 0.90;
        }
        return total;
    }

    public CheckoutResult checkout(
            User user,
            String customerEmail,
            List<CartItem> cartItems,
            String deliveryAddress,
            PaymentRequest paymentRequest
    ) {
        if (cartItems == null || cartItems.isEmpty()) {
            return CheckoutResult.fail("Your cart is empty");
        }
        String normalizedCustomerEmail;
        try {
            normalizedCustomerEmail = user != null
                    ? authService.requireValidEmail(user.getEmail())
                    : authService.requireValidEmail(customerEmail);
        } catch (IllegalArgumentException ex) {
            return CheckoutResult.fail(ex.getMessage());
        }
        if (deliveryAddress == null || deliveryAddress.isBlank()) {
            return CheckoutResult.fail("Please provide a delivery address");
        }
        if (paymentRequest == null) {
            return CheckoutResult.fail("Payment details are missing");
        }
        List<CartLineDto> lines = new ArrayList<>();
        Map<String, Double> unitPrice = new HashMap<>();
        Map<String, Double> discount = new HashMap<>();
        for (CartItem item : cartItems) {
            lines.add(new CartLineDto(item.getProductId(), item.getQuantity()));
            unitPrice.put(item.getProductId(), item.getUnitPrice());
            discount.put(item.getProductId(), item.getDiscountPercent());
        }


        StockReservationResult stockCheck = inventoryApiClient.reserveOrReject(lines);
        if (!stockCheck.isSuccess()) {
            return CheckoutResult.fail(
                    "Stock changed during checkout. Please review your cart.",
                    stockCheck.getUnavailableProductIds()
            );
        }

        double total = calculateCartTotal(cartItems, user);
        PaymentRequest actualPayment = new PaymentRequest(
                normalizedCustomerEmail,
                total,
                paymentRequest.getCardType(),
                paymentRequest.getFirst4(),
                paymentRequest.getLast4(),
                paymentRequest.getExpiryDate()
        );
        int purchaserUserId = user != null ? user.getUserId() : authService.ensureGuestCheckoutUser();

        PaymentResult paymentResult = paymentService.processPayment(actualPayment);
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        if (!paymentResult.isSuccess()) {
            paymentRepository.savePayment(null, actualPayment, paymentResult);
            return CheckoutResult.fail("Payment failed: " + paymentResult.getMessage());
        }

        OnlineOrderResult inventoryPropagation = inventoryApiClient.submitOnlineOrder(
                new OnlineOrderRequest(orderId, deliveryAddress, normalizedCustomerEmail, lines));
        if (!inventoryPropagation.isAccepted()) {
            paymentRepository.savePayment(null, actualPayment, paymentResult);
            return CheckoutResult.fail("Payment completed but stock propagation failed. Please contact support.");
        }

        String trackingCode = "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        orderRepository.createOrderWithItems(
                orderId,
                purchaserUserId,
                total,
                "RECEIVED",
                trackingCode,
                deliveryAddress,
                lines,
                unitPrice,
                discount
        );
        paymentRepository.savePayment(orderId, actualPayment, paymentResult);
        for (CartItem item : cartItems) {
            campaignService.recordItemPurchased(item.getProductId(), item.getQuantity());
        }
        if (user != null) {
            authService.incrementCompletedOrders(user.getUserId());
            user.setCompletedOrderCount(user.getCompletedOrderCount() + 1);
        }
        commsService.sendOrderConfirmation(
                normalizedCustomerEmail, orderId, "RECEIVED", trackingCode, user == null);

        return CheckoutResult.success(orderId, trackingCode, total);
    }

    public List<Order> getOrderHistory(int userId) {
        List<Order> list = orderRepository.findOrdersByUser(userId);
        if (InventoryApiFactory.isCaInventoryEnabled()) {
            for (Order o : list) {
                mergeCaStatusOntoOrder(o);
            }
        }
        return list;
    }

    public Optional<Order> findOrderByTracking(String customerEmail, String trackingCode) {
        String normalizedCustomerEmail = authService.requireValidEmail(customerEmail);
        if (trackingCode == null || trackingCode.isBlank()) {
            throw new IllegalArgumentException("Tracking code is required");
        }
        Optional<Order> found = orderRepository.findOrderByTracking(normalizedCustomerEmail, trackingCode.trim());
        found.ifPresent(this::mergeCaStatusOntoOrder);
        return found;
    }

    
    private void mergeCaStatusOntoOrder(Order order) {
        if (!InventoryApiFactory.isCaInventoryEnabled() || order == null) {
            return;
        }
        OrderStatusDto ca = inventoryApiClient.getOrderStatus(order.getOrderId());
        if (ca != null && ca.getStatus() != null && !"VOID".equalsIgnoreCase(ca.getStatus().trim())) {
            order.setStatus(ca.getStatus().trim());
        }
    }

    public static class CheckoutResult {
        private final boolean success;
        private final String message;
        private final String orderId;
        private final String trackingCode;
        private final double amount;
        private final List<String> unavailableProductIds;

        private CheckoutResult(
                boolean success,
                String message,
                String orderId,
                String trackingCode,
                double amount,
                List<String> unavailableProductIds
        ) {
            this.success = success;
            this.message = message;
            this.orderId = orderId;
            this.trackingCode = trackingCode;
            this.amount = amount;
            this.unavailableProductIds = unavailableProductIds == null
                    ? Collections.emptyList()
                    : Collections.unmodifiableList(unavailableProductIds);
        }

        public static CheckoutResult fail(String message) {
            return new CheckoutResult(false, message, null, null, 0, Collections.emptyList());
        }

        public static CheckoutResult fail(String message, List<String> unavailableProductIds) {
            return new CheckoutResult(false, message, null, null, 0, unavailableProductIds);
        }

        public static CheckoutResult success(String orderId, String trackingCode, double amount) {
            return new CheckoutResult(true, "Checkout successful", orderId, trackingCode, amount, Collections.emptyList());
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getTrackingCode() {
            return trackingCode;
        }

        public double getAmount() {
            return amount;
        }

        public List<String> getUnavailableProductIds() {
            return unavailableProductIds;
        }
    }
}
