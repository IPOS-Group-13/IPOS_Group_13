package com.berrybyte.RPT.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents low stock report.
 */
public class LowStockReport {
    private final String title;
    private final LocalDateTime generatedAt;
    private final List<LowStockItem> items;
/**
 * Creates a new LowStockReport instance.
 * This method coordinates the main operation for this action.
 *
 * @param title title
 * @param generatedAt generated at
 * @param items items
 */

    public LowStockReport(String title, LocalDateTime generatedAt, List<LowStockItem> items) {
        this.title = title;
        this.generatedAt = generatedAt;
        this.items = items;
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
 * Returns generated at.
 *
 * @return result value
 */

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
/**
 * Returns items.
 *
 * @return result value
 */

    public List<LowStockItem> getItems() {
        return items;
    }

/**
 * Performs to string.
 *
 * @return result value
 */
    @Override
    public String toString() {
        return "LowStockReport{" +
                "title='" + title + '\'' +
                ", generatedAt=" + generatedAt +
                ", items=" + items +
                '}';
    }
}
