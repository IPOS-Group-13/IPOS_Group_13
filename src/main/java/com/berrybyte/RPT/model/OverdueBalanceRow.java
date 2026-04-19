package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents overdue balance row.
 */
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
/**
 * Creates a new OverdueBalanceRow instance.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param companyName company name
 * @param iposAccountNumber ipos account number
 * @param accountStatus account status
 * @param creditLimit credit limit
 * @param accountOutstandingBalance account outstanding balance
 * @param oldestDueDate oldest due date
 * @param totalOverdueAmount total overdue amount
 * @param overdueInvoiceCount overdue invoice count
 */

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
 * Returns ipos account number.
 *
 * @return result value
 */

    public String getIposAccountNumber() {
        return iposAccountNumber;
    }
/**
 * Returns account status.
 *
 * @return result value
 */

    public String getAccountStatus() {
        return accountStatus;
    }
/**
 * Returns credit limit.
 *
 * @return result value
 */

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }
/**
 * Returns account outstanding balance.
 *
 * @return result value
 */

    public BigDecimal getAccountOutstandingBalance() {
        return accountOutstandingBalance;
    }
/**
 * Returns oldest due date.
 *
 * @return result value
 */

    public LocalDate getOldestDueDate() {
        return oldestDueDate;
    }
/**
 * Returns total overdue amount.
 *
 * @return result value
 */

    public BigDecimal getTotalOverdueAmount() {
        return totalOverdueAmount;
    }
/**
 * Returns overdue invoice count.
 *
 * @return result value
 */

    public int getOverdueInvoiceCount() {
        return overdueInvoiceCount;
    }
}
