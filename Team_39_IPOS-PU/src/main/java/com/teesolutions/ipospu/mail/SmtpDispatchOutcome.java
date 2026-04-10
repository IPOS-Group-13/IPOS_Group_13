package com.teesolutions.ipospu.mail;


public record SmtpDispatchOutcome(Type type, String hint) {

    public enum Type {
        
        DISABLED,
        
        SKIPPED_BAD_CONFIG,
        
        SUCCESS,
        
        FAILED
    }

    public static SmtpDispatchOutcome disabled() {
        return new SmtpDispatchOutcome(Type.DISABLED, null);
    }

    public static SmtpDispatchOutcome skippedBadConfig(String hint) {
        return new SmtpDispatchOutcome(Type.SKIPPED_BAD_CONFIG, hint);
    }

    public static SmtpDispatchOutcome success() {
        return new SmtpDispatchOutcome(Type.SUCCESS, null);
    }

    public static SmtpDispatchOutcome failed(String hint) {
        return new SmtpDispatchOutcome(Type.FAILED, hint);
    }
}
