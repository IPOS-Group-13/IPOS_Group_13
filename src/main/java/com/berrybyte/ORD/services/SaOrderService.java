package com.berrybyte.ORD.services;

import com.berrybyte.API.IOrderAPI;
import com.berrybyte.ORD.DTO.*;
import com.berrybyte.ORD.Status.AcceptOrderStatus;
import com.berrybyte.common.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaOrderService implements IOrderAPI {

    @Override
    public List<IncomingOrderRow> getOrdersForReview() throws Exception {
        String sql = """
                SELECT o.OrderId,
                       ma.CompanyName AS AccountHolder,
                       DATE_FORMAT(o.OrderDate, '%d/%m/%Y') AS OrderDate,
                       o.TotalAmount,
                       o.Status
                FROM Orders o
                JOIN MerchantAccounts ma ON o.MerchantId = ma.MerchantId
                JOIN Users u ON ma.UserId = u.UserId
                ORDER BY o.OrderId DESC
                """;

        List<IncomingOrderRow> orders = new ArrayList<>();

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orders.add(new IncomingOrderRow(
                        rs.getInt("OrderId"),
                        rs.getString("AccountHolder"),
                        rs.getString("OrderDate"),
                        rs.getDouble("TotalAmount"),
                        rs.getString("Status")
                ));
            }
        }

        return orders;
    }

    @Override
    public List<IncomingOrderRow> searchOrdersForReview(String keyword) throws Exception {
        String sql = """
                SELECT o.OrderId,
                       ma.CompanyName AS AccountHolder,
                       DATE_FORMAT(o.OrderDate, '%d/%m/%Y') AS OrderDate,
                       o.TotalAmount,
                       o.Status
                FROM Orders o
                JOIN MerchantAccounts ma ON o.MerchantId = ma.MerchantId
                JOIN Users u ON ma.UserId = u.UserId
                WHERE CAST(o.OrderId AS CHAR) LIKE ?
                   OR ma.CompanyName LIKE ?
                   OR ma.CompanyName LIKE ?
                   OR ma.IPOSAccountNumber LIKE ?
                   OR o.Status LIKE ?
                ORDER BY o.OrderId DESC
                """;

        String like = "%" + keyword + "%";
        List<IncomingOrderRow> orders = new ArrayList<>();

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 5; i++) {
                ps.setString(i, like);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(new IncomingOrderRow(
                            rs.getInt("OrderId"),
                            rs.getString("AccountHolder"),
                            rs.getString("OrderDate"),
                            rs.getDouble("TotalAmount"),
                            rs.getString("Status")
                    ));
                }
            }
        }
        return orders;
    }

    @Override
    public OrderDetails getOrderDetails(int orderId) throws Exception {
        String sql = """
                SELECT o.OrderId,
                       o.MerchantId,
                       ma.IPOSAccountNumber,
                       ma.CompanyName AS AccountHolder,
                       ma.CompanyName,
                       ma.Address,
                       DATE_FORMAT(o.OrderDate, '%d/%m/%Y') AS OrderDate,
                       o.Status,
                       o.TotalAmount
                FROM Orders o
                JOIN MerchantAccounts ma ON o.MerchantId = ma.MerchantId
                JOIN Users u ON ma.UserId = u.UserId
                WHERE o.OrderId = ?
                """;

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new OrderDetails(
                            rs.getInt("OrderId"),
                            rs.getInt("MerchantId"),
                            rs.getString("IPOSAccountNumber"),
                            rs.getString("AccountHolder"),
                            rs.getString("CompanyName"),
                            rs.getString("Address"),
                            rs.getString("OrderDate"),
                            rs.getString("Status"),
                            rs.getDouble("TotalAmount")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<OrderLine> getOrderItems(int orderId) throws Exception {
        String sql = """
                SELECT oi.ItemId,
                       c.Description,
                       c.PackageType,
                       c.UnitsInPack,
                       oi.UnitCost,
                       oi.Quantity,
                       oi.LineTotal
                FROM OrderItems oi
                JOIN Catalogue c ON oi.ItemId = c.ItemId
                WHERE oi.OrderId = ?
                ORDER BY oi.OrderItemId
                """;

        List<OrderLine> items = new ArrayList<>();

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new OrderLine(
                            rs.getInt("ItemId"),
                            rs.getString("Description"),
                            rs.getString("PackageType"),
                            rs.getInt("UnitsInPack"),
                            rs.getDouble("UnitCost"),
                            rs.getInt("Quantity"),
                            rs.getDouble("LineTotal")
                    ));
                }
            }
        }
        return items;
    }

    @Override
    public AcceptOrderStatus acceptOrder(int orderId, int staffUserId) throws Exception {
        Connection conn = null;

        try {
            conn = new DatabaseConnection().getConnection();
            conn.setAutoCommit(false);

            String orderSql = """
                    SELECT OrderId, MerchantId, Status, TotalAmount
                    FROM Orders
                    WHERE OrderId = ?
                    FOR UPDATE
                    """;

            int merchantId;
            double totalAmount;
            String status;

            try (PreparedStatement ps = conn.prepareStatement(orderSql)) {
                ps.setInt(1, orderId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return AcceptOrderStatus.ORDER_NOT_FOUND;
                    }
                    merchantId = rs.getInt("MerchantId");
                    totalAmount = rs.getDouble("TotalAmount");
                    status = rs.getString("Status");
                }
            }
            if (!"NEW".equalsIgnoreCase(status)) {
                conn.rollback();
                return AcceptOrderStatus.ORDER_ALREADY_ACCEPTED;
            }

            String merchantSql = """
                    SELECT AccountStatus
                    FROM MerchantAccounts
                    WHERE MerchantId = ?
                    FOR UPDATE
                    """;

            try (PreparedStatement ps = conn.prepareStatement(merchantSql)) {
                ps.setInt(1, merchantId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String accountStatus = rs.getString("AccountStatus");

                        if ("SUSPENDED".equalsIgnoreCase(accountStatus)) {
                            conn.rollback();
                            return AcceptOrderStatus.MERCHANT_SUSPENDED;
                        }

                        if ("IN_DEFAULT".equalsIgnoreCase(accountStatus)) {
                            conn.rollback();
                            return AcceptOrderStatus.MERCHANT_IN_DEFAULT;
                        }
                    }
                }
            }

            String stockSql = """
                    SELECT oi.ItemId, oi.Quantity, c.AvailabilityPacks
                    FROM OrderItems oi
                    JOIN Catalogue c ON oi.ItemId = c.ItemId
                    WHERE oi.OrderId = ?
                    FOR UPDATE
                    """;

            List<StockReduction> reductions = new ArrayList<>();

            try (PreparedStatement ps = conn.prepareStatement(stockSql)) {
                ps.setInt(1, orderId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int itemId = rs.getInt("ItemId");
                        int quantity = rs.getInt("Quantity");
                        int available = rs.getInt("AvailabilityPacks");

                        if (available < quantity) {
                            conn.rollback();
                            return AcceptOrderStatus.INSUFFICIENT_STOCK;
                        }

                        reductions.add(new StockReduction(itemId, quantity));
                    }
                }
            }

            if (reductions.isEmpty()) {
                conn.rollback();
                return AcceptOrderStatus.ERROR;
            }

            String reduceStockSql = """
                    UPDATE Catalogue
                    SET AvailabilityPacks = AvailabilityPacks - ?
                    WHERE ItemId = ?
                    """;

            try (PreparedStatement ps = conn.prepareStatement(reduceStockSql)) {
                for (StockReduction reduction : reductions) {
                    ps.setInt(1, reduction.quantity());
                    ps.setInt(2, reduction.itemId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            String insertInvoiceSql = """
                    INSERT INTO Invoices
                    (OrderId, MerchantId, InvoiceDate, DueDate, TotalAmount, AmountPaid, OutstandingBalance, PaymentStatus)
                    VALUES (?, ?, CURDATE(), LAST_DAY(CURDATE()), ?, 0.00, ?, 'PENDING')
                    """;

            int invoiceId;

            try (PreparedStatement ps = conn.prepareStatement(insertInvoiceSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, orderId);
                ps.setInt(2, merchantId);
                ps.setDouble(3, totalAmount);
                ps.setDouble(4, totalAmount);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conn.rollback();
                        return AcceptOrderStatus.INVOICE_GENERATION_FAILED;
                    }
                    invoiceId = keys.getInt(1);
                }
            }

            String insertInvoiceItemsSql = """
                    INSERT INTO InvoiceItems (InvoiceId, ItemId, UnitCost, Quantity, LineTotal)
                    SELECT ?, ItemId, UnitCost, Quantity, LineTotal
                    FROM OrderItems
                    WHERE OrderId = ?
                    """;

            try (PreparedStatement ps = conn.prepareStatement(insertInvoiceItemsSql)) {
                ps.setInt(1, invoiceId);
                ps.setInt(2, orderId);
                ps.executeUpdate();
            }

            String updateOrderSql = """
                    UPDATE Orders
                    SET Status = 'ACCEPTED',
                        AcceptedByUserId = ?,
                        AcceptedAt = NOW()
                    WHERE OrderId = ?
                    """;

            try (PreparedStatement ps = conn.prepareStatement(updateOrderSql)) {
                ps.setInt(1, staffUserId);
                ps.setInt(2, orderId);
                ps.executeUpdate();
            }

            String updateBalanceSql = """
                    UPDATE MerchantAccounts
                    SET OutstandingBalance = OutstandingBalance + ?
                    WHERE MerchantId = ?
                    """;

            try (PreparedStatement ps = conn.prepareStatement(updateBalanceSql)) {
                ps.setDouble(1, totalAmount);
                ps.setInt(2, merchantId);
                ps.executeUpdate();
            }
            conn.commit();
            return AcceptOrderStatus.SUCCESS;

        } catch (SQLIntegrityConstraintViolationException e) {
            if (conn != null) conn.rollback();
            return AcceptOrderStatus.INVOICE_GENERATION_FAILED;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) conn.rollback();
            return AcceptOrderStatus.ERROR;
        } finally {
            if (conn != null) conn.close();
        }
    }

    @Override
    public InvoiceDetails getInvoiceByOrderId(int orderId) throws Exception {
        String invoiceSql = """
                SELECT InvoiceId, OrderId, MerchantId,
                       DATE_FORMAT(InvoiceDate, '%d/%m/%Y') AS InvoiceDate,
                       DATE_FORMAT(DueDate, '%d/%m/%Y') AS DueDate,
                       TotalAmount, AmountPaid, OutstandingBalance, PaymentStatus
                FROM Invoices
                WHERE OrderId = ?
                """;

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement invoicePs = conn.prepareStatement(invoiceSql)) {

            invoicePs.setInt(1, orderId);

            try (ResultSet invoiceRs = invoicePs.executeQuery()) {
                if (!invoiceRs.next()) {
                    return null;
                }

                int invoiceId = invoiceRs.getInt("InvoiceId");
                List<InvoiceLine> items = new ArrayList<>();

                String itemsSql = """
                        SELECT ii.ItemId,
                               c.Description,
                               c.PackageType,
                               c.UnitsInPack,
                               ii.UnitCost,
                               ii.Quantity,
                               ii.LineTotal
                        FROM InvoiceItems ii
                        JOIN Catalogue c ON ii.ItemId = c.ItemId
                        WHERE ii.InvoiceId = ?
                        ORDER BY ii.InvoiceItemId
                        """;

                try (PreparedStatement itemsPs = conn.prepareStatement(itemsSql)) {
                    itemsPs.setInt(1, invoiceId);

                    try (ResultSet itemsRs = itemsPs.executeQuery()) {
                        while (itemsRs.next()) {
                            items.add(new InvoiceLine(
                                    itemsRs.getInt("ItemId"),
                                    itemsRs.getString("Description"),
                                    itemsRs.getString("PackageType"),
                                    itemsRs.getInt("UnitsInPack"),
                                    itemsRs.getDouble("UnitCost"),
                                    itemsRs.getInt("Quantity"),
                                    itemsRs.getDouble("LineTotal")
                            ));
                        }
                    }
                }
                return new InvoiceDetails(
                        invoiceRs.getInt("InvoiceId"),
                        invoiceRs.getInt("OrderId"),
                        invoiceRs.getInt("MerchantId"),
                        invoiceRs.getString("InvoiceDate"),
                        invoiceRs.getString("DueDate"),
                        invoiceRs.getDouble("TotalAmount"),
                        invoiceRs.getDouble("AmountPaid"),
                        invoiceRs.getDouble("OutstandingBalance"),
                        invoiceRs.getString("PaymentStatus"),
                        items
                );
            }
        }
    }

    @Override
    public List<MerchantOrderSummary> getMerchantOrderSummary(int merchantId) throws Exception {
        String sql = """
                SELECT OrderId,
                       DATE_FORMAT(OrderDate, '%d/%m/%Y') AS OrderedDate,
                       TotalAmount,
                       Status
                FROM Orders
                WHERE MerchantId = ?
                ORDER BY OrderDate DESC, OrderId DESC
                """;

        List<MerchantOrderSummary> rows = new ArrayList<>();

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, merchantId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new MerchantOrderSummary(
                            rs.getInt("OrderId"),
                            rs.getString("OrderedDate"),
                            rs.getDouble("TotalAmount"),
                            rs.getString("Status")
                    ));
                }
            }
        }
        return rows;
    }

    @Override
    public List<MerchantOrderSummary> getMerchantOrderSummary(int merchantId, LocalDate startDate, LocalDate endDate) throws Exception {
        String sql = """
                SELECT OrderId,
                       DATE_FORMAT(OrderDate, '%d/%m/%Y') AS OrderedDate,
                       TotalAmount,
                       Status
                FROM Orders
                WHERE MerchantId = ?
                  AND OrderDate BETWEEN ? AND ?
                ORDER BY OrderDate DESC, OrderId DESC
                """;

        List<MerchantOrderSummary> rows = new ArrayList<>();

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, merchantId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new MerchantOrderSummary(
                            rs.getInt("OrderId"),
                            rs.getString("OrderedDate"),
                            rs.getDouble("TotalAmount"),
                            rs.getString("Status")
                    ));
                }
            }
        }
        return rows;
    }

    @Override
    public List<OrderSummaryRow> getOrdersSummary() throws Exception {
        String sql = """
                SELECT o.OrderId,
                       ma.CompanyName AS MerchantName,
                       COALESCE(DATE_FORMAT(o.DispatchDateTime, '%d/%m/%Y %H:%i'), '') AS DispatchedDate,
                       o.TotalAmount,
                       o.Status AS DeliveredStatus,
                       COALESCE(i.PaymentStatus, 'N/A') AS PaidStatus,
                       COALESCE(o.CourierName, '') AS CourierName,
                       COALESCE(o.CourierReference, '') AS CourierRef,
                       COALESCE(DATE_FORMAT(o.ExpectedDeliveryDateTime, '%d/%m/%Y %H:%i'), '') AS ExpectedDelivery
                FROM Orders o
                JOIN MerchantAccounts ma ON o.MerchantId = ma.MerchantId
                LEFT JOIN Invoices i ON o.OrderId = i.OrderId
                ORDER BY o.OrderDate DESC, o.OrderId DESC
                """;

        List<OrderSummaryRow> rows = new ArrayList<>();

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(mapOrderSummaryRow(rs));
            }
        }

        return rows;
    }

    @Override
    public List<OrderSummaryRow> searchOrdersSummary(String keyword) throws Exception {
        String sql = """
                SELECT o.OrderId,
                       ma.CompanyName AS MerchantName,
                       COALESCE(DATE_FORMAT(o.DispatchDateTime, '%d/%m/%Y %H:%i'), '') AS DispatchedDate,
                       o.TotalAmount,
                       o.Status AS DeliveredStatus,
                       COALESCE(i.PaymentStatus, 'N/A') AS PaidStatus,
                       COALESCE(o.CourierName, '') AS CourierName,
                       COALESCE(o.CourierReference, '') AS CourierRef,
                       COALESCE(DATE_FORMAT(o.ExpectedDeliveryDateTime, '%d/%m/%Y %H:%i'), '') AS ExpectedDelivery
                FROM Orders o
                JOIN MerchantAccounts ma ON o.MerchantId = ma.MerchantId
                JOIN Users u ON ma.UserId = u.UserId
                LEFT JOIN Invoices i ON o.OrderId = i.OrderId
                WHERE CAST(o.OrderId AS CHAR) LIKE ?
                   OR ma.CompanyName LIKE ?
                   OR u.Username LIKE ?
                ORDER BY o.OrderDate DESC, o.OrderId DESC
                """;

        String like = "%" + keyword + "%";
        List<OrderSummaryRow> rows = new ArrayList<>();

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapOrderSummaryRow(rs));
                }
            }
        }

        return rows;
    }

    @Override
    public boolean updateDispatchDetails(int orderId,
                                         String courierName,
                                         String courierRef,
                                         LocalDateTime dispatchedDateTime,
                                         LocalDateTime expectedDeliveryDateTime,
                                         String status) throws Exception {
        String sql = """
                UPDATE Orders
                SET CourierName = ?,
                    CourierReference = ?,
                    DispatchDateTime = ?,
                    ExpectedDeliveryDateTime = ?,
                    Status = ?
                WHERE OrderId = ?
                """;

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courierName);
            ps.setString(2, courierRef);
            ps.setTimestamp(3, Timestamp.valueOf(dispatchedDateTime));
            ps.setTimestamp(4, Timestamp.valueOf(expectedDeliveryDateTime));
            ps.setString(5, status);
            ps.setInt(6, orderId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public String trackOrder(int orderId) throws Exception {
        String sql = "SELECT Status FROM Orders WHERE OrderId = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Status");
                }
            }
        }
        return null;
    }

    private OrderSummaryRow mapOrderSummaryRow(ResultSet rs) throws SQLException {
        return new OrderSummaryRow(
                rs.getInt("OrderId"),
                rs.getString("MerchantName"),
                rs.getString("DispatchedDate"),
                rs.getDouble("TotalAmount"),
                rs.getString("DeliveredStatus"),
                rs.getString("PaidStatus"),
                rs.getString("CourierName"),
                rs.getString("CourierRef"),
                rs.getString("ExpectedDelivery")
        );
    }

    private record StockReduction(int itemId, int quantity) { }
}
