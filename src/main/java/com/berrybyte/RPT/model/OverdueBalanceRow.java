package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OverdueBalanceRow {
    private final int merchantId;
    private final String companyName;
    private final String iposAccountNumber;
    private final String accountStatus;
    private final BigDecimal creditLimit;
    private final BigDecimal accountOutstandingBalance;
    private final LocalDate oldestDueDate;
    private final BigDecimal totalOverdueAmount;
    private final int overdueInvoiceCount;

    public OverdueBalanceRow(int merchantId,
                             String companyName,
                             String iposAccountNumber,
                             String accountStatus,
                             BigDecimal creditLimit,
                             BigDecimal accountOutstandingBalance,
                             LocalDate oldestDueDate,
                             BigDecimal totalOverdueAmount,
                             int overdueInvoiceCount) {
        this.merchantId = merchantId;
        this.companyName = companyName;
        this.iposAccountNumber = iposAccountNumber;
        this.accountStatus = accountStatus;
        this.creditLimit = creditLimit;
        this.accountOutstandingBalance = accountOutstandingBalance;
        this.oldestDueDate = oldestDueDate;
        this.totalOverdueAmount = totalOverdueAmount;
        this.overdueInvoiceCount = overdueInvoiceCount;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getIposAccountNumber() {
        return iposAccountNumber;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public BigDecimal getAccountOutstandingBalance() {
        return accountOutstandingBalance;
    }

    public LocalDate getOldestDueDate() {
        return oldestDueDate;
    }

    public BigDecimal getTotalOverdueAmount() {
        return totalOverdueAmount;
    }

    public int getOverdueInvoiceCount() {
        return overdueInvoiceCount;
    }
}