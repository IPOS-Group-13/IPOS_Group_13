package com.teesolutions.ipospu.services;

import com.teesolutions.ipospu.api.I_PaymentAPI;
import com.teesolutions.ipospu.dto.PaymentRequest;
import com.teesolutions.ipospu.dto.PaymentResult;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.regex.Pattern;

public class PaymentService implements I_PaymentAPI {
    private static final Pattern FOUR_DIGITS = Pattern.compile("\\d{4}");
    private static final Pattern CARD_TYPE = Pattern.compile("[A-Za-z][A-Za-z\\s-]{1,31}");
    private static final DateTimeFormatter EXPIRY_FORMAT = DateTimeFormatter.ofPattern("MM/yy");

    @Override
    public PaymentResult processPayment(PaymentRequest paymentRequest) {
        if (paymentRequest == null) {
            return new PaymentResult(false, null, "Payment details are missing");
        }
        if (paymentRequest.getAmount() <= 0) {
            return new PaymentResult(false, null, "Invalid payment amount");
        }
        if (isBlank(paymentRequest.getCardType()) ||
                !CARD_TYPE.matcher(paymentRequest.getCardType().trim()).matches()) {
            return new PaymentResult(false, null, "Card type is invalid");
        }
        if (!isFourDigits(paymentRequest.getFirst4()) || !isFourDigits(paymentRequest.getLast4())) {
            return new PaymentResult(false, null, "Card details are invalid");
        }
        PaymentResult expiryValidation = validateExpiry(paymentRequest.getExpiryDate());
        if (expiryValidation != null) {
            return expiryValidation;
        }
        String txId = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(true, txId, "Payment accepted");
    }

    private PaymentResult validateExpiry(String expiryDate) {
        if (isBlank(expiryDate)) {
            return new PaymentResult(false, null, "Card expiry date is required");
        }
        try {
            YearMonth expiry = YearMonth.parse(expiryDate.trim(), EXPIRY_FORMAT);
            if (expiry.isBefore(YearMonth.now())) {
                return new PaymentResult(false, null, "Card expiry date has passed");
            }
            return null;
        } catch (DateTimeParseException ex) {
            return new PaymentResult(false, null, "Expiry date must use MM/YY");
        }
    }

    private boolean isFourDigits(String value) {
        return value != null && FOUR_DIGITS.matcher(value.trim()).matches();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
