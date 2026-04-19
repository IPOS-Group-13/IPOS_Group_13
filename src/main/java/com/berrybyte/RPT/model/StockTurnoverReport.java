package com.berrybyte.RPT.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents stock turnover report.
 */
public class StockTurnoverReport {
    private final String title;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDateTime generatedAt;
    private final List<StockTurnoverRow> rows;
    private final int totalQuantitySold;
    private final BigDecimal totalSalesValue;
/**
 * Creates a new StockTurnoverReport instance.
 * This method coordinates the main operation for this action.
 *
 * @param title title
 * @param startDate start date
 * @param endDate end date
 * @param generatedAt generated at
 * @param rows rows
 * @param totalQuantitySold total quantity sold
 * @param totalSalesValue total sales value
 */

    public StockTurnoverReport(String title,
                               LocalDate startDate,
                               LocalDate endDate,
                               LocalDateTime generatedAt,
                               List<StockTurnoverRow> rows,
                               int totalQuantitySold,
                               BigDecimal totalSalesValue) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedAt = generatedAt;
        this.rows = rows;
        this.totalQuantitySold = totalQuantitySold;
        this.totalSalesValue = totalSalesValue;
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

    public List<StockTurnoverRow> getRows() {
        return rows;
    }
/**
 * Returns total quantity sold.
 *
 * @return result value
 */

    public int getTotalQuantitySold() {
        return totalQuantitySold;
    }
/**
 * Returns total sales value.
 *
 * @return result value
 */

    public BigDecimal getTotalSalesValue() {
        return totalSalesValue;
    }

/**
 * Performs to string.
 *
 * @return result value
 */
    @Override
    public String toString() {
        return "StockTurnoverReport{" +
                "title='" + title + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", generatedAt=" + generatedAt +
                ", rows=" + rows +
                ", totalQuantitySold=" + totalQuantitySold +
                ", totalSalesValue=" + totalSalesValue +
                '}';
    }
}
