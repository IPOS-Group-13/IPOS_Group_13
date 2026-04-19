package com.berrybyte.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents overdue account alert row.
 */
public class OverdueAccountAlertRow {

    private final Integer merchantId;
    private final String companyName;
    private final String iposAccountNumber;
    private final LocalDate oldestDueDate;
    private final Integer daysOverdue;
    private final BigDecimal overdueAmount;
    private final String accountState;
    private final String alertLevel;
/**
 * Creates a new OverdueAccountAlertRow instance.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param companyName company name
 * @param iposAccountNumber ipos account number
 * @param oldestDueDate oldest due date
 * @param daysOverdue days overdue
 * @param overdueAmount overdue amount
 * @param accountState account state
 * @param alertLevel alert level
 */

    public OverdueAccountAlertRow(Integer merchantId,
                                  String companyName,
                                  String iposAccountNumber,
                                  LocalDate oldestDueDate,
                                  Integer daysOverdue,
                                  BigDecimal overdueAmount,
                                  String accountState,
                                  String alertLevel) {
        this.merchantId = merchantId;
        this.companyName = companyName;
        this.iposAccountNumber = iposAccountNumber;
        this.oldestDueDate = oldestDueDate;
        this.daysOverdue = daysOverdue;
        this.overdueAmount = overdueAmount;
        this.accountState = accountState;
        this.alertLevel = alertLevel;
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
 * Returns oldest due date.
 *
 * @return result value
 */

    public LocalDate getOldestDueDate() {
        return oldestDueDate;
    }
/**
 * Returns days overdue.
 *
 * @return result value
 */

    public Integer getDaysOverdue() {
        return daysOverdue;
    }
/**
 * Returns overdue amount.
 *
 * @return result value
 */

    public BigDecimal getOverdueAmount() {
        return overdueAmount;
    }
/**
 * Returns account state.
 *
 * @return result value
 */

    public String getAccountState() {
        return accountState;
    }
/**
 * Returns alert level.
 *
 * @return result value
 */

    public String getAlertLevel() {
        return alertLevel;
    }
}
