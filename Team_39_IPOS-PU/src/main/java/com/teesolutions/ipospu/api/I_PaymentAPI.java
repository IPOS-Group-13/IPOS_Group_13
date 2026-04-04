package com.teesolutions.ipospu.api;

import com.teesolutions.ipospu.dto.PaymentRequest;
import com.teesolutions.ipospu.dto.PaymentResult;

public interface I_PaymentAPI {
    PaymentResult processPayment(PaymentRequest paymentRequest);
}