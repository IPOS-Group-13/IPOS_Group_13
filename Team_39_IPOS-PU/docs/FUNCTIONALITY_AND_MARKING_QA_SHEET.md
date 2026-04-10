# IPOS-PU — functionality explainer & marking Q&A sheet

Use this when lecturers or teammates ask **how** features work, **where** they live in code, and **how** that ties to **IPOS_SampleData_2026.pdf** scenarios (especially 17–20 and PU logins).

**Companion docs:** [PROJECT_STATUS_AND_RUNBOOK.md](PROJECT_STATUS_AND_RUNBOOK.md) (progress + runbook), [DEMO_CHEATSHEET_ONE_PAGE.md](DEMO_CHEATSHEET_ONE_PAGE.md) (click path + SQL).

---

## 1. How to use this sheet

- **“Acceptable answer”** = honest, accurate, and scoped: say what PU does, name the main class/table, and admit what is **mocked** or **out of scope** (CA/SA apps).
- If asked for **detail you do not know**, say: *“That lives in Team 37/38’s subsystem; PU calls it through `I_InventoryAPI` / `I_MemberAPI` and we will wire real adapters once they confirm table names and grants.”*
- **PDF scenarios 1–16** are largely **not** PU-only; point to the integration plan after mocks are replaced: [SHARED_DB_INTEGRATION_TEST_PLAN.md](SHARED_DB_INTEGRATION_TEST_PLAN.md).

---

## 2. Architecture in one minute

| Layer | Role | Main packages / types |
|-------|------|-------------------------|
| **UI** | JavaFX screens, buttons, tables | `Main.java` loads `views/MainView.fxml`; `LoginController`, `PortalController` |
| **State** | Who is logged in | `SessionState` (per login flow); `PortalController` holds `currentUser` / guest flag |
| **Services** | Business rules | `AuthService`, `CatalogService`, `CampaignService`, `OrderService`, `PaymentService`, `CommsService`, `ReportService` |
| **Repositories** | JDBC SQL | `UserRepository`, `ProductRepository`, `OrderRepository`, `CampaignRepository`, `PaymentRepository`, `CommsRepository`, `CommercialApplicationRepository` |
| **Integrations** | Cross-team boundaries (today: mocks) | `MockInventoryApiClient` (`I_InventoryAPI`), `MockMemberApiClient` (`I_MemberAPI`) |
| **Bootstrap** | DB connection + optional schema/seed | `DatabaseManager` reads `db.properties.local`; `LOCAL` runs `schema.sql` + `seed.sql`, `SHARED` connects only |

**Startup:** `Main.main` calls `DatabaseManager.getConnection()` before `Application.launch`, so the DB is ready when the UI opens.

---

## 3. Types of questions — answer patterns

| If they ask… | Good structure for your answer |
|--------------|--------------------------------|
| *“Walk through checkout.”* | Validate cart → reserve stock (inventory API) → validate card (`PaymentService`) → if pay OK, propagate stock deduction (mock CA) → insert `orders` / `order_items` → save `payments` → increment loyalty count → write `email_outbox`. |
| *“Where is the PDF sample data?”* | Mostly in `db/seed.sql`: users (`login_alias`, passwords), Cosymed products (`10000001`…), March/April campaigns, Pond Pharmacy commercial row, `completed_order_count` for PU0001 scenario 20. |
| *“Is email real?”* | No — `CommsService` + `CommsRepository` **simulate** email by inserting into `email_outbox`; body explains this for demos. |
| *“Does stock sync with CA?”* | Today **no real CA**: `MockInventoryApiClient` updates PU’s own `products` table in a transaction; a future adapter would hit CA’s tables or API. |
| *“How do you hash passwords?”* | `PasswordUtil` uses **SHA-256** hex of the password. `UserRepository.authenticate` also accepts a **plain-text match** against `password_hash` so seeded PDF passwords work without pre-hashing. |

---

## 4. Feature → code → PDF / demo — Q&A

### 4.1 Login (email or PDF username)

- **What it does:** User enters identifier + password; admins get the Admin tab; members see profile and order history.
- **How it is coded:** `LoginController` → `AuthService.login` → `UserRepository.authenticate` SQL matches `email = ?` **or** `login_alias = ?` (when column exists). Password checked with `PasswordUtil.hash` or raw equality for seed data.
- **PDF / scenarios:** `sysdba`, `manager`, `PU0001`, `PU0002` via `login_alias`; same users also have email addresses in seed.
- **Likely Q:** *“Why both email and alias?”*  
  **A:** *The PDF uses short usernames; we store canonical email in `users.email` and map PDF-style ids in `login_alias` for a familiar login experience.*

