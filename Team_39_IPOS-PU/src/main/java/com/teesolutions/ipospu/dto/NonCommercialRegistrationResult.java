package com.teesolutions.ipospu.dto;


public record NonCommercialRegistrationResult(
        String email,
        String temporaryPassword,
        OutboundEmailResult outboundEmail) {
}
