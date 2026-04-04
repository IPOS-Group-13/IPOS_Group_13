package com.teesolutions.ipospu.dto;

public class PaymentRequest {
    private final String payeeDetails;
    private final double amount;
    private final String cardType;
    private final String first4;
    private final String last4;
    private final String expiryDate;

    public PaymentRequest(String payeeDetails, double amount, String cardType, String first4, String last4, String expiryDate) {
        this.payeeDetails = payeeDetails;
        this.amount = amount;
        this.cardType = cardType;
        this.first4 = first4;
        this.last4 = last4;
        this.expiryDate = expiryDate;
    }

    public String getPayeeDetails() {
        return payeeDetails;
    }

    public double getAmount() {
        return amount;
    }

    public String getCardType() {
        return cardType;
    }

    public String getFirst4() {
        return first4;
    }

    public String getLast4() {
        return last4;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}
