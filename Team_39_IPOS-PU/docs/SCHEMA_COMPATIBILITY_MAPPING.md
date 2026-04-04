# Schema Compatibility Mapping (Team 39 Local -> Shared Group DB)

This document is a migration worksheet to fill in once Aden shares final schema details.

## 1) Connection Metadata (To Fill)

- Shared host:
- Shared port:
- Team 39 db/schema name:
- SSL required (`yes/no`):
- Team 39 username:
- Team 39 access level:

## 2) Table Mapping Matrix

| Local Table (`IPOS-PU`) | Local Purpose | Shared Object Name | Direction | Required Permissions | Blocker? |
|---|---|---|---|---|---|
| `users` | PU auth and member state | _TBD_ | PU-owned | `SELECT/INSERT/UPDATE` | No |
| `commercial_applications` | Commercial handoff queue to SA | _TBD_ | PU -> SA | `INSERT` (+ optional `SELECT`) | No |
| `products` | Catalogue for PU display | _TBD_ | CA -> PU (read) | `SELECT` | No |
| `orders` | PU order header | _TBD_ | PU-owned / shared read | `SELECT/INSERT/UPDATE` | No |
| `order_items` | PU order lines | _TBD_ | PU-owned / shared read | `SELECT/INSERT` | No |
| `payments` | Payment simulation/audit | _TBD_ | PU-owned | `SELECT/INSERT` | No |
| `campaigns` | Promo campaign definition | _TBD_ | PU-owned | `SELECT/INSERT/UPDATE` | No |
| `campaign_items` | Promo item discount map | _TBD_ | PU-owned | `SELECT/INSERT` | No |
| `campaign_metrics` | Hits/add/purchased counters | _TBD_ | PU-owned | `SELECT/INSERT/UPDATE` | No |
| `email_outbox` | SMTP simulation outbox | _TBD_ | PU-owned | `SELECT/INSERT` | No |
| `app_config` | Config values | _TBD_ | PU-owned | `SELECT/INSERT/UPDATE` | No |

## 3) Column-Level Mapping (Integration-Critical Only)

### A) Commercial Application Handoff

| Local Column | Type | Null? | Default | Shared Column | Notes |
|---|---|---|---|---|---|
| `company_registration_number` | `VARCHAR(100)` | No | - | _TBD_ | must match SA validation |
| `director_name` | `VARCHAR(255)` | No | - | _TBD_ | |
| `business_type` | `VARCHAR(255)` | No | - | _TBD_ | |
| `address` | `VARCHAR(500)` | No | - | _TBD_ | |
| `email` | `VARCHAR(255)` | No | - | _TBD_ | |
| `submitted_at` | `TIMESTAMP` | No | `CURRENT_TIMESTAMP` | _TBD_ | timezone alignment |
| `submission_status` | `VARCHAR(64)` | No | `SUBMITTED_TO_SA` | _TBD_ | status enum alignment |

### B) Inventory/Stock Integration

| Local Column | Type | Null? | Default | Shared Column | Notes |
|---|---|---|---|---|---|
| `products.id` | `VARCHAR(64)` | No | - | _TBD_ | stable product id |
| `products.retail_price` | `DECIMAL(12,2)` | No | - | _TBD_ | price source should be CA |
| `products.stock_quantity` | `INT` | No | - | _TBD_ | final stock check target |
| `products.is_active` | `BOOLEAN` | No | `TRUE` | _TBD_ | display logic |

### C) Order Status Tracking

| Local Column | Type | Null? | Default | Shared Column | Notes |
|---|---|---|---|---|---|
| `orders.id` | `VARCHAR(64)` | No | - | _TBD_ | unique per order |
| `orders.status` | `VARCHAR(32)` | No | - | _TBD_ | must support lifecycle states |
| `orders.tracking_code` | `VARCHAR(128)` | Yes | `NULL` | _TBD_ | may be set by external subsystem |
| `orders.created_at` | `TIMESTAMP` | No | `CURRENT_TIMESTAMP` | _TBD_ | |

## 4) Assumption Register (Review With Group)

- Money precision assumption: `DECIMAL(12,2)` for totals and unit prices.
- Discount precision assumption: `DECIMAL(5,2)` for percentages.
- Timestamp assumption: all timestamps stored in server timezone with explicit JDBC timezone parameter.
- String length assumption: IDs fit `VARCHAR(64)`.
- Promo metric counters are non-negative integers.

## 5) Migration Decision Log

- Date:
- Change made:
- Reason:
- Impacted files (repositories/scripts):