### 4.2 Catalogue & search

- **What it does:** Lists active products; keyword filter.
- **How it is coded:** `CatalogService.search` → `ProductRepository.findActiveProducts`; promotions merge via `CampaignRepository.getActiveDiscountByProduct` for cart line discounts.
- **PDF / pricing:** Product ids align with Cosymed catalogue (`10000001`, …). **`products.retail_price`** is the **customer** unit price: PDF **package cost** with **100%** markup and **0%** VAT (2× cost) via `app_config` keys `retail_markup_percent` and `vat_rate`. Stock matches PDF availability. **`RetailPricing`** applies the same rule when reading CA **`Package_cost`** at integration time.

### 4.3 Promotions / campaigns (scenarios 17–18)

- **What it does:** Time-bounded campaigns with per-product discount %; Promotions tab; admin CRUD.
- **How it is coded:** `CampaignService` parses admin item spec, checks **overlap** (`isProductInOverlappingCampaign`), persists via `CampaignRepository`. Engagement counters: `recordPromotionsView`, `recordItemAdded`, `recordItemPurchased`.
- **PDF:** Seeded **March Promotion** and **April Promotion** with PDF-style products and windows.
- **Likely Q:** *“How is line price calculated?”*  
  **A:** *`CartItem.getLineTotal()` = `quantity × unitPrice × (1 − discountPercent/100)`; loyalty (below) applies to the **cart total** after line totals.*

### 4.4 Cart & checkout (guest or member; scenario 19 style)

- **What it does:** Guest can checkout with email + address + card; member uses account email.
- **How it is coded:** `OrderService.checkout`: builds `CartLineDto` list → `inventoryApiClient.reserveOrReject` → `calculateCartTotal` (loyalty) → `PaymentService.processPayment` → on success `inventoryApiClient.submitOnlineOrder` → `OrderRepository.createOrderWithItems` → `PaymentRepository.savePayment` → `CampaignService.recordItemPurchased` → optional `AuthService.incrementCompletedOrders` → `CommsService.sendOrderConfirmation`.
- **Guest user id:** `AuthService.ensureGuestCheckoutUser` ensures a hidden `GUEST` row (`guest.checkout@ipos.local`) for FK integrity on `orders`.
- **Likely Q:** *“What if stock runs out between browsing and pay?”*  
  **A:** *We run a final `reserveOrReject` before payment; if it fails, checkout aborts with a message and optional list of product ids.*

### 4.5 Payment (simulated)

- **What it does:** Validates card type, first/last four digits, expiry `MM/yy` not in the past; generates a fake transaction id on success.
- **How it is coded:** `PaymentService` implements `I_PaymentAPI`; no external PSP.
- **PDF:** Supports “valid card + future expiry” style demos (e.g. Peter-style checkout narrative) without charging real cards.

### 4.6 Loyalty — 10th order 10% (scenario 20)

- **What it does:** **Non-commercial** members get **10% off the order total** when the order they are placing is their **10th, 20th, …** completed order (i.e. when `completed_order_count + 1` is divisible by 10 **before** this checkout completes — eligibility checked **at checkout time** using the count **before** increment).
- **How it is coded:** `User.isEligibleForLoyaltyDiscount()` in `User.java`; `OrderService.calculateCartTotal` multiplies total by `0.90` when true; after successful checkout, `incrementCompletedOrders` updates DB and `PortalController` bumps in-memory count.
- **PDF / seed:** PU0001 (`cool@example.com`) is seeded with `completed_order_count = 8` so the **next** completed order is the **9th** (no loyalty); the **10th** gets the discount.
- **Likely Q:** *“Is it 10% per line or on total?”*  
  **A:** *On the **subtotal after promotional line discounts**, applied once in `calculateCartTotal`.*

### 4.7 Order tracking

- **What it does:** Lookup by customer email + tracking code; shows order row from PU DB.
- **How it is coded:** `OrderService.findOrderByTracking` → `OrderRepository`; confirmation text stored in `email_outbox` with tracking code.
- **Limitation:** Status is written as `RECEIVED` at creation; `MockInventoryApiClient.getOrderStatus` always returns `RECEIVED` until CA integration supplies a real lifecycle.

