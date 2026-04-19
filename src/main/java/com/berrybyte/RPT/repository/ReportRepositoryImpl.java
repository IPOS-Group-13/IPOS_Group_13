package com.berrybyte.RPT.repository;

import com.berrybyte.RPT.model.*;
import com.berrybyte.common.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents report repository impl.
 */
public class ReportRepositoryImpl implements ReportRepository {

/**
 * Executes the find low stock items workflow.
 *
 * @return result value
 */
    @Override
    public List<LowStockItem> findLowStockItems() {
        List<LowStockItem> items = new ArrayList<>();

        String sql = """
            SELECT ItemId, Description, AvailabilityPacks, StockLimitPacks
            FROM Catalogue
            WHERE AvailabilityPacks < StockLimitPacks
              AND IsDeleted = 0
            ORDER BY AvailabilityPacks ASC
        """;

        DatabaseConnection databaseConnection = new DatabaseConnection();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                items.add(new LowStockItem(
                        rs.getInt("ItemId"),
                        rs.getString("Description"),
                        rs.getInt("AvailabilityPacks"),
                        rs.getInt("StockLimitPacks")
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch low stock report data", e);
        }

        return items;
    }

/**
 * Executes the find merchant order summary workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    @Override
    public List<MerchantOrderSummaryRow> findMerchantOrderSummary(int merchantId,
                                                                  java.time.LocalDate startDate,
                                                                  java.time.LocalDate endDate) {
        List<MerchantOrderSummaryRow> rows = new ArrayList<>();

        String sql = """
            SELECT o.OrderId,
                   o.OrderDate,
                   o.TotalAmount,
                   o.DispatchDateTime,
                   o.DeliveredDateTime,
                   o.Status,
                   i.PaymentStatus
            FROM Orders o
            LEFT JOIN Invoices i ON o.OrderId = i.OrderId
            WHERE o.MerchantId = ?
              AND o.OrderDate BETWEEN ? AND ?
            ORDER BY o.OrderDate ASC
        """;

        DatabaseConnection databaseConnection = new DatabaseConnection();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, merchantId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp dispatchTs = rs.getTimestamp("DispatchDateTime");
                    Timestamp deliveredTs = rs.getTimestamp("DeliveredDateTime");

                    rows.add(new MerchantOrderSummaryRow(
                            rs.getInt("OrderId"),
                            rs.getDate("OrderDate").toLocalDate(),
                            rs.getBigDecimal("TotalAmount"),
                            dispatchTs != null ? dispatchTs.toLocalDateTime() : null,
                            deliveredTs != null ? deliveredTs.toLocalDateTime() : null,
                            rs.getString("Status"),
                            rs.getString("PaymentStatus")
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch merchant order summary", e);
        }

        return rows;
    }
/**
 * Executes the find invoice listing workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    @Override
    public List<InvoiceListingRow> findInvoiceListing(Integer merchantId,
                                                      java.time.LocalDate startDate,
                                                      java.time.LocalDate endDate) {
        List<InvoiceListingRow> rows = new ArrayList<>();

        String sql = """
        SELECT InvoiceId,
               OrderId,
               MerchantId,
               InvoiceDate,
               DueDate,
               TotalAmount,
               AmountPaid,
               OutstandingBalance,
               PaymentStatus
        FROM Invoices
        WHERE InvoiceDate BETWEEN ? AND ?
          AND (? IS NULL OR MerchantId = ?)
        ORDER BY InvoiceDate ASC
    """;

        DatabaseConnection databaseConnection = new DatabaseConnection();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            if (merchantId == null) {
                stmt.setNull(3, java.sql.Types.INTEGER);
                stmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(3, merchantId);
                stmt.setInt(4, merchantId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(new InvoiceListingRow(
                            rs.getInt("InvoiceId"),
                            rs.getInt("OrderId"),
                            rs.getInt("MerchantId"),
                            rs.getDate("InvoiceDate").toLocalDate(),
                            rs.getDate("DueDate").toLocalDate(),
                            rs.getBigDecimal("TotalAmount"),
                            rs.getBigDecimal("AmountPaid"),
                            rs.getBigDecimal("OutstandingBalance"),
                            rs.getString("PaymentStatus")
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch invoice listing report", e);
        }

        return rows;
    }

/**
 * Executes the find stock turnover workflow.
 *
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    @Override
    public List<StockTurnoverRow> findStockTurnover(java.time.LocalDate startDate,
                                                    java.time.LocalDate endDate) {
        List<StockTurnoverRow> rows = new ArrayList<>();

        String sql = """
        SELECT oi.ItemId,
               c.Description,
               SUM(oi.Quantity) AS QuantitySold,
               SUM(oi.LineTotal) AS SalesValue
        FROM OrderItems oi
        JOIN Orders o ON oi.OrderId = o.OrderId
        JOIN Catalogue c ON oi.ItemId = c.ItemId
        WHERE o.OrderDate BETWEEN ? AND ?
          AND c.IsDeleted = 0
        GROUP BY oi.ItemId, c.Description
        ORDER BY QuantitySold DESC
    """;

        DatabaseConnection databaseConnection = new DatabaseConnection();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(new StockTurnoverRow(
                            rs.getInt("ItemId"),
                            rs.getString("Description"),
                            rs.getInt("QuantitySold"),
                            rs.getBigDecimal("SalesValue")
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch stock turnover report", e);
        }

        return rows;
    }

/**
 * Executes the find merchant activity report workflow.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    @Override
    public MerchantActivityReport findMerchantActivityReport(int merchantId,
                                                             java.time.LocalDate startDate,
                                                             java.time.LocalDate endDate) {
        DatabaseConnection databaseConnection = new DatabaseConnection();

        String merchantSql = """
        SELECT MerchantId, CompanyName, IPOSAccountNumber, Address
        FROM MerchantAccounts
        WHERE MerchantId = ?
    """;

        String orderSql = """
        SELECT o.OrderId,
               o.OrderDate,
               o.TotalAmount,
               i.PaymentStatus,
               i.OutstandingBalance
        FROM Orders o
        LEFT JOIN Invoices i ON o.OrderId = i.OrderId
        WHERE o.MerchantId = ?
          AND o.OrderDate BETWEEN ? AND ?
        ORDER BY o.OrderDate ASC
    """;

        String itemSql = """
        SELECT oi.ItemId,
               c.Description,
               oi.Quantity,
               oi.UnitCost,
               oi.LineTotal
        FROM OrderItems oi
        JOIN Catalogue c ON oi.ItemId = c.ItemId
        WHERE oi.OrderId = ?
        ORDER BY oi.OrderItemId ASC
    """;

        try (Connection connection = databaseConnection.getConnection()) {

            String companyName = null;
            String iposAccountNumber = null;
            String address = null;

            try (PreparedStatement merchantStmt = connection.prepareStatement(merchantSql)) {
                merchantStmt.setInt(1, merchantId);

                try (ResultSet rs = merchantStmt.executeQuery()) {
                    if (rs.next()) {
                        companyName = rs.getString("CompanyName");
                        iposAccountNumber = rs.getString("IPOSAccountNumber");
                        address = rs.getString("Address");
                    } else {
                        throw new RuntimeException("Merchant not found for MerchantId=" + merchantId);
                    }
                }
            }

            List<MerchantActivityOrderSection> orderSections = new ArrayList<>();

            try (PreparedStatement orderStmt = connection.prepareStatement(orderSql)) {
                orderStmt.setInt(1, merchantId);
                orderStmt.setDate(2, Date.valueOf(startDate));
                orderStmt.setDate(3, Date.valueOf(endDate));

                try (ResultSet orderRs = orderStmt.executeQuery()) {
                    while (orderRs.next()) {
                        int orderId = orderRs.getInt("OrderId");

                        List<MerchantActivityItemRow> itemRows = new ArrayList<>();

                        try (PreparedStatement itemStmt = connection.prepareStatement(itemSql)) {
                            itemStmt.setInt(1, orderId);

                            try (ResultSet itemRs = itemStmt.executeQuery()) {
                                while (itemRs.next()) {
                                    itemRows.add(new MerchantActivityItemRow(
                                            itemRs.getInt("ItemId"),
                                            itemRs.getString("Description"),
                                            itemRs.getInt("Quantity"),
                                            itemRs.getBigDecimal("UnitCost"),
                                            itemRs.getBigDecimal("LineTotal")
                                    ));
                                }
                            }
                        }

                        orderSections.add(new MerchantActivityOrderSection(
                                orderId,
                                orderRs.getDate("OrderDate").toLocalDate(),
                                orderRs.getBigDecimal("TotalAmount"),
                                orderRs.getString("PaymentStatus"),
                                orderRs.getBigDecimal("OutstandingBalance"),
                                itemRows
                        ));
                    }
                }
            }

            return new MerchantActivityReport(
                    "Merchant Activity Detailed Report",
                    merchantId,
                    companyName,
                    iposAccountNumber,
                    address,
                    startDate,
                    endDate,
                    java.time.LocalDateTime.now(),
                    orderSections,
                    0,
                    java.math.BigDecimal.ZERO
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch merchant activity report", e);
        }
    }

/**
 * Executes the find info pharma turnover workflow.
 *
 * @param startDate start date
 * @param endDate end date
 * @return result value
 */
    @Override
    public List<InfoPharmaTurnoverRow> findInfoPharmaTurnover(java.time.LocalDate startDate,
                                                              java.time.LocalDate endDate) {
        List<InfoPharmaTurnoverRow> rows = new ArrayList<>();

        String sql = """
        SELECT InvoiceId,
               OrderId,
               MerchantId,
               InvoiceDate,
               TotalAmount,
               AmountPaid,
               OutstandingBalance,
               PaymentStatus
        FROM Invoices
        WHERE InvoiceDate BETWEEN ? AND ?
        ORDER BY InvoiceDate ASC
    """;

        DatabaseConnection databaseConnection = new DatabaseConnection();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(new InfoPharmaTurnoverRow(
                            rs.getInt("InvoiceId"),
                            rs.getInt("OrderId"),
                            rs.getInt("MerchantId"),
                            rs.getDate("InvoiceDate").toLocalDate(),
                            rs.getBigDecimal("TotalAmount"),
                            rs.getBigDecimal("AmountPaid"),
                            rs.getBigDecimal("OutstandingBalance"),
                            rs.getString("PaymentStatus")
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch InfoPharma turnover report", e);
        }

        return rows;
    }

/**
 * Executes the find merchant options workflow.
 *
 * @return result value
 */
    @Override
    public List<MerchantOption> findMerchantOptions() {
        List<MerchantOption> merchants = new ArrayList<>();

        String sql = """
        SELECT MerchantId, CompanyName
        FROM MerchantAccounts
        WHERE COALESCE(IsActivated, 1) = 1
        ORDER BY CompanyName ASC
    """;

        DatabaseConnection databaseConnection = new DatabaseConnection();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                merchants.add(new MerchantOption(
                        rs.getInt("MerchantId"),
                        rs.getString("CompanyName")
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch merchant options.", e);
        }

        return merchants;
    }

/**
 * Executes the find overdue balance report workflow.
 *
 * @param merchantId merchant id
 * @return result value
 */
    @Override
    public List<OverdueBalanceRow> findOverdueBalanceReport(Integer merchantId) {
        List<OverdueBalanceRow> rows = new ArrayList<>();

        String sql = """
        SELECT m.MerchantId,
               m.CompanyName,
               m.IPOSAccountNumber,
               m.AccountStatus,
               m.CreditLimit,
               m.OutstandingBalance,
               MIN(i.DueDate) AS OldestDueDate,
               SUM(i.OutstandingBalance) AS TotalOverdueAmount,
               COUNT(i.InvoiceId) AS OverdueInvoiceCount
        FROM MerchantAccounts m
        JOIN Invoices i ON m.MerchantId = i.MerchantId
        WHERE i.OutstandingBalance > 0
          AND i.DueDate < CURRENT_DATE
          AND (? IS NULL OR m.MerchantId = ?)
        GROUP BY m.MerchantId,
                 m.CompanyName,
                 m.IPOSAccountNumber,
                 m.AccountStatus,
                 m.CreditLimit,
                 m.OutstandingBalance
        ORDER BY TotalOverdueAmount DESC, OldestDueDate ASC
    """;

        DatabaseConnection databaseConnection = new DatabaseConnection();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            if (merchantId == null) {
                stmt.setNull(1, java.sql.Types.INTEGER);
                stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(1, merchantId);
                stmt.setInt(2, merchantId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(new OverdueBalanceRow(
                            rs.getInt("MerchantId"),
                            rs.getString("CompanyName"),
                            rs.getString("IPOSAccountNumber"),
                            rs.getString("AccountStatus"),
                            rs.getBigDecimal("CreditLimit"),
                            rs.getBigDecimal("OutstandingBalance"),
                            rs.getDate("OldestDueDate").toLocalDate(),
                            rs.getBigDecimal("TotalOverdueAmount"),
                            rs.getInt("OverdueInvoiceCount")
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch overdue balance report", e);
        }

        return rows;
    }

}
