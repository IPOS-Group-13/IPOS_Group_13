package com.berrybyte.ORD.Status;

/**
 * Enumerates values for accept order status.
 */
public enum AcceptOrderStatus {
    SUCCESS,
    ORDER_NOT_FOUND,
    ORDER_ALREADY_ACCEPTED,
    MERCHANT_SUSPENDED,
    MERCHANT_IN_DEFAULT,
    INSUFFICIENT_STOCK,
    INVOICE_GENERATION_FAILED,
    ERROR
}
