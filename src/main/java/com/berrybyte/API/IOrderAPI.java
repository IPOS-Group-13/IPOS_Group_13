package com.berrybyte.API;

import com.berrybyte.ORD.helpers.*;
import com.berrybyte.ORD.Status.AcceptOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Defines the contract for i order api.
 */
public interface IOrderAPI {
/**
 * Returns orders for review.
 *
 * @return result value
 * @throws Exception when the operation fails
 */

    List<IncomingOrderRow> getOrdersForReview() throws Exception;
/**
 * Executes the search orders for review workflow.
 * This method coordinates the main operation for this action.
 *
 * @param keyword keyword
 * @return result value
 * @throws Exception when the operation fails
 */

    List<IncomingOrderRow> searchOrdersForReview(String keyword) throws Exception;
/**
 * Performs get order details.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @return result value
 * @throws Exception when the operation fails
 */

    OrderDetails getOrderDetails(int orderId) throws Exception;
/**
 * Performs get order items.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @return result value
 * @throws Exception when the operation fails
 */

    List<OrderLine> getOrderItems(int orderId) throws Exception;
/**
 * Executes the accept order workflow.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @param staffUserId staff user id
 * @return result value
 * @throws Exception when the operation fails
 */

    AcceptOrderStatus acceptOrder(int orderId, int staffUserId) throws Exception;
/**
 * Performs get invoice by order id.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @return result value
 * @throws Exception when the operation fails
 */

    InvoiceDetails getInvoiceByOrderId(int orderId) throws Exception;
/**
 * Performs get merchant order summary.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @return result value
 * @throws Exception when the operation fails
 */

    List<MerchantOrderSummary> getMerchantOrderSummary(int merchantId) throws Exception;
/**
 * Performs get merchant order summary.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @return result value
 * @throws Exception when the operation fails
 */

    List<MerchantOrderSummary> getMerchantOrderSummary(int merchantId,
                                                       LocalDate startDate,
                                                       LocalDate endDate) throws Exception;
/**
 * Returns orders summary.
 *
 * @return result value
 * @throws Exception when the operation fails
 */

    List<OrderSummaryRow> getOrdersSummary() throws Exception;
/**
 * Executes the search orders summary workflow.
 * This method coordinates the main operation for this action.
 *
 * @param keyword keyword
 * @return result value
 * @throws Exception when the operation fails
 */

    List<OrderSummaryRow> searchOrdersSummary(String keyword) throws Exception;
/**
 * Executes the record payment workflow.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @param paymentAmount payment amount
 * @param paymentMethod payment method
 * @param recordedByUserId recorded by user id
 * @throws Exception when the operation fails
 */

    void recordPayment(int orderId, BigDecimal paymentAmount, String paymentMethod, int recordedByUserId) throws Exception;
/**
 * Executes the update dispatch details workflow.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @param courierName courier name
 * @param courierRef courier ref
 * @param dispatchedDateTime dispatched date time
 * @param expectedDeliveryDateTime expected delivery date time
 * @param status status
 * @return result value
 * @throws Exception when the operation fails
 */

    boolean updateDispatchDetails(int orderId,
                                  String courierName,
                                  String courierRef,
                                  LocalDateTime dispatchedDateTime,
                                  LocalDateTime expectedDeliveryDateTime,
                                  String status) throws Exception;
/**
 * Performs mark order as delivered.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @param deliveredDateTime delivered date time
 * @return result value
 * @throws Exception when the operation fails
 */

    boolean markOrderAsDelivered(int orderId, LocalDateTime deliveredDateTime) throws Exception;
/**
 * Executes the track order workflow.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @return result value
 * @throws Exception when the operation fails
 */

    String trackOrder(int orderId) throws Exception;
}
