package com.berrybyte.dashboard;

public class MerchantMenuRow {

    private final int merchantId;
    private final int userId;
    private final String merchantName;
    private final String companyName;
    private final String iposAccountNumber;
    private final String creditLimit;
    private final String discountPlan;

    public MerchantMenuRow(int merchantId,
                           int userId,
                           String merchantName,
                           String companyName,
                           String iposAccountNumber,
                           String creditLimit,
                           String discountPlan) {
        this.merchantId = merchantId;
        this.userId = userId;
        this.merchantName = merchantName;
        this.companyName = companyName;
        this.iposAccountNumber = iposAccountNumber;
        this.creditLimit = creditLimit;
        this.discountPlan = discountPlan;
    }

    public int getMerchantId() {return merchantId;}

    public int getUserId() {return userId;}

    public String getMerchantName() {return merchantName;}

    public String getCompanyName() {return companyName;}

    public String getIposAccountNumber() {return iposAccountNumber;}

    public String getCreditLimit() {return creditLimit;}

    public String getDiscountPlan() {return discountPlan;}
}
