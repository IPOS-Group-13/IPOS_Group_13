package com.berrybyte.ORD.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents payment request row.
 */
public class PaymentRequestRow {

    private final long orderId;
    private final String merchantEmail;
    private final String amount;
    private final String paymentType;
    private final String status;
/**
 * Creates a new PaymentRequestRow instance.
 * This method coordinates the main operation for this action.
 *
 * @param orderId order id
 * @param merchantEmail merchant email
 * @param amount amount
 * @param paymentType payment type
 * @param status status
 */

    public PaymentRequestRow(long orderId,
                             String merchantEmail,
                             BigDecimal amount,
                             String paymentType,
                             String status) {
        this.orderId = orderId;
        this.merchantEmail = merchantEmail == null ? "" : merchantEmail;
        this.amount = normalizeAmount(amount);
        this.paymentType = paymentType == null ? "" : paymentType;
        this.status = status == null ? "" : status;
    }
/**
 * Returns order id.
 *
 * @return result value
 */

    public long getOrderId() {
        return orderId;
    }
/**
 * Returns merchant email.
 *
 * @return result value
 */

    public String getMerchantEmail() {
        return merchantEmail;
    }
/**
 * Returns amount.
 *
 * @return result value
 */

    public String getAmount() {
        return amount;
    }
/**
 * Returns payment type.
 *
 * @return result value
 */

    public String getPaymentType() {
        return paymentType;
    }
/**
 * Returns status.
 *
 * @return result value
 */

    public String getStatus() {
        return status;
    }
/**
 * Performs normalize amount.
 *
 * @param value value
 * @return result value
 */

    private String normalizeAmount(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
