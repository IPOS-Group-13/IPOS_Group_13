package com.teesolutions.ipospu.integrations;

import com.teesolutions.ipospu.api.I_InventoryAPI;
import com.teesolutions.ipospu.dto.CartLineDto;
import com.teesolutions.ipospu.dto.InventoryItemDto;
import com.teesolutions.ipospu.dto.OnlineOrderRequest;
import com.teesolutions.ipospu.dto.OnlineOrderResult;
import com.teesolutions.ipospu.dto.OrderStatusDto;
import com.teesolutions.ipospu.dto.StockReservationResult;
import com.teesolutions.ipospu.utils.DatabaseManager;
import com.teesolutions.ipospu.utils.RetailPricing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class CaJdbcInventoryApiClient implements I_InventoryAPI {

    private static final String CA_SCHEMA = "ipos_ca";

    private static final String T_ORDER_HEADER = "pu_online_order";
    private static final String T_ORDER_LINE = "pu_online_order_line";

    private static final String COL_ORDER_ID = "order_id";
    private static final String COL_DELIVERY_ADDRESS = "delivery_address";
    private static final String COL_CUSTOMER_EMAIL = "customer_email";
    private static final String COL_ORDER_STATUS = "status";
    private static final String COL_CREATED_AT = "created_at";

    private static final String COL_LINE_ORDER_ID = "order_id";
    private static final String COL_LINE_PRODUCT_ID = "product_id";
    private static final String COL_LINE_QTY = "quantity";
    private static final String COL_LINE_UNIT_PRICE = "unit_price";
    private static final String COL_LINE_DISCOUNT_PCT = "discount_percent";

    private static final String T_INVENTORY = "Inventory";
    private static final String COL_INV_PRODUCT_ID = "ItemID";
    private static final String COL_INV_QTY = "Availability";

    private static final String SQL_GET_CATALOGUE = """
            SELECT CAST(`ItemID` AS CHAR) AS product_id,
                   `Description` AS product_name,
                   `Description` AS product_description,
                   `Package_cost` AS package_cost,
                   `Availability` AS stock_quantity
            FROM `ipos_ca`.`Inventory`
            WHERE `Availability` > 0
            ORDER BY `Description`
            """;

    private final RetailPricing retailPricing = new RetailPricing();

    private static String q(String ident) {
        return "`" + ident.replace("`", "``") + "`";
    }

    private static String fq(String schema, String table) {
        return q(schema) + "." + q(table);
    }

    @Override
    public List<InventoryItemDto> getCatalogue() {
        List<InventoryItemDto> out = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_GET_CATALOGUE);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                double retail = retailPricing.retailUnitPriceFromPackageCost(rs.getDouble("package_cost"));
                out.add(new InventoryItemDto(
                        rs.getString("product_id"),
                        rs.getString("product_name"),
                        rs.getString("product_description"),
                        retail,
                        rs.getInt("stock_quantity")
                ));
            }
            return out;
        } catch (SQLException e) {
            throw new IllegalStateException("CA getCatalogue failed", e);
        }
    }

    @Override
    public boolean checkStock(String productId, int requestedQty) {
        if (requestedQty <= 0) {
            return false;
        }
        String sql = "SELECT " + q(COL_INV_QTY) + " AS q FROM " + fq(CA_SCHEMA, T_INVENTORY)
                + " WHERE CAST(" + q(COL_INV_PRODUCT_ID) + " AS CHAR) = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("q") >= requestedQty;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("CA checkStock failed", e);
        }
    }

    @Override
    public StockReservationResult reserveOrReject(List<CartLineDto> lines) {
        List<String> bad = new ArrayList<>();
        for (CartLineDto line : lines) {
            if (!checkStock(line.getProductId(), line.getQuantity())) {
                bad.add(line.getProductId());
            }
        }
        return new StockReservationResult(bad.isEmpty(), bad);
    }

    @Override
    public OnlineOrderResult submitOnlineOrder(OnlineOrderRequest request) {
        if (request == null || request.getLines() == null || request.getLines().isEmpty()) {
            return new OnlineOrderResult(false, "Empty order");
        }
        String inv = fq(CA_SCHEMA, T_INVENTORY);
        String pid = q(COL_INV_PRODUCT_ID);
        String qtyCol = q(COL_INV_QTY);
        String deductSql = "UPDATE " + inv + " SET " + qtyCol + " = " + qtyCol + " - ? WHERE CAST(" + pid + " AS CHAR) = ? AND " + qtyCol + " >= ?";

        String hdr = fq(CA_SCHEMA, T_ORDER_HEADER);
        String insertHdr = "INSERT INTO " + hdr + " ("
                + q(COL_ORDER_ID) + ", " + q(COL_DELIVERY_ADDRESS) + ", "
                + q(COL_CUSTOMER_EMAIL) + ", " + q(COL_ORDER_STATUS) + ", " + q(COL_CREATED_AT)
                + ") VALUES (?,?,?,?,?)";

        String ln = fq(CA_SCHEMA, T_ORDER_LINE);
        String insertLine = "INSERT INTO " + ln + " ("
                + q(COL_LINE_ORDER_ID) + ", " + q(COL_LINE_PRODUCT_ID) + ", " + q(COL_LINE_QTY) + ", "
                + q(COL_LINE_UNIT_PRICE) + ", " + q(COL_LINE_DISCOUNT_PCT)
                + ") VALUES (?,?,?,?,?)";

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                for (CartLineDto line : request.getLines()) {
                    try (PreparedStatement ps = connection.prepareStatement(deductSql)) {
                        ps.setInt(1, line.getQuantity());
                        ps.setString(2, line.getProductId());
                        ps.setInt(3, line.getQuantity());
                        if (ps.executeUpdate() != 1) {
                            connection.rollback();
                            return new OnlineOrderResult(false, "Stock changed during checkout for " + line.getProductId());
                        }
                    }
                }

                try (PreparedStatement ps = connection.prepareStatement(insertHdr)) {
                    ps.setString(1, request.getOrderId());
                    ps.setString(2, request.getDeliveryAddress());
                    ps.setString(3, request.getCustomerEmail());
                    ps.setString(4, "RECEIVED");
                    ps.setTimestamp(5, Timestamp.from(Instant.now()));
                    ps.executeUpdate();
                }

                for (CartLineDto line : request.getLines()) {
                    try (PreparedStatement ps = connection.prepareStatement(insertLine)) {
                        ps.setString(1, request.getOrderId());
                        ps.setString(2, line.getProductId());
                        ps.setInt(3, line.getQuantity());
                        ps.setObject(4, null);
                        ps.setObject(5, null);
                        ps.executeUpdate();
                    }
                }

                connection.commit();
                return new OnlineOrderResult(true, "Order propagated to CA");
            } catch (SQLException ex) {
                connection.rollback();
                return new OnlineOrderResult(false, "CA order failed: " + ex.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return new OnlineOrderResult(false, "CA connection failure: " + e.getMessage());
        }
    }

    @Override
    public OrderStatusDto getOrderStatus(String orderId) {
        String sql = "SELECT " + q(COL_ORDER_STATUS) + " AS st FROM " + fq(CA_SCHEMA, T_ORDER_HEADER)
                + " WHERE " + q(COL_ORDER_ID) + " = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return new OrderStatusDto(orderId, "VOID");
                }
                return new OrderStatusDto(orderId, rs.getString("st"));
            }
        } catch (SQLException e) {
            System.err.println(
                    "[CA] getOrderStatus failed for " + orderId + " (check GRANT SELECT on ipos_ca.pu_online_order for db user): "
                            + e.getMessage());
            return new OrderStatusDto(orderId, "VOID");
        }
    }
}
