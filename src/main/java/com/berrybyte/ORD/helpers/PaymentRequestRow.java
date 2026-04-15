package com.berrybyte.ORD.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PaymentRequestRow {

    private final long orderId;
    private final String merchantEmail;
    private final String amount;
    private final String paymentType;
    private final String status;

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

    public long getOrderId() {
        return orderId;
    }

    public String getMerchantEmail() {
        return merchantEmail;
    }

    public String getAmount() {
        return amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getStatus() {
        return status;
    }

    private String normalizeAmount(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
