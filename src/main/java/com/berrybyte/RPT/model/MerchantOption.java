package com.berrybyte.RPT.model;

/**
 * Represents merchant option.
 */
public class MerchantOption {
    private final int merchantId;
    private final String companyName;
/**
 * Creates a new MerchantOption instance.
 *
 * @param merchantId merchant id
 * @param companyName company name
 */

    public MerchantOption(int merchantId, String companyName) {
        this.merchantId = merchantId;
        this.companyName = companyName;
    }
/**
 * Returns merchant id.
 *
 * @return result value
 */

    public int getMerchantId() {
        return merchantId;
    }
/**
 * Returns company name.
 *
 * @return result value
 */

    public String getCompanyName() {
        return companyName;
    }

/**
 * Performs to string.
 *
 * @return result value
 */
    @Override
    public String toString() {
        return companyName;
    }
}
