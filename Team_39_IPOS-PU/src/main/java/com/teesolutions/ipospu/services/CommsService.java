package com.teesolutions.ipospu.services;

import com.teesolutions.ipospu.api.I_CommsAPI;
import com.teesolutions.ipospu.dto.OutboundEmailResult;
import com.teesolutions.ipospu.repositories.CommsRepository;

public class CommsService implements I_CommsAPI {
    private final CommsRepository commsRepository = new CommsRepository();

    @Override
    public boolean sendEmail(String recipientEmail, String subject, String body) {
        return commsRepository.saveOutboundEmail(recipientEmail, subject, body, "GENERIC").insertedIntoOutbox();
    }

    public OutboundEmailResult sendRegistrationEmail(String recipientEmail, String generatedPassword) {
        String subject = "Welcome to IPOS-PU — your login details";
        String body = String.format(
                "Hello, %s%n%n"
                        + "Welcome to IPOS_PU!%n%n"
                        + "Below are your log-in details, including your first time password which should be changed "
                        + "during first log-in.%n%n"
                        + "Username: %s%n"
                        + "Temporary Password: %s%n%n"
                        + "Thank you for signing up!%n%n"
                        + "Kind regards,%n"
                        + "IPOS-PU Team",
                recipientEmail,
                recipientEmail,
                generatedPassword);
        return commsRepository.saveOutboundEmail(recipientEmail, subject, body, "REGISTRATION");
    }

    
    public boolean sendOrderConfirmation(
            String recipientEmail,
            String orderId,
            String status,
            String trackingCode,
            boolean guestCheckout) {
        String subject = "Your IPOS-PU order — " + orderId;
        String intro = guestCheckout
                ? "Thank you for your order as a guest."
                : "Thank you for your order.";
        String body = String.format(
                "Hello, %s%n%n"
                        + "%s%n%n"
                        + "Order reference: %s%n"
                        + "Current status: %s%n"
                        + "Tracking code: %s%n%n"
                        + "Please keep your tracking code safe. You can track this order in IPOS-PU using this email "
                        + "address and your tracking code under Orders / Tracking.%n%n"
                        + "Kind regards,%n"
                        + "IPOS-PU Team",
                recipientEmail,
                intro,
                orderId,
                status,
                trackingCode);
        return commsRepository.saveOutboundEmail(recipientEmail, subject, body, "ORDER_CONFIRMATION").insertedIntoOutbox();
    }
}
