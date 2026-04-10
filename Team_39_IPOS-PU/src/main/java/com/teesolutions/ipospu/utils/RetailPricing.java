package com.teesolutions.ipospu.utils;

import com.teesolutions.ipospu.repositories.AppConfigRepository;


public final class RetailPricing {

    public static final String KEY_RETAIL_MARKUP_PERCENT = "retail_markup_percent";
    public static final String KEY_VAT_RATE = "vat_rate";

    
    public static final double DEFAULT_RETAIL_MARKUP_PERCENT = 100.0;
    public static final double DEFAULT_VAT_RATE_PERCENT = 0.0;

    private final AppConfigRepository appConfigRepository;

    public RetailPricing() {
        this(new AppConfigRepository());
    }

    public RetailPricing(AppConfigRepository appConfigRepository) {
        this.appConfigRepository = appConfigRepository;
    }

    public double getRetailMarkupPercent() {
        return appConfigRepository.getDouble(KEY_RETAIL_MARKUP_PERCENT, DEFAULT_RETAIL_MARKUP_PERCENT);
    }

    public double getVatRatePercent() {
        return appConfigRepository.getDouble(KEY_VAT_RATE, DEFAULT_VAT_RATE_PERCENT);
    }

    
    public static double retailFromPackageCost(double packageCost, double markupPercent, double vatRatePercent) {
        if (packageCost < 0) {
            throw new IllegalArgumentException("packageCost must be >= 0");
        }
        double beforeVat = packageCost * (1.0 + markupPercent / 100.0);
        return beforeVat * (1.0 + vatRatePercent / 100.0);
    }

    
    public double retailUnitPriceFromPackageCost(double packageCost) {
        return retailFromPackageCost(packageCost, getRetailMarkupPercent(), getVatRatePercent());
    }
}
