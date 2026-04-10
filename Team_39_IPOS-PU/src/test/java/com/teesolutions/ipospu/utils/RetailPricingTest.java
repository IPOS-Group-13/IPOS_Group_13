package com.teesolutions.ipospu.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RetailPricingTest {

    @Test
    void sampleSheet_100PercentMarkup_zeroVat_doublesPackageCost() {
        assertEquals(0.20, RetailPricing.retailFromPackageCost(0.10, 100.0, 0.0), 1e-9);
        assertEquals(37.00, RetailPricing.retailFromPackageCost(18.50, 100.0, 0.0), 1e-9);
    }

    @Test
    void vatAppliedOnRetailAfterMarkup() {
        assertEquals(1.20 * 1.2, RetailPricing.retailFromPackageCost(1.0, 20.0, 20.0), 1e-9);
    }
}
