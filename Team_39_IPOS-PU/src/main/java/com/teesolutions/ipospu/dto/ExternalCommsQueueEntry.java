package com.teesolutions.ipospu.dto;


public record ExternalCommsQueueEntry(
        int id,
        String recipientEmail,
        String subject,
        String body,
        String purpose,
        String sourceSystem,
        String referenceKey
) {
}
