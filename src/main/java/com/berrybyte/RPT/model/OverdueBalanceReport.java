package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OverdueBalanceReport {
    private final String title;
    private final Integer merchantId;
    private final String merchantName;
    private final LocalDateTime generatedAt;
    private final List<OverdueBalanceRow> rows;
    private final int totalAccounts;
    private final BigDecimal totalOverdueAmount;

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

    public String getTitle() {
        return title;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public List<OverdueBalanceRow> getRows() {
        return rows;
    }

    public int getTotalAccounts() {
        return totalAccounts;
    }

    public BigDecimal getTotalOverdueAmount() {
        return totalOverdueAmount;
    }
}