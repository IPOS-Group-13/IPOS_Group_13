# CA inventory API — JDBC client (replaces `MockInventoryApiClient`)

Team **38 (CA)** owns **catalogue + stock +** how **online orders from PU** land in **`ipos_ca`**.  
This document mirrors the SA intake flow: **placeholders → CA fills names → DBA `GRANT`s → PU wires `CaJdbcInventoryApiClient`**.

**Contract:** `docs/GROUP_API_CONTRACT_TEAM37_38_39.md` — `I_InventoryAPI`.

---

## 0) Order of work (checklist)

| Step | Who | What |
|------|-----|------|
| 0a | **CA** | Confirm **real** table/view names + column names (Workbench / `information_schema`). |
| 0b | **CA** | If **no** PU order tables exist, run **§4 SQL** (or equivalent they prefer). |
| 0c | **DBA** | Run **§5 `GRANT`s** for `'pu_user'@'%'` (you already use this user). |
| 0d | **PU** | Replace placeholders in **`CaJdbcInventoryApiClient`** (§6) and copy to `src/.../integrations/`. |
| 0e | **PU** | In **`OrderService`**, use `I_InventoryAPI inventoryApiClient = new CaJdbcInventoryApiClient();` instead of `MockInventoryApiClient`. |
| 0f | **PU** | **`db.init.mode=SHARED`** on shared DB; test checkout → rows in CA order tables + stock down. |
| 0g | **PU (optional but full shop parity)** | Today **`CatalogService`** uses **`ProductRepository`** (`ipos_pu.products`) for search. For **100% CA catalogue**, also route browse/search through CA (e.g. call `getCatalogue()` or a CA query) — **separate small change** after checkout works. |

---

## 1) Which CA objects PU needs (names come from CA)

From your Workbench tree, **likely** (CA must confirm exact spelling/case):

| PU method | Typical CA objects | Purpose |
|-----------|-------------------|--------|
| `getCatalogue()` | **`Catalogue`** + **`Inventory`** (join or view) | Same logical fields as PU `products`: id, name, description, price, stock qty; only **active** / in-stock rows if CA models that. |
| `checkStock` / `reserveOrReject` | **Quantity** on **`Inventory`** (or via join) | Read `stock_quantity >= requested`. |
| `submitOnlineOrder` | **Order header + line** tables CA designates | After payment, PU sends `orderId` (`ORD-…`), `deliveryAddress`, lines (`productId`, `qty`). Optional: `customer_email`, `unit_price`, `discount_percent` if CA columns exist. |
| `getOrderStatus` | **Same header** (or view) | Read status for contract lifecycle: `RECEIVED`, `DISPATCHED`, `DELIVERED`, `VOID`. |

If CA already has different names, PU only changes **constants / SQL strings** in the client — no contract change.

---

## 1b) Pricing — no extra CA column needed (default until the group agrees otherwise)

You do **not** need a separate legal-style contract file for coursework. A short **written agreement** (email / Teams / WhatsApp + this section) is enough, so nobody assumes the wrong thing about `Package_cost`.

**Facts today**

- CA **`Inventory.Package_cost`** matches the PDF catalogue column **“Package cost, £”** (unit cost per pack).
- **IPOS_SampleData_2026.pdf** also states: *retail = 100% markup on unit cost; 0.0% VAT on retail* → **selling price = 2 × package cost**.

**Default integration rule (recommended)**

1. **CA** keeps a single column **`Package_cost`** = PDF package cost (no change required).
2. **PU** treats that value as **unit cost** and applies **module retail** in Java via **`RetailPricing`** (`com.teesolutions.ipospu.utils`): reads **`app_config.retail_markup_percent`** (default **100**) and **`app_config.vat_rate`** (default **0** for the sample sheet). Formula: `packageCost × (1 + markup/100) × (1 + vat/100)`.
3. **`pu_online_order_line.unit_price`** should be the **actual price paid per line** (cart unit price after promos), **not** raw `Package_cost`, unless CA explicitly asks for cost on the line.

**Implementation in this repo**

- **`RetailPricing`** + **`AppConfigRepository`** — use in **`CaJdbcInventoryApiClient.getCatalogue()`** when mapping rows (see §6). Local **`products.retail_price`** in **`seed.sql`** is seeded to the **same customer prices** (2× PDF package cost at default config).

**If CA adds `Retail_price` or a view later**  
PU can switch to reading that instead of **`RetailPricing`** on `Package_cost`.

**One message you can send CA**

> We’re treating **`Package_cost`** as the PDF **package cost** (unit cost). Per **IPOS_SampleData**, **online retail = 2×** that, 0% VAT. We’ll send **`unit_price`** on order lines as the **customer price** (after that rule and promos). Shout if you want a different column or formula on your side.

---

## 2) Column alias contract for JDBC (CA SQL must match)

PU maps `ResultSet` using these **aliases** (CA’s `SELECT` should use `AS`):

