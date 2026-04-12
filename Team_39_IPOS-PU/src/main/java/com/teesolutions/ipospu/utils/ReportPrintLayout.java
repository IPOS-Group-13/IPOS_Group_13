package com.teesolutions.ipospu.utils;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Builds a black-on-white, page-width-bound report for {@link javafx.print.PrinterJob}.
 * Uses a {@link GridPane} for tabular data so columns stay within the printable width and wrap.
 */
public final class ReportPrintLayout {

    private static final String PRINT_CSS = "/com/teesolutions/ipospu/print-report.css";

    private static final String BLACK = "-fx-text-fill: #000000;";
    private static final String PANEL = BLACK + "-fx-background-color: #ffffff;";

    /** Fallback when no {@link javafx.print.PageLayout} is available yet. */
    private static final double DEFAULT_PRINTABLE_WIDTH_PT = 520;

    private ReportPrintLayout() {
    }

    /**
     * @param printableWidth layout width in points (typically from {@code PageLayout.getPrintableWidth()}).
     */
    public static VBox buildPrintableRoot(
            String reportTitle,
            String periodLine,
            List<String> columnKeys,
            List<Map<String, Object>> rows,
            double printableWidth
    ) {
        double pageW = printableWidth > 80 ? printableWidth : DEFAULT_PRINTABLE_WIDTH_PT;
        Insets rootPad = new Insets(20, 28, 24, 28);
        double innerW = Math.max(160, pageW - rootPad.getLeft() - rootPad.getRight());

        VBox root = new VBox(12);
        root.setPadding(rootPad);
        root.setStyle(PANEL);
        root.setAlignment(Pos.TOP_LEFT);
        root.setMinWidth(pageW);
        root.setPrefWidth(pageW);
        root.setMaxWidth(pageW);
        root.getStyleClass().add("report-print-root");
        URL css = ReportPrintLayout.class.getResource(PRINT_CSS);
        if (css != null) {
            root.getStylesheets().add(css.toExternalForm());
        }

        Label title = new Label(reportTitle == null ? "Report" : reportTitle);
        title.getStyleClass().add("report-print-title");
        title.setFont(Font.font(null, FontWeight.BOLD, 18));
        title.setStyle(BLACK);
        title.setWrapText(true);
        title.setMaxWidth(innerW);

        Separator rule = new Separator();
        rule.setMaxWidth(innerW);

        Label period = new Label(periodLine == null ? "" : periodLine);
        period.setFont(Font.font(10));
        period.setStyle(BLACK);
        period.setWrapText(true);
        period.setMaxWidth(innerW * 0.55);

        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.UK));
        String timePart = now.format(DateTimeFormatter.ofPattern("HH:mm"));

        Label generatedHeading = new Label("Generated");
        generatedHeading.setFont(Font.font(null, FontWeight.BOLD, 9));
        generatedHeading.setStyle(BLACK + "-fx-opacity: 0.95;");
        Label generatedDate = new Label("Date: " + datePart);
        generatedDate.setFont(Font.font(9));
        generatedDate.setStyle(BLACK + "-fx-opacity: 0.9;");
        generatedDate.setWrapText(true);
        Label generatedTime = new Label("Time: " + timePart);
        generatedTime.setFont(Font.font(9));
        generatedTime.setStyle(BLACK + "-fx-opacity: 0.9;");

        VBox generatedBlock = new VBox(2, generatedHeading, generatedDate, generatedTime);
        generatedBlock.setAlignment(Pos.TOP_RIGHT);

        Region metaSpacer = new Region();
        HBox.setHgrow(metaSpacer, Priority.ALWAYS);
        HBox metaRow = new HBox(10, period, metaSpacer, generatedBlock);
        metaRow.setAlignment(Pos.TOP_LEFT);
        metaRow.setMaxWidth(innerW);
        metaRow.setPrefWidth(innerW);

        String summary = computeSummaryLine(reportTitle, columnKeys, rows);
        VBox summaryBox = new VBox(6);
        summaryBox.getStyleClass().add("report-print-summary");
        summaryBox.setMaxWidth(innerW);
        summaryBox.setPadding(new Insets(10, 12, 10, 12));
        Label summaryHeading = new Label("Administrator summary");
        summaryHeading.setFont(Font.font(null, FontWeight.BOLD, 10));
        summaryHeading.setStyle(BLACK);
        Label summaryLabel = new Label(summary);
        summaryLabel.setFont(Font.font(9.5));
        summaryLabel.setStyle(BLACK);
        summaryLabel.setWrapText(true);
        summaryLabel.setMaxWidth(innerW - 24);
        summaryBox.getChildren().addAll(summaryHeading, summaryLabel);

        Label detailHeading = new Label("Report data"
                + (rows == null || rows.isEmpty() ? "" : " (" + rows.size() + " row" + (rows.size() == 1 ? "" : "s") + ")"));
        detailHeading.setFont(Font.font(null, FontWeight.BOLD, 10));
        detailHeading.setStyle(BLACK);

        GridPane grid = buildDataGrid(columnKeys, rows == null ? List.of() : rows, innerW);
        grid.setMaxWidth(innerW);
        grid.setPrefWidth(innerW);

        root.getChildren().addAll(title, rule, metaRow, summaryBox, detailHeading, grid);
        return root;
    }

    /**
     * Overload for callers that do not yet know printable width (uses default).
     */
    public static VBox buildPrintableRoot(
            String reportTitle,
            String periodLine,
            List<String> columnKeys,
            List<Map<String, Object>> rows
    ) {
        return buildPrintableRoot(reportTitle, periodLine, columnKeys, rows, DEFAULT_PRINTABLE_WIDTH_PT);
    }

    private static GridPane buildDataGrid(List<String> columnKeys, List<Map<String, Object>> rows, double innerWidth) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("report-print-grid");
        grid.setHgap(8);
        grid.setVgap(6);
        grid.setPadding(new Insets(8, 0, 0, 0));

        int cols = columnKeys == null ? 0 : columnKeys.size();
        if (cols == 0) {
            Label empty = new Label("No columns.");
            empty.setStyle(BLACK);
            grid.add(empty, 0, 0);
            return grid;
        }

        double[] weights = columnWeights(columnKeys);
        double sumW = Arrays.stream(weights).sum();
        for (int c = 0; c < cols; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 * weights[c] / sumW);
            cc.setHgrow(Priority.SOMETIMES);
            cc.setMinWidth(36);
            grid.getColumnConstraints().add(cc);
        }

        for (int c = 0; c < cols; c++) {
            String key = columnKeys.get(c);
            Label head = new Label(humanizeColumnTitle(key));
            head.setFont(Font.font(null, FontWeight.BOLD, 8.5));
            head.setStyle(BLACK);
            head.setWrapText(true);
            head.setMaxWidth(Double.MAX_VALUE);
            GridPane.setConstraints(head, c, 0, 1, 1, HPos.LEFT, VPos.BOTTOM, Priority.ALWAYS, Priority.NEVER);
            grid.add(head, c, 0);
        }

        for (int r = 0; r < rows.size(); r++) {
            Map<String, Object> row = rows.get(r);
            for (int c = 0; c < cols; c++) {
                String key = columnKeys.get(c);
                Object raw = row == null ? null : row.get(key);
                String text = raw == null ? "" : String.valueOf(raw);
                Label cell = new Label(text);
                cell.setFont(Font.font(8));
                cell.setStyle(BLACK);
                cell.setWrapText(true);
                cell.setMaxWidth(Double.MAX_VALUE);
                GridPane.setConstraints(cell, c, r + 1, 1, 1, HPos.LEFT, VPos.TOP, Priority.ALWAYS, Priority.NEVER);
                grid.add(cell, c, r + 1);
            }
        }

        return grid;
    }

    /**
     * Wider columns for text-heavy fields so IDs stay narrow.
     */
    private static double[] columnWeights(List<String> columnKeys) {
        double[] w = new double[columnKeys.size()];
        for (int i = 0; i < columnKeys.size(); i++) {
            String k = columnKeys.get(i) == null ? "" : columnKeys.get(i).toLowerCase(Locale.UK);
            if (k.contains("name") || k.contains("description")) {
                w[i] = 2.2;
            } else if (k.contains("time") || k.contains("date") || k.contains("email")) {
                w[i] = 1.6;
            } else if (k.contains("id") && !k.contains("name")) {
                w[i] = 0.85;
            } else {
                w[i] = 1.0;
            }
        }
        return w;
    }

    static String humanizeColumnTitle(String key) {
        if (key == null || key.isBlank()) {
            return "";
        }
        String s = key.replace('_', ' ');
        if (s.length() == 1) {
            return s.toUpperCase(Locale.UK);
        }
        return s.substring(0, 1).toUpperCase(Locale.UK) + s.substring(1);
    }

    static String computeSummaryLine(String reportTitle, List<String> columnKeys, List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return "Summary: no rows in this report.";
        }
        String t = reportTitle == null ? "" : reportTitle.toLowerCase(Locale.UK);
        if (t.contains("sales")) {
            return summarizeSales(rows);
        }
        if (t.contains("engagement")) {
            return summarizeEngagement(rows);
        }
        if (t.contains("campaign")) {
            return summarizeCampaigns(rows);
        }
        return summarizeGeneric(rows, columnKeys);
    }

    private static String summarizeSales(List<Map<String, Object>> rows) {
        double sumQty = 0;
        double sumRevenue = 0;
        double weightedUnitNumerator = 0;
        int n = rows.size();
        for (Map<String, Object> row : rows) {
            double qty = toDouble(firstPresent(row, "qty_sold", "Qty_sold", "QTY_SOLD"));
            double unit = toDouble(firstPresent(row, "unit_price", "Unit_price", "UNIT_PRICE"));
            double line = toDouble(firstPresent(row, "total_price", "Total_price", "TOTAL_PRICE"));
            sumQty += qty;
            sumRevenue += line > 0 ? line : unit * qty;
            weightedUnitNumerator += unit * qty;
        }
        double avgUnit = sumQty > 0 ? weightedUnitNumerator / sumQty : 0;
        double avgRevenuePerLine = n > 0 ? sumRevenue / n : 0;
        return String.format(Locale.UK,
                "Summary: %d product line(s). Total quantity sold: %.0f. Total revenue: £%.2f. "
                        + "Weighted average unit price: £%.2f. Average revenue per line: £%.2f.",
                n, sumQty, sumRevenue, avgUnit, avgRevenuePerLine);
    }

    private static String summarizeEngagement(List<Map<String, Object>> rows) {
        double sumHits = 0;
        double sumPurchased = 0;
        double sumConv = 0;
        int convCount = 0;
        for (Map<String, Object> row : rows) {
            sumHits += toDouble(firstPresent(row, "campaign_hits", "Campaign_hits"));
            sumPurchased += toDouble(firstPresent(row, "item_purchased_count", "Item_purchased_count"));
            Double c = toDoubleOrNull(firstPresent(row, "conversion_rate", "Conversion_rate"));
            if (c != null) {
                sumConv += c;
                convCount++;
            }
        }
        double meanConv = convCount > 0 ? sumConv / convCount : 0;
        double meanHits = rows.isEmpty() ? 0 : sumHits / rows.size();
        return String.format(Locale.UK,
                "Summary: %d campaign/product row(s). Total campaign hit events: %.0f. Total units purchased (tracked): %.0f. "
                        + "Mean hits per row: %.1f. Mean conversion rate: %.2f%%.",
                rows.size(), sumHits, sumPurchased, meanHits, meanConv);
    }

    private static String summarizeCampaigns(List<Map<String, Object>> rows) {
        List<Long> durationsDays = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            LocalDateTime start = toLocalDateTime(firstPresent(row, "start_time", "Start_time"));
            LocalDateTime end = toLocalDateTime(firstPresent(row, "end_time", "End_time"));
            if (start != null && end != null && !end.isBefore(start)) {
                durationsDays.add(ChronoUnit.DAYS.between(start, end));
            }
        }
        double avgDays = durationsDays.isEmpty()
                ? 0
                : durationsDays.stream().mapToLong(Long::longValue).average().orElse(0);
        return String.format(Locale.UK,
                "Summary: %d campaign(s). Average campaign length: %.1f days (from start to end date).",
                rows.size(), avgDays);
    }

    private static String summarizeGeneric(List<Map<String, Object>> rows, List<String> keys) {
        int numericCols = 0;
        double grand = 0;
        for (String k : keys) {
            if (k == null) {
                continue;
            }
            double colSum = 0;
            int colCount = 0;
            for (Map<String, Object> row : rows) {
                Double d = toDoubleOrNull(row.get(k));
                if (d != null) {
                    colSum += d;
                    colCount++;
                }
            }
            if (colCount > 0) {
                numericCols++;
                grand += colSum / colCount;
            }
        }
        if (numericCols == 0) {
            return String.format(Locale.UK, "Summary: %d row(s).", rows.size());
        }
        return String.format(Locale.UK,
                "Summary: %d row(s). Mean of column averages (numeric columns only): %.2f.",
                rows.size(), grand / numericCols);
    }

    private static Object firstPresent(Map<String, Object> row, String... keys) {
        for (String k : keys) {
            if (row.containsKey(k) && row.get(k) != null) {
                return row.get(k);
            }
        }
        for (String k : row.keySet()) {
            if (k != null && row.get(k) != null) {
                for (String want : keys) {
                    if (want != null && want.equalsIgnoreCase(k)) {
                        return row.get(k);
                    }
                }
            }
        }
        return null;
    }

    private static LocalDateTime toLocalDateTime(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Timestamp) {
            return ((Timestamp) o).toLocalDateTime();
        }
        if (o instanceof LocalDateTime) {
            return (LocalDateTime) o;
        }
        return null;
    }

    private static Double toDoubleOrNull(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(o).trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static double toDouble(Object o) {
        Double d = toDoubleOrNull(o);
        return d == null ? 0.0 : d;
    }

    /**
     * Converts report rows to a list of maps preserving column key order for printing.
     */
    public static List<Map<String, Object>> rowsFromMaps(List<String> columnKeys, List<Map<String, Object>> sourceRows) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> src : sourceRows) {
            LinkedHashMap<String, Object> copy = new LinkedHashMap<>();
            for (String k : columnKeys) {
                copy.put(k, src.get(k));
            }
            out.add(copy);
        }
        return out;
    }
}