### 4.8 Communications (email simulation)

- **What it does:** Registration and order confirmation “emails” are rows in `email_outbox`.
- **How it is coded:** `CommsService` implements `I_CommsAPI`; `CommsRepository.saveOutboundEmail` with purpose e.g. `ORDER_CONFIRMATION`, `REGISTRATION`.

### 4.9 Commercial membership application (PU0003 / Pond sample)

- **What it does:** Validates company number (UK-style patterns), director name, business type, address, email; rejects if email already has a member account; submits to persistence.
- **How it is coded:** `AuthService.validateCommercialApplication` → `PortalController` calls `MockMemberApiClient.submitCommercialApplication` → `CommercialApplicationRepository.save` → table `commercial_applications`.
- **PDF:** Pond Pharmacy–style row can be shown in SQL; **PU0003 is not necessarily a login account** unless the module is extended — today it is an **application record** path aligned with handoff to SA.

### 4.10 Admin & reports

- **What it does:** Campaign management; reports (sales, campaign engagement, etc.) with print support from UI.
- **How it is coded:** `CampaignService` + admin sections in `PortalController`; `ReportService` + `ReportRepository` for aggregates.

### 4.11 Database bootstrap & shared DB

- **What it does:** `LOCAL`: apply `schema.sql`, ensure `login_alias` column on old DBs, run `seed.sql`. `SHARED`: connect only — **no** auto seed (safe for Railway / team MySQL).
- **How it is coded:** `DatabaseManager.initializeSchemaIfNeeded`; config from `db.properties.local` and optional `IPOS_DB_*` env vars (see runbook).

---

## 5. Quick “scenario → what to say” (PDF-aligned)

| Scenario area | One sentence you can say |
|---------------|---------------------------|
| **17 March / 18 April promos** | *“Campaigns and line discounts are loaded from our MySQL seed and enforced in the cart via `CartItem` line maths and `CampaignRepository` active windows.”* |
| **19 Guest + promos + pay** | *“Guest checkout uses a hidden guest user id for FKs, validates payment in `PaymentService`, then writes orders, payments, and a simulated confirmation in `email_outbox`.”* |
| **20 Loyalty** | *“Loyalty is implemented in `User.isEligibleForLoyaltyDiscount` using `completed_order_count`; seed sets PU0001 to 8 so the marker can see 9th vs 10th order behaviour.”* |
| **PU0003 / commercial** | *“We validate intake in `AuthService` and persist via `CommercialApplicationRepository`; full SA workflow waits on Team 37’s schema and grants.”* |

---

## 6. Honest boundaries (good answers if pressed)

- **CA shop sales, SA merchant ledgers, debtors, reminders (PDF 1–16, 7, etc.):** *“Those belong to other subsystems’ databases and UIs; PU is the public storefront slice.”*
- **Real SMTP / real payment capture:** *“Out of scope for this coursework build; we model audit tables and validation instead.”*
- **Why mocks?** *“Interfaces `I_InventoryAPI` and `I_MemberAPI` isolate PU from CA/SA until table names and grants are agreed; `MockInventoryApiClient` still performs real stock deduction on **our** `products` table to keep demos consistent.”*

---

## 7. Class index (where to look in the IDE)

| Topic | Primary classes |
|-------|------------------|
| App entry | `Main.java` |
| DB lifecycle | `DatabaseManager.java` |
| Login | `LoginController.java`, `AuthService.java`, `UserRepository.java` |
| Main UI / cart / admin | `PortalController.java` |
| Checkout pipeline | `OrderService.java` |
| Payment rules | `PaymentService.java` |
| Promotions | `CampaignService.java`, `CampaignRepository.java` |
| Catalogue | `CatalogService.java`, `ProductRepository.java` |
| Email simulation | `CommsService.java`, `CommsRepository.java` |
| CA boundary (mock) | `MockInventoryApiClient.java`, `I_InventoryAPI.java` |
| SA boundary (mock) | `MockMemberApiClient.java`, `I_MemberAPI.java` |
| Loyalty rule | `User.java` |
| Sample data | `src/main/resources/db/seed.sql`, `schema.sql` |

---

*Team 39 (IPOS-PU). Keep this in sync when you replace mocks or change loyalty/payment rules.*
