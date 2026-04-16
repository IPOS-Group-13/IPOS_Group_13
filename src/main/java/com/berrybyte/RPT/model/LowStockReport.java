package com.berrybyte.RPT.model;

import java.time.LocalDateTime;
import java.util.List;

public class LowStockReport {
    private final String title;
    private final LocalDateTime generatedAt;
    private final List<LowStockItem> items;

    public LowStockReport(String title, LocalDateTime generatedAt, List<LowStockItem> items) {
        this.title = title;
        this.generatedAt = generatedAt;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public List<LowStockItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "LowStockReport{" +
                "title='" + title + '\'' +
                ", generatedAt=" + generatedAt +
                ", items=" + items +
                '}';
    }
}