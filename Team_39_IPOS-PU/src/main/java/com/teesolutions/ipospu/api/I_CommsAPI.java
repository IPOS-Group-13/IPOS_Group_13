package com.teesolutions.ipospu.api;

public interface I_CommsAPI {
    /**
     * A generic method to send emails for registrations, orders, etc.
     */
    boolean sendEmail(String recipientEmail, String subject, String body);
}