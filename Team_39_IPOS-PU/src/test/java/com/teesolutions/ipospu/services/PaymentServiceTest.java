package com.teesolutions.ipospu.services;

import com.teesolutions.ipospu.dto.PaymentRequest;
import com.teesolutions.ipospu.dto.PaymentResult;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentServiceTest {
    @Test
    void acceptsValidPaymentRequest() {
        PaymentService service = new PaymentService();
        PaymentResult result = service.processPayment(new PaymentRequest(
                "dimitarprem@gmail.com",
                25.50,
                "DEBIT",
                "1234",
                "5678",
                futureExpiry()
        ));
        assertTrue(result.isSuccess());
    }

    @Test
    void rejectsInvalidCardFragments() {
        PaymentService service = new PaymentService();
        PaymentResult result = service.processPayment(new PaymentRequest(
                "dimitarprem@gmail.com",
                25.50,
                "DEBIT",
                "123",
                "567",
                futureExpiry()
        ));
        assertFalse(result.isSuccess());
    }

    @Test
    void rejectsNonPositiveAmount() {
        PaymentService service = new PaymentService();
        PaymentResult result = service.processPayment(new PaymentRequest(
                "dimitarprem@gmail.com",
                0,
                "DEBIT",
                "1234",
                "5678",
                futureExpiry()
        ));
        assertFalse(result.isSuccess());
        assertEquals("Invalid payment amount", result.getMessage());
    }

    @Test
    void rejectsNonNumericCardFragments() {
        PaymentService service = new PaymentService();
        PaymentResult result = service.processPayment(new PaymentRequest(
                "dimitarprem@gmail.com",
                25.50,
                "DEBIT",
                "12A4",
                "56B8",
                futureExpiry()
        ));
        assertFalse(result.isSuccess());
        assertEquals("Card details are invalid", result.getMessage());
    }

    @Test
    void rejectsMissingCardType() {
        PaymentService service = new PaymentService();
        PaymentResult result = service.processPayment(new PaymentRequest(
                "dimitarprem@gmail.com",
                25.50,
                " ",
                "1234",
                "5678",
                futureExpiry()
        ));
        assertFalse(result.isSuccess());
        assertEquals("Card type is invalid", result.getMessage());
    }

    @Test
    void rejectsExpiredCard() {
        PaymentService service = new PaymentService();
        PaymentResult result = service.processPayment(new PaymentRequest(
                "dimitarprem@gmail.com",
                25.50,
                "DEBIT",
                "1234",
                "5678",
                expiredExpiry()
        ));
        assertFalse(result.isSuccess());
        assertEquals("Card expiry date has passed", result.getMessage());
    }

    @Test
    void rejectsMalformedExpiry() {
        PaymentService service = new PaymentService();
        PaymentResult result = service.processPayment(new PaymentRequest(
                "dimitarprem@gmail.com",
                25.50,
                "DEBIT",
                "1234",
                "5678",
                "2029-10"
        ));
        assertFalse(result.isSuccess());
        assertEquals("Expiry date must use MM/YY", result.getMessage());
    }

    private String futureExpiry() {
        return YearMonth.now().plusMonths(6).format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    private String expiredExpiry() {
        return YearMonth.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MM/yy"));
    }
}
