package com.berrybyte.ORD.DTO;

public class OrderDetails {

    private final int orderId;
    private final int merchantId;
    private final String iposAccountNumber;
    private final String accountHolder;
    private final String companyName;
    private final String address;
    private final String date;
    private final String status;
    private final double totalAmount;

    public OrderDetails(int orderId, int merchantId, String iposAccountNumber,
                        String accountHolder, String companyName, String address,
                        String date, String status, double totalAmount) {
        this.orderId = orderId;
        this.merchantId = merchantId;
        this.iposAccountNumber = iposAccountNumber;
        this.accountHolder = accountHolder;
        this.companyName = companyName;
        this.address = address;
        this.date = date;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public String getIposAccountNumber() {
        return iposAccountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}