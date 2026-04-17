package com.berrybyte.ORD.services;

import com.berrybyte.API.IOrderAPI;
import com.berrybyte.ORD.helpers.*;
import com.berrybyte.ORD.Status.AcceptOrderStatus;
import com.berrybyte.ACC.services.MerchantStatusService;
import com.berrybyte.common.DatabaseConnection;

import java.nio.file.Path;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaOrderService implements IOrderAPI {

    private final ExternalCommsQueueService externalCommsQueueService = new ExternalCommsQueueService();
    private final InvoicePdfService invoicePdfService = new InvoicePdfService();
    private final InvoiceStorageService invoiceStorageService = new InvoiceStorageService();
    private final MerchantStatusService merchantStatusService = new MerchantStatusService();

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
                        rs.getString("Status")));
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
                            rs.getString("Status")));
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
                            rs.getDouble("TotalAmount"));
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
                            rs.getDouble("LineTotal")));
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

            merchantStatusService.refreshMerchantStatus(conn, merchantId, LocalDate.now());

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

            double discountedTotalAmount = calculateDiscountedTotal(conn, merchantId, totalAmount);

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
                ps.setDouble(3, discountedTotalAmount);
                ps.setDouble(4, discountedTotalAmount);
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
                ps.setDouble(1, discountedTotalAmount);
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
                SELECT i.InvoiceId, i.OrderId, i.MerchantId,
                       ma.IPOSAccountNumber,
                       ma.CompanyName,
                       u.Name AS MerchantName,
                       u.Email,
                       u.PhoneNumber,
                       ma.Address,
                       DATE_FORMAT(i.InvoiceDate, '%d/%m/%Y') AS InvoiceDate,
                       DATE_FORMAT(i.DueDate, '%d/%m/%Y') AS DueDate,
                       i.TotalAmount, i.AmountPaid, i.OutstandingBalance, i.PaymentStatus
                FROM Invoices i
                JOIN MerchantAccounts ma ON i.MerchantId = ma.MerchantId
                JOIN Users u ON ma.UserId = u.UserId
                WHERE i.OrderId = ?
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
                                    itemsRs.getDouble("LineTotal")));
                        }
                    }
                }
                return new InvoiceDetails(
                        invoiceRs.getInt("InvoiceId"),
                        invoiceRs.getInt("OrderId"),
                        invoiceRs.getInt("MerchantId"),
                        invoiceRs.getString("IPOSAccountNumber"),
                        invoiceRs.getString("CompanyName"),
                        invoiceRs.getString("MerchantName"),
                        invoiceRs.getString("Email"),
                        invoiceRs.getString("PhoneNumber"),
                        invoiceRs.getString("Address"),
                        invoiceRs.getString("InvoiceDate"),
                        invoiceRs.getString("DueDate"),
                        invoiceRs.getDouble("TotalAmount"),
                        invoiceRs.getDouble("AmountPaid"),
                        invoiceRs.getDouble("OutstandingBalance"),
                        invoiceRs.getString("PaymentStatus"),
                        items);
            }
        }
    }

    public Path generateInvoicePdfForOrder(int orderId) throws Exception {
        InvoiceDetails invoiceDetails = getInvoiceByOrderId(orderId);
        if (invoiceDetails == null) {
            throw new IllegalArgumentException("No invoice exists for the selected order.");
        }
        return invoicePdfService.generateInvoicePdf(invoiceDetails);
    }

    public Path openInvoicePdfForOrder(int orderId) throws Exception {
        Path pdfPath = generateInvoicePdfForOrder(orderId);
        invoicePdfService.openInvoicePdf(pdfPath);
        return pdfPath;
    }

    public String queueOrderAcceptedEmailForOrder(int orderId, Path pdfPath) throws Exception {
        InvoiceDetails invoiceDetails = getInvoiceByOrderId(orderId);
        if (invoiceDetails == null) {
            throw new IllegalArgumentException("No invoice exists for the selected order.");
        }
        String invoiceUrl = invoiceStorageService.uploadInvoiceAndCreateAccessUrl(invoiceDetails, pdfPath);
        return externalCommsQueueService.queueOrderAcceptedEmail(invoiceDetails, invoiceUrl);
    }

    @Override
    public void recordPayment(int orderId, BigDecimal paymentAmount, String paymentMethod, int recordedByUserId) throws Exception {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Select an order first.");
        }
        if (paymentAmount == null) {
            throw new IllegalArgumentException("Enter a payment amount.");
        }
        if (paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0.");
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("Select a payment method.");
        }
        if (recordedByUserId <= 0) {
            throw new IllegalArgumentException("No logged-in user was found.");
        }

        BigDecimal normalizedPaymentAmount = paymentAmount.setScale(2, RoundingMode.HALF_UP);
        Connection conn = null;

        try {
            conn = new DatabaseConnection().getConnection();
            conn.setAutoCommit(false);

            String invoiceSql = """
                    SELECT InvoiceId, MerchantId, TotalAmount, AmountPaid, OutstandingBalance
                    FROM Invoices
                    WHERE OrderId = ?
                    FOR UPDATE
                    """;

            int invoiceId;
            int merchantId;
            BigDecimal invoiceTotal;
            BigDecimal currentAmountPaid;
            BigDecimal currentOutstandingBalance;

            try (PreparedStatement ps = conn.prepareStatement(invoiceSql)) {
                ps.setInt(1, orderId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        throw new IllegalArgumentException("No invoice exists for the selected order.");
                    }

                    invoiceId = rs.getInt("InvoiceId");
                    merchantId = rs.getInt("MerchantId");
                    invoiceTotal = normalizeCurrency(rs.getBigDecimal("TotalAmount"));
                    currentAmountPaid = normalizeCurrency(rs.getBigDecimal("AmountPaid"));

                    BigDecimal outstandingBalanceValue = rs.getBigDecimal("OutstandingBalance");
                    if (outstandingBalanceValue == null) {
                        currentOutstandingBalance = invoiceTotal.subtract(currentAmountPaid)
                                .max(BigDecimal.ZERO)
                                .setScale(2, RoundingMode.HALF_UP);
                    } else {
                        currentOutstandingBalance = normalizeCurrency(outstandingBalanceValue);
                    }
                }
            }

            if (currentOutstandingBalance.compareTo(BigDecimal.ZERO) == 0) {
                conn.rollback();
                throw new IllegalArgumentException("This invoice is already fully paid.");
            }

            if (normalizedPaymentAmount.compareTo(currentOutstandingBalance) > 0) {
                conn.rollback();
                throw new IllegalArgumentException("Payment amount cannot exceed the invoice outstanding balance.");
            }

            String merchantOutstandingSql = """
                    SELECT OutstandingBalance
                    FROM MerchantAccounts
                    WHERE MerchantId = ?
                    FOR UPDATE
                    """;

            BigDecimal merchantOutstandingBalance;

            try (PreparedStatement ps = conn.prepareStatement(merchantOutstandingSql)) {
                ps.setInt(1, merchantId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        throw new IllegalArgumentException("Merchant account not found.");
                    }

                    merchantOutstandingBalance = normalizeCurrency(rs.getBigDecimal("OutstandingBalance"));
                }
            }

            BigDecimal newAmountPaid = currentAmountPaid.add(normalizedPaymentAmount).setScale(2, RoundingMode.HALF_UP);
            BigDecimal newInvoiceOutstandingBalance = currentOutstandingBalance.subtract(normalizedPaymentAmount)
                    .max(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal newMerchantOutstandingBalance = merchantOutstandingBalance.subtract(normalizedPaymentAmount)
                    .max(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);

            String paymentStatus;
            if (newInvoiceOutstandingBalance.compareTo(BigDecimal.ZERO) == 0) {
                paymentStatus = "PAID";
            } else if (newAmountPaid.compareTo(BigDecimal.ZERO) > 0 && newAmountPaid.compareTo(invoiceTotal) < 0) {
                paymentStatus = "PARTIAL";
            } else {
                paymentStatus = "PENDING";
            }

            String insertPaymentSql = """
                    INSERT INTO Payments
                    (MerchantId, InvoiceId, PaymentDate, Amount, PaymentMethod, ReferenceNumber, RecordedByUserId)
                    VALUES (?, ?, CURDATE(), ?, ?, NULL, ?)
                    """;

            try (PreparedStatement ps = conn.prepareStatement(insertPaymentSql)) {
                ps.setInt(1, merchantId);
                ps.setInt(2, invoiceId);
                ps.setBigDecimal(3, normalizedPaymentAmount);
                ps.setString(4, paymentMethod);
                ps.setInt(5, recordedByUserId);
                ps.executeUpdate();
            }

            String updateInvoiceSql = """
                    UPDATE Invoices
                    SET AmountPaid = ?,
                        OutstandingBalance = ?,
                        PaymentStatus = ?
                    WHERE InvoiceId = ?
                    """;

            try (PreparedStatement ps = conn.prepareStatement(updateInvoiceSql)) {
                ps.setBigDecimal(1, newAmountPaid);
                ps.setBigDecimal(2, newInvoiceOutstandingBalance);
                ps.setString(3, paymentStatus);
                ps.setInt(4, invoiceId);
                ps.executeUpdate();
            }

            String updateMerchantSql = """
                    UPDATE MerchantAccounts
                    SET OutstandingBalance = ?
                    WHERE MerchantId = ?
                    """;

            try (PreparedStatement ps = conn.prepareStatement(updateMerchantSql)) {
                ps.setBigDecimal(1, newMerchantOutstandingBalance);
                ps.setInt(2, merchantId);
                ps.executeUpdate();
            }
            merchantStatusService.refreshMerchantStatus(conn, merchantId, LocalDate.now());
            conn.commit();

        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
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
                            rs.getString("Status")));
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
                            rs.getString("Status")));
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
                       COALESCE(i.TotalAmount, o.TotalAmount) AS TotalAmount,
                       o.Status AS DeliveredStatus,
                       COALESCE(i.PaymentStatus, 'N/A') AS PaidStatus,
                       COALESCE(o.CourierName, '') AS CourierName,
                       COALESCE(o.CourierReference, '') AS CourierRef,
                       COALESCE(DATE_FORMAT(o.ExpectedDeliveryDateTime, '%d/%m/%Y %H:%i'), '') AS ExpectedDelivery,
                       COALESCE(DATE_FORMAT(o.DeliveredDateTime, '%d/%m/%Y %H:%i'), '') AS DeliveryDate
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
                       COALESCE(i.TotalAmount, o.TotalAmount) AS TotalAmount,
                       o.Status AS DeliveredStatus,
                       COALESCE(i.PaymentStatus, 'N/A') AS PaidStatus,
                       COALESCE(o.CourierName, '') AS CourierName,
                       COALESCE(o.CourierReference, '') AS CourierRef,
                       COALESCE(DATE_FORMAT(o.ExpectedDeliveryDateTime, '%d/%m/%Y %H:%i'), '') AS ExpectedDelivery,
                       COALESCE(DATE_FORMAT(o.DeliveredDateTime, '%d/%m/%Y %H:%i'), '') AS DeliveryDate
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
    public boolean markOrderAsDelivered(int orderId, LocalDateTime deliveredDateTime) throws Exception {
        String sql = """
                UPDATE Orders
                SET Status = 'DELIVERED',
                    DeliveredDateTime = ?
                WHERE OrderId = ?
                  AND Status = 'DISPATCHED'
                """;

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(deliveredDateTime));
            ps.setInt(2, orderId);

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
                rs.getString("ExpectedDelivery"),
                rs.getString("DeliveryDate"));
    }

    private double calculateDiscountedTotal(Connection conn, int merchantId, double grossAmount) throws SQLException {
        DiscountTierMatch discountTier = findApplicableDiscountTier(conn, merchantId, grossAmount);
        if (discountTier == null || discountTier.discountPercent() <= 0) {
            return roundCurrency(grossAmount);
        }

        BigDecimal gross = BigDecimal.valueOf(grossAmount);
        BigDecimal discountRate = BigDecimal.valueOf(discountTier.discountPercent())
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

        return gross.multiply(BigDecimal.ONE.subtract(discountRate))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private DiscountTierMatch findApplicableDiscountTier(Connection conn, int merchantId, double orderAmount) throws SQLException {
        String activePlanSql = """
                SELECT DiscountPlanId
                FROM DiscountPlans
                WHERE MerchantId = ?
                  AND IsActive = 1
                ORDER BY DiscountPlanId DESC
                LIMIT 1
                """;

        Integer discountPlanId = null;

        try (PreparedStatement planPs = conn.prepareStatement(activePlanSql)) {
            planPs.setInt(1, merchantId);

            try (ResultSet planRs = planPs.executeQuery()) {
                if (planRs.next()) {
                    discountPlanId = planRs.getInt("DiscountPlanId");
                }
            }
        }

        if (discountPlanId == null) {
            return null;
        }

        String tiersSql = """
                SELECT MinOrderValue, MaxOrderValue, DiscountPercent
                FROM DiscountPlanTiers
                WHERE DiscountPlanId = ?
                ORDER BY MinOrderValue ASC, MaxOrderValue ASC
                """;

        try (PreparedStatement tiersPs = conn.prepareStatement(tiersSql)) {
            tiersPs.setInt(1, discountPlanId);

            try (ResultSet tiersRs = tiersPs.executeQuery()) {
                while (tiersRs.next()) {
                    double minOrderValue = tiersRs.getDouble("MinOrderValue");
                    Double maxOrderValue = tiersRs.getObject("MaxOrderValue") == null
                            ? null
                            : tiersRs.getDouble("MaxOrderValue");

                    boolean matchesTier = orderAmount >= minOrderValue
                            && (maxOrderValue == null || orderAmount <= maxOrderValue);

                    if (matchesTier) {
                        return new DiscountTierMatch(
                                minOrderValue,
                                maxOrderValue,
                                tiersRs.getDouble("DiscountPercent"));
                    }
                }
            }
        }
        return null;
    }

    private double roundCurrency(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private BigDecimal normalizeCurrency(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }

    private record DiscountTierMatch(double minOrderValue, Double maxOrderValue, double discountPercent) { }

    private record StockReduction(int itemId, int quantity) { }
}

