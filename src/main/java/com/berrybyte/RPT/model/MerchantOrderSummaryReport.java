package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents merchant order summary report.
 */
public class MerchantOrderSummaryReport {
    private final String title;
    private final int merchantId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDateTime generatedAt;
    private final List<MerchantOrderSummaryRow> rows;
    private final int totalOrders;
    private final BigDecimal totalOrderValue;
/**
 * Creates a new MerchantOrderSummaryReport instance.
 * This method coordinates the main operation for this action.
 *
 * @param title title
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @param generatedAt generated at
 * @param rows rows
 * @param totalOrders total orders
 * @param totalOrderValue total order value
 */

    public MerchantOrderSummaryReport(String title,
                                      int merchantId,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      LocalDateTime generatedAt,
                                      List<MerchantOrderSummaryRow> rows,
                                      int totalOrders,
                                      BigDecimal totalOrderValue) {
        this.title = title;
        this.merchantId = merchantId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedAt = generatedAt;
        this.rows = rows;
        this.totalOrders = totalOrders;
        this.totalOrderValue = totalOrderValue;
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

    public int getMerchantId() {
        return merchantId;
    }
/**
 * Returns start date.
 *
 * @return result value
 */

    public LocalDate getStartDate() {
        return startDate;
    }
/**
 * Returns end date.
 *
 * @return result value
 */

    public LocalDate getEndDate() {
        return endDate;
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

    public List<MerchantOrderSummaryRow> getRows() {
        return rows;
    }
/**
 * Returns total orders.
 *
 * @return result value
 */

    public int getTotalOrders() {
        return totalOrders;
    }
/**
 * Returns total order value.
 *
 * @return result value
 */

    public BigDecimal getTotalOrderValue() {
        return totalOrderValue;
    }

/**
 * Performs to string.
 *
 * @return result value
 */
    @Override
    public String toString() {
        return "MerchantOrderSummaryReport{" +
                "title='" + title + '\'' +
                ", merchantId=" + merchantId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", generatedAt=" + generatedAt +
                ", rows=" + rows +
                ", totalOrders=" + totalOrders +
                ", totalOrderValue=" + totalOrderValue +
                '}';
    }
}