| Alias in `ResultSet` | `InventoryItemDto` field |
|---------------------|---------------------------|
| `product_id` | product id (string, PDF style e.g. `10000001`) |
| `product_name` | name |
| `product_description` | description |
| `retail_price` | customer-facing price in **`InventoryItemDto`** — from CA **or** computed with **`RetailPricing`** on **`Package_cost`** (§1b) |
| `stock_quantity` | qty (int) — use **`Availability`** (CA: decreases on sale; not `Stock_limit`) |

**JDBC catalogue query:** Prefer exposing CA **`Package_cost`** as alias **`package_cost`** and running it through **`RetailPricing`** in Java (keeps one source of truth with **`app_config`**). Example:

```sql
SELECT ItemID AS product_id,
       Description AS product_name,
       Description AS product_description,
       Package_cost AS package_cost,
       Availability AS stock_quantity
FROM ipos_ca.Inventory;
```

CA can change physical column names; **aliases** in the query must match the table above (or you edit the Java getters / `RetailPricing` call).

---

## 3) `GRANT` template (after CA confirms objects)

Run as **admin**. Replace table/view names if CA uses different ones.

```sql
-- Catalogue / stock read (adjust object names)
GRANT SELECT ON ipos_ca.`Catalogue` TO 'pu_user'@'%';
GRANT SELECT ON ipos_ca.`Inventory` TO 'pu_user'@'%';

-- If PU deducts stock on submitOnlineOrder (same as mock today) — confirm with CA before granting:
GRANT SELECT, UPDATE ON ipos_ca.`Inventory` TO 'pu_user'@'%';

-- PU order handoff (use final names; examples from §4)
GRANT INSERT, SELECT ON ipos_ca.`pu_online_order` TO 'pu_user'@'%';
GRANT INSERT, SELECT ON ipos_ca.`pu_online_order_line` TO 'pu_user'@'%';

FLUSH PRIVILEGES;
```

If CA exposes a **single view** for catalogue instead of two tables, replace the first two lines with one `GRANT SELECT ON ipos_ca.`view_name``.

---

## 4) SQL for CA to run **if** PU order tables do not exist yet

Aligned with **`docs/TEAM39_TO_CA_HANDOFF.md`** (header + lines). CA may rename; if they do, PU updates Java constants.

```sql
-- PU → CA online order handoff (Team 39 checkout after payment)
CREATE TABLE IF NOT EXISTS ipos_ca.pu_online_order (
    order_id VARCHAR(64) NOT NULL,
    delivery_address VARCHAR(500) NOT NULL,
    customer_email VARCHAR(255) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'RECEIVED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (order_id),
    KEY idx_pu_online_order_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS ipos_ca.pu_online_order_line (
    id INT NOT NULL AUTO_INCREMENT,
    order_id VARCHAR(64) NOT NULL,
    product_id VARCHAR(64) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NULL,
    discount_percent DECIMAL(5,2) NULL,
    PRIMARY KEY (id),
    KEY idx_pu_online_order_line_order (order_id),
    CONSTRAINT fk_pu_online_order_line_header
        FOREIGN KEY (order_id) REFERENCES ipos_ca.pu_online_order (order_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Note:** If `Catalogue` / `Inventory` use different PK/FK rules, CA keeps those as-is; this block is **only** for PU-posted orders.

---

## 5) Minimal `GRANT` block for **only** the new order tables (if §3 is too broad)

```sql
GRANT INSERT, SELECT ON ipos_ca.pu_online_order TO 'pu_user'@'%';
GRANT INSERT, SELECT ON ipos_ca.pu_online_order_line TO 'pu_user'@'%';
FLUSH PRIVILEGES;
```

(Still add **`Catalogue` / `Inventory`** grants when you wire catalogue + stock.)

---

## 6) Java — `CaJdbcInventoryApiClient` (copy to `integrations/` when ready)

**Before use:** replace every `<<…>>` and the **`SQL_GET_CATALOGUE`** string with CA’s real `SELECT` (with aliases from §2).  
**Transaction:** `submitOnlineOrder` uses one connection + manual commit like the mock.

```java
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

/**
 * Team 38 (CA) inventory + PU order handoff via shared MySQL.
 * Replace <<PLACEHOLDERS>> and SQL_GET_CATALOGUE after CA confirms DDL.
 */
public class CaJdbcInventoryApiClient implements I_InventoryAPI {

    private static final String CA_SCHEMA = "<<CA_SCHEMA>>"; // e.g. ipos_ca

    // Order tables (match §4 or CA’s final names)
    private static final String T_ORDER_HEADER = "<<T_ORDER_HEADER>>"; // e.g. pu_online_order
    private static final String T_ORDER_LINE = "<<T_ORDER_LINE>>";   // e.g. pu_online_order_line

    private static final String COL_ORDER_ID = "<<COL_ORDER_ID>>";           // e.g. order_id
    private static final String COL_DELIVERY_ADDRESS = "<<COL_DELIVERY_ADDRESS>>";
    private static final String COL_CUSTOMER_EMAIL = "<<COL_CUSTOMER_EMAIL>>"; // nullable — use NULL insert if unused
    private static final String COL_ORDER_STATUS = "<<COL_ORDER_STATUS>>";   // e.g. status
    private static final String COL_CREATED_AT = "<<COL_CREATED_AT>>";      // e.g. created_at

    private static final String COL_LINE_ORDER_ID = "<<COL_LINE_ORDER_ID>>";
    private static final String COL_LINE_PRODUCT_ID = "<<COL_LINE_PRODUCT_ID>>";
    private static final String COL_LINE_QTY = "<<COL_LINE_QTY>>";
    private static final String COL_LINE_UNIT_PRICE = "<<COL_LINE_UNIT_PRICE>>";       // optional
    private static final String COL_LINE_DISCOUNT_PCT = "<<COL_LINE_DISCOUNT_PCT>>"; // optional

    // Inventory: CA must confirm column for product key and quantity (deduct pattern like PU products table)
    private static final String T_INVENTORY = "<<T_INVENTORY>>";           // e.g. Inventory
    private static final String COL_INV_PRODUCT_ID = "<<COL_INV_PRODUCT_ID>>";
    private static final String COL_INV_QTY = "<<COL_INV_QTY>>";

    /**
     * SELECT must expose: product_id, product_name, product_description, package_cost (CA Package_cost), stock_quantity (Availability).
     * Customer retail is computed in Java via RetailPricing (app_config retail_markup_percent + vat_rate).
     */
    private static final String SQL_GET_CATALOGUE = "<<REPLACE_WITH_FULL_SELECT_FROM_CA>>";

    private final RetailPricing retailPricing = new RetailPricing();

    private static String q(String ident) {
        if (ident == null || ident.isBlank() || ident.startsWith("<<")) {
            throw new IllegalStateException("Replace CA JDBC placeholders before use: " + ident);
        }
        return "`" + ident.replace("`", "``") + "`";
    }

    private static String fq(String schema, String table) {
        return q(schema) + "." + q(table);
    }

    @Override
    public List<InventoryItemDto> getCatalogue() {
        if (SQL_GET_CATALOGUE.startsWith("<<")) {
            throw new IllegalStateException("Set SQL_GET_CATALOGUE from Team CA");
        }
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
                + " WHERE " + q(COL_INV_PRODUCT_ID) + " = ?";
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
        String deductSql = "UPDATE " + inv + " SET " + qtyCol + " = " + qtyCol + " - ? WHERE " + pid + " = ? AND " + qtyCol + " >= ?";

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
                    ps.setString(3, null); // TODO: pass guest/member email from OrderService if CA wants it
                    ps.setString(4, "RECEIVED");
                    ps.setTimestamp(5, Timestamp.from(Instant.now()));
                    ps.executeUpdate();
                }

                for (CartLineDto line : request.getLines()) {
                    try (PreparedStatement ps = connection.prepareStatement(insertLine)) {
                        ps.setString(1, request.getOrderId());
                        ps.setString(2, line.getProductId());
                        ps.setInt(3, line.getQuantity());
                        ps.setObject(4, null); // optional: unit price from cart
                        ps.setObject(5, null); // optional: discount %
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
            return new OrderStatusDto(orderId, "VOID");
        }
    }
}
```

**TODO for PU after CA answers:**  
- Set **`SQL_GET_CATALOGUE`** to CA’s query.  
- If CA **does not** want PU to **`UPDATE Inventory`**, remove the deduct loop and agree CA deducts via trigger/job — then drop **`UPDATE`** from `GRANT` and adjust `submitOnlineOrder`.  
- Pass **`customer_email`** into `OnlineOrderRequest` or extend the flow if CA requires it (today DTO has only order id, address, lines).

---

## 7) After all steps — “integrated with CA?”

Same rule as SA:

- Table(s) exist, **`GRANT`** correct, **`db.inventory.api=ca`** in **`db.properties.local`** (or **`IPOS_INVENTORY_API=ca`**), **`db.init.mode=SHARED`** on the shared server, **`OrderService`** uses **`InventoryApiFactory`** → **`CaJdbcInventoryApiClient`**, **one successful checkout** with stock moving in CA + order rows visible in **`ipos_ca`**.

**Note:** When **`db.inventory.api=ca`**, **`CatalogService`** uses **`I_InventoryAPI.getCatalogue()`** (same CA **`Inventory`** rows as checkout). With **`mock`**, the shop still uses **`ProductRepository`** (`ipos_pu.products`).

---

## Related docs

- `docs/TEAM39_TO_CA_HANDOFF.md`  
- `docs/GROUP_SHARED_DB_CONTRACT_TEAM39.md`  
- `docs/sql/pu_integration_grants_TEMPLATE.sql`  
