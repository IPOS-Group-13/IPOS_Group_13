package com.berrybyte.API;

import com.berrybyte.ORD.DTO.*;
import com.berrybyte.ORD.Status.AcceptOrderStatus;

import java.time.LocalDate;
import java.util.List;

public interface IOrderAPI {

    // SA "Review Incoming Orders" screen
    List<IncomingOrderRow> getOrdersForReview() throws Exception;
    List<IncomingOrderRow> searchOrdersForReview(String keyword) throws Exception;

    // SA order details screen
    OrderDetails getOrderDetails(int orderId) throws Exception;
    List<OrderLine> getOrderItems(int orderId) throws Exception;

    // SA accept order action
    AcceptOrderStatus acceptOrder(int orderId, int staffUserId) throws Exception;

    // Invoice lookup by order id
    InvoiceDetails getInvoiceByOrderId(int orderId) throws Exception;

    // Merchant order summary
    List<MerchantOrderSummary> getMerchantOrderSummary(int merchantId) throws Exception;
    List<MerchantOrderSummary> getMerchantOrderSummary(int merchantId,
                                                       LocalDate startDate,
                                                       LocalDate endDate) throws Exception;

    // Basic tracking
    String trackOrder(int orderId) throws Exception;
}