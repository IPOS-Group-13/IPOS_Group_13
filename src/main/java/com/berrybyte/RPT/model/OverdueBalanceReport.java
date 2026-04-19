package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents overdue balance report.
 */
public class OverdueBalanceReport {
    private final String title;
    private final Integer merchantId;
    private final String merchantName;
    private final LocalDateTime generatedAt;
    private final List<OverdueBalanceRow> rows;
    private final int totalAccounts;
    private final BigDecimal totalOverdueAmount;
/**
 * Creates a new OverdueBalanceReport instance.
 * This method coordinates the main operation for this action.
 *
 * @param title title
 * @param merchantId merchant id
 * @param merchantName merchant name
 * @param generatedAt generated at
 * @param rows rows
 * @param totalAccounts total accounts
 * @param totalOverdueAmount total overdue amount
 */

    public OverdueBalanceReport(String title,
                                Integer merchantId,
                                String merchantName,
                                LocalDateTime generatedAt,
                                List<OverdueBalanceRow> rows,
                                int totalAccounts,
                                BigDecimal totalOverdueAmount) {
        this.title = title;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.generatedAt = generatedAt;
        this.rows = rows;
        this.totalAccounts = totalAccounts;
        this.totalOverdueAmount = totalOverdueAmount;
    }
/**
 * Returns title.
 *
 * @return result value
 */

    public String getTitle() {
        return title;
    }
/**
 * Returns merchant id.
 *
 * @return result value
 */

    public Integer getMerchantId() {
        return merchantId;
    }
/**
 * Returns merchant name.
 *
 * @return result value
 */

    public String getMerchantName() {
        return merchantName;
    }
/**
 * Returns generated at.
 *
 * @return result value
 */

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
/**
 * Returns rows.
 *
 * @return result value
 */

    public List<OverdueBalanceRow> getRows() {
        return rows;
    }
/**
 * Returns total accounts.
 *
 * @return result value
 */

    public int getTotalAccounts() {
        return totalAccounts;
    }
/**
 * Returns total overdue amount.
 *
 * @return result value
 */

    public BigDecimal getTotalOverdueAmount() {
        return totalOverdueAmount;
    }
}
