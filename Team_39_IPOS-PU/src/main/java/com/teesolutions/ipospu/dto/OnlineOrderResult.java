package com.teesolutions.ipospu.dto;

public class OnlineOrderResult {
    private final boolean accepted;
    private final String message;

    public OnlineOrderResult(boolean accepted, String message) {
        this.accepted = accepted;
        this.message = message;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getMessage() {
        return message;
    }
}
