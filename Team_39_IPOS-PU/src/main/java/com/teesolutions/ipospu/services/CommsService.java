package com.teesolutions.ipospu.services;

import com.teesolutions.ipospu.api.I_CommsAPI;
import com.teesolutions.ipospu.repositories.CommsRepository;

public class CommsService implements I_CommsAPI {
    private final CommsRepository commsRepository = new CommsRepository();

    @Override
    public boolean sendEmail(String recipientEmail, String subject, String body) {
        return commsRepository.saveOutboundEmail(recipientEmail, subject, body, "GENERIC");
    }

    public boolean sendRegistrationEmail(String recipientEmail, String generatedPassword) {
        String subject = "IPOS-PU Registration Credentials";
        String body = "Welcome to IPOS-PU.\n\nUsername: " + recipientEmail +
                "\nTemporary Password: " + generatedPassword +
                "\nPlease change your password at first login.";
        return commsRepository.saveOutboundEmail(recipientEmail, subject, body, "REGISTRATION");
    }

    public boolean sendOrderConfirmation(String recipientEmail, String orderId, String status, String trackingCode) {
        String subject = "Order Confirmation - " + orderId;
        String body = "Your order " + orderId + " has been received.\nCurrent status: " + status +
                "\n\nTracking code: " + trackingCode +
                "\nKeep this code. You can track your order in IPOS-PU under Orders / Tracking using this email and the tracking code." +
                "\n\nNote: In this demo, outbound mail is simulated. The same message is stored in the database table email_outbox " +
                "(check purpose ORDER_CONFIRMATION). A real deployment would send it via SMTP or a provider." +
                "\n\nStatus lifecycle: RECEIVED -> DISPATCHED -> DELIVERED";
        return commsRepository.saveOutboundEmail(recipientEmail, subject, body, "ORDER_CONFIRMATION");
    }
}
