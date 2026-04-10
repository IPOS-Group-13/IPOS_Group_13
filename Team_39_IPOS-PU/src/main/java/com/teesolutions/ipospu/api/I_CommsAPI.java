package com.teesolutions.ipospu.api;

public interface I_CommsAPI {
    
    boolean sendEmail(String recipientEmail, String subject, String body);
}