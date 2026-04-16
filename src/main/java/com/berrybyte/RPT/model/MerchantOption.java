package com.berrybyte.RPT.model;

public class MerchantOption {
    private final int merchantId;
    private final String companyName;

    public MerchantOption(int merchantId, String companyName) {
        this.merchantId = merchantId;
        this.companyName = companyName;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public String getCompanyName() {
        return companyName;
    }

    @Override
    public String toString() {
        return companyName;
    }
}