# Team 39 To Team 38 (CA)

This is the exact information Team 39 (`IPOS-PU`) is giving Team 38 (`IPOS-CA`) for **shared MySQL** integration, plus the exact information we need back.

## Important note about DB setup

This does **not** go against Aden's setup.

- We still use `db.properties.local`
- We still use the shared MySQL host / port / db / user
- We still keep passwords out of Git
- `db.init.mode=SHARED` only means: **PU should connect without auto-running schema/seed on the shared DB**

So this is the same connection method as Aden's, just safer for a shared database.

## What Team 39 gives Team 38

### 1. Business flow from PU

- PU shows catalogue items to customers.
- PU maintains cart, promotions, checkout, and tracking UI.
- PU processes payment **first** in PU.
- If payment succeeds, PU then sends the online order to CA.
- PU expects CA to become the source of truth for stock and order progression.

### 2. Data PU sends to CA after successful checkout

Order header:

| Field | Value from PU | Notes |
|------|----------------|-------|
| `order_id` | `ORD-XXXXXXXX` | PU currently generates this. We need CA to confirm whether CA will keep this id as the shared id. |
| `delivery_address` | Customer-entered text | Required in PU before checkout succeeds. |
| `customer_email` | Logged-in member email or guest checkout email | Optional if CA wants to store it. |
| `status` | `RECEIVED` | PU creates new orders in this state. |
| `created_at` | Current timestamp | PU can provide, or CA can stamp its own time. |

Order lines:

| Field | Value from PU | Notes |
|------|----------------|-------|
| `order_id` | Same as header | Must link to header row. |
| `product_id` | PDF-aligned product id, e.g. `10000001` | Must exactly match CA catalogue ids. |
| `quantity` | Integer | Same stock unit CA uses. |
| `unit_price` | PU line unit price | Optional, depending on how CA wants to store the order. |
| `discount_percent` | PU campaign discount | Optional, depending on CA design. |

### 3. IDs and statuses Team 39 is already using

Product ids in PU are aligned to the PDF sample data and stored without spaces, for example:

- `10000001` = Paracetamol
- `10000002` = Aspirin
- `10000003` = Analgin
- `10000004` = Celebrex 100 mg
- `10000006` = Retin-A
- `30000001` = Ospen
- `40000001` = Vitamin C

Order status vocabulary in PU:

- `RECEIVED`
- `DISPATCHED`
- `DELIVERED`
- `VOID`

### 4. What Team 39 needs Team 38 to provide back

Please fill in and return:

#### A. Catalogue source for PU

| Question | Team 38 answer |
|---------|-----------------|
| Table/view name PU should read from | |
| Column for product id | |
| Column for product name | |
| Column for description | |
| Column for retail price | _(PU default: read **`Package_cost`**, apply **`RetailPricing`** / `app_config` markup + VAT — no extra CA column.)_ |
| Column for stock quantity | |
| Column for active/inactive status (if any) | |

#### B. Online order handoff from PU to CA

| Question | Team 38 answer |
|---------|-----------------|
| Table name for order header | |
| Table name for order lines | |
| Is `order_id` from PU accepted as the shared id? | |
| If not, what id does CA generate and where should PU read/store it? | |
| Which columns are required on insert? | |
| Does CA want `customer_email` stored? | |
| Does CA want `unit_price` stored? | |
| Does CA want `discount_percent` stored? | |

#### C. Status tracking

| Question | Team 38 answer |
|---------|-----------------|
| Where should PU read order status from? | |
| Which column contains the status? | |
| Which column matches the order id? | |
| Does CA update status manually, by trigger, or by process? | |

#### D. Permissions needed for `pu_user`

| Permission | Needed? | Team 38 answer |
|-----------|---------|----------------|
| `SELECT` on catalogue object(s) | Yes | |
| `INSERT` on CA online-order object(s) | Yes | |
| `SELECT` on status object(s) | Yes | |
| `UPDATE` on any CA object | Only if explicitly required | |

## What Team 38 needs to do

1. Confirm one canonical product id scheme with Team 39.
2. Create or expose the CA-side table/view(s) PU should use.
3. Grant the minimum required rights to the PU DB user.
4. Confirm how CA handles:
   - stock check
   - stock deduction
   - order lifecycle updates
5. Run one shared integration test with Team 39.

## Definition of done for CA integration

CA integration is complete when all of the below are true:

- PU reads catalogue from the CA-agreed object, not only PU's local mock source.
- A successful PU checkout creates rows in the CA-agreed order object(s).
- CA stock is reduced or reserved according to the agreed design.
- PU can read the current status for a shared order from CA data.
- All agreed product ids match between PU campaigns/cart and CA catalogue/stock.
