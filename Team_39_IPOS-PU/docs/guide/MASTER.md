# IPOS-PU — master reference (Team 39)

Single document for **what this subsystem is**, **how it was built**, **how to run it**, and **how it connects to CA/SA**. Older handoff PDFs, runbooks, and duplicate markdown files were removed to reduce clutter; this file replaces them.

---

## 1. Purpose

**IPOS-PU** is the **public JavaFX portal**: catalogue (from CA inventory), promotions, cart/checkout, simulated card payment, order tracking, non-commercial registration and login, commercial application intake (to SA), admin campaign management, reports, and outbound comms (`email_outbox` + optional SMTP).  

University sample data is aligned with **IPOS_SampleData_2026** (Cosymed product IDs, March/April campaigns, PU0001/PU0002, scenario 20 loyalty seed, Pond-style commercial row).

---

## 2. Evolution (what was delivered over time)

| Phase | Outcomes |
|-------|----------|
| **Core PU** | JavaFX UI (`MainView.fxml`, `PortalController`), auth (email or `login_alias`: PU0001, PU0002, sysdba, manager), guest checkout, cart, `PaymentService` validation, `OrderService` checkout pipeline, `orders` / `order_items` / `payments`, `email_outbox` simulation. |
| **Sample alignment** | `seed.sql` + `schema.sql`: PDF catalogue IDs, retail pricing (markup/VAT via `app_config`), March/April campaigns, loyalty seed (`completed_order_count = 8` for PU0001). |
| **Members & commercial** | Non-commercial registration with first-login flow; commercial validation (UK-style company reg); persistence and SA handoff. |
| **Admin** | Campaign CRUD, overlap protection, terminate/delete; sales/campaign/engagement reports + print. |
| **Shared MySQL** | `db.init.mode=LOCAL` vs `SHARED`; Railway-safe behaviour (no auto seed on shared). |
| **SMTP** | `MailConfig`, `SmtpOutboxDispatcher`, `db.properties.local` / env overrides; rows still written to `email_outbox` if SMTP fails. |
| **Shared DB sample emails** | With `db.init.mode=SHARED`, `shared_align_sample_data.sql` runs once per JVM on first connection: migrates legacy `example.com` PU rows to team Gmail, and sets the **Pond Pharmacy commercial application** contact to **`ipos_commercial@yahoo.com`** (also replaces `pondPharma@example.com` or the previous Pond Gmail if still present). |
| **CA integration** | `db.inventory.api=ca` → `CaJdbcInventoryApiClient`: catalogue/stock from `ipos_ca`, `pu_online_order` / lines, `getOrderStatus` merged into PU order list and tracking (`OrderService.mergeCaStatusOntoOrder`). |
| **SA integration** | `SaJdbcMemberApiClient` → `ipos_sa.pu_commercial_application_intake` (`MemberApiFactory`; no mock). |
| **External comms** | `external_comms_queue`: CA/SA enqueue customer emails; `ExternalCommsQueueService` + ~45s poller in `PortalController` drain to `email_outbox`. SQL scripts under `docs/sql/`. |
| **CA trigger (optional)** | `docs/sql/OPTIONAL_trigger_CA_pu_online_order_shipped_delivered_only.sql` — emails on `DISPATCHED` / `DELIVERED` only. |

---

## 3. Tech stack

- Java 17+, JavaFX, Maven (`mvnw.cmd`), modular (`module-info.java`).
- MySQL (or SQLite path in config) via `DatabaseManager`.
- JDBC for PU schema + optional `ipos_ca` / `ipos_sa` cross-schema access.

---

## 4. Repository layout (what matters)

| Path | Role |
|------|------|
| `src/main/java/...` | Application code. Entry: `Main.java`. Heavy UI: `PortalController.java`. |
| `src/main/resources/com/.../views/` | FXML + `app.css`. |
| `src/main/resources/db/` | `schema.sql`, `seed.sql`, `reset.sql`, `shared_align_sample_data.sql` (classpath scripts). |
| `src/main/resources/db.properties.info` | Config key reference (safe to commit). |
| `src/main/resources/db.properties.local` | **Gitignored** — real host/user/password/SMTP (copy from `.example`). |
| `src/main/resources/mail.properties` | Default mail keys (SMTP off). |
| `docs/guide/MASTER.md` | This file. |
| `docs/sql/` | DBA/Workbench scripts (queue, grants, optional triggers). Not loaded by the app from here; Java errors may cite these paths. |

---

## 5. Run the program

```powershell
cd "<path>\IPOS-PU"
.\mvnw.cmd javafx:run
```

```powershell
.\mvnw.cmd test
```

```powershell
.\mvnw.cmd -DskipTests compile
```

---

## 6. Configuration

1. Copy `src/main/resources/db.properties.local.example` → `db.properties.local` (same folder).
2. Set host, port, `db.name`, user, password; set `db.init.mode`:
   - **`SHARED`** — connect only; runs `shared_align_sample_data.sql` once per JVM for legacy email migration; does **not** run full `seed.sql` (use on group Railway DB).
   - **`LOCAL`** — applies `schema.sql` + `seed.sql` on first connection (disposable local MySQL).
3. **Inventory API:** `db.inventory.api=mock` (local `products`) or `ca` (`CaJdbcInventoryApiClient`).
4. **Mail:** Gmail app passwords; `IPOS_MAIL_*` env vars. See `db.properties.info` for key names.

Full key list and discovery order: **`src/main/resources/db.properties.info`**.

---

## 7. Database bootstrap

- **LOCAL:** `DatabaseManager` runs `schema.sql`, ensures `login_alias`, runs `seed.sql`.
- **SHARED:** skips schema/seed; ensures `external_comms_queue` if possible; runs `db/shared_align_sample_data.sql` (classpath). That script aligns **group / Railway** sample data without re-running `seed.sql`: it updates PU0001/PU0002 legacy `example.com` rows to team Gmail and sets the **Pond Pharmacy** row in **`commercial_applications`** to **`ipos_commercial@yahoo.com`** (and migrates that row if it still uses `pondPharma@example.com` or the previous Pond Gmail).
- **Reset (local only):** run `src/main/resources/db/reset.sql`, restart with `LOCAL` to reseed. **Never** run reset on shared production without agreement.

**Seeded sample users (after team email swap):**  
PU0001 / `dimitarprem@gmail.com` + `12ss_56_SS`; PU0002 / `test.ipos.pu@gmail.com` + `34pp_78_LL`; admins `sysdba@ipos.local` / `masterkey`, `manager@ipos.local` / `GetPU_it_done`.

**Pond Pharmacy commercial application (sample row):** canonical contact **`ipos_commercial@yahoo.com`**. On the **shared** database this is applied when **`shared_align_sample_data.sql`** runs (once per JVM on first connection with **`db.init.mode=SHARED`**). On **LOCAL**, the same address is inserted by **`seed.sql`** so behaviour matches.

---

## 8. Brief / demo mapping (IPOS-PU only)

| Area | Implementation notes |
|------|----------------------|
| **PU-Members** | Registration, login, profile, order history, tracking by email + code. |
| **PU-Sales** | CA catalogue (mock or JDBC), promos, cart, counters, checkout, loyalty 10th order, delivery + simulated payment, order confirmation email (tracking code + instructions; “tracking link” = instructions unless you add a real URL in copy). |
| **PU-Comms** | Optional SMTP; `email_outbox`; SA/CA → `external_comms_queue` → PU drain; simulated card (no real PSP). |
| **PU-PRM / RPT** | Campaigns + reports + print. |
| **Sample PDF 17–18** | March / April campaigns in `seed.sql`. |
| **Scenario 20** | PU0001 `completed_order_count = 8` → 9th checkout no loyalty; 10th with discount. |

Whole-module stories in the PDF that live in **IPOS-CA** or **IPOS-SA** are out of scope for PU except where integrated via JDBC/queue.

---

## 9. Integration contracts (short)

### CA (`ipos_ca`)

- **`pu_online_order`** / **`pu_online_order_line`:** PU writes orders after payment when CA mode enabled; **`customer_email`**, **`status`**, **`order_id`**.
- **`Inventory` / catalogue:** read model for shop; pricing via `RetailPricing` on CA package cost where applicable.
- **Status lifecycle:** align literals (e.g. `RECEIVED`, `DISPATCHED`, `DELIVERED`, `VOID`) with group contract.
- **Customer emails on fulfilment:** optional DB trigger in `docs/sql/OPTIONAL_trigger_CA_pu_online_order_shipped_delivered_only.sql` **or** JDBC `INSERT` into `ipos_pu.external_comms_queue` — **not both** for the same event.

### SA (`ipos_sa`)

- **`pu_commercial_application_intake`:** PU inserts new applications (`SaJdbcMemberApiClient`). Column names must match SA DDL (e.g. `company_registration_number`).
- **Outcome emails:** SA **`INSERT`** into `ipos_pu.external_comms_queue` when staff want to notify applicant (recommended over blind status trigger so IPOS-SA access wording is correct). **`GRANT INSERT`** for `sa_user` if they insert from their app.

### PU queue (`ipos_pu.external_comms_queue`)

- Create table + grants: **`docs/sql/pu_external_comms_queue.sql`**.
- PU user: `SELECT`, `UPDATE`. CA/SA: `INSERT` if using JDBC (not required for CA if only admin-owned trigger runs).

---

## 10. SQL scripts in `docs/sql/`

| File | Purpose |
|------|---------|
| `pu_external_comms_queue.sql` | CREATE queue + GRANTs for `pu_user`, `ca_user`, `sa_user`. |
| `pu_sa_intake_grants.sql` | `GRANT INSERT, SELECT` on intake for `pu_user`. |
| `OPTIONAL_trigger_CA_pu_online_order_shipped_delivered_only.sql` | CA trigger: `DISPATCHED` / `DELIVERED` → queue. |
| `OPTIONAL_triggers_CA_SA_to_external_comms_queue.sql` | Legacy combined CA+SA triggers (full status list on CA); prefer CA-only file above + manual SA inserts if that is team choice. |

---

## 11. Key classes (navigation)

| Topic | Classes |
|-------|---------|
| Bootstrap | `Main.java`, `DatabaseManager.java` |
| Auth | `AuthService`, `UserRepository`, `LoginController` / portal auth in `PortalController` |
| Catalogue / cart | `CatalogService`, `ProductRepository`, `CampaignService` |
| Checkout | `OrderService`, `PaymentService`, `OrderRepository` |
| CA boundary | `InventoryApiFactory`, `CaJdbcInventoryApiClient`, `MockInventoryApiClient`, `I_InventoryAPI` |
| SA boundary | `MemberApiFactory`, `SaJdbcMemberApiClient`, `I_MemberAPI` |
| Mail | `CommsService`, `CommsRepository`, `MailConfig`, `SmtpOutboxDispatcher`, `ExternalCommsQueueService`, `ExternalCommsQueueRepository` |
| Pricing | `RetailPricing` |

---

## 12. Demo quick path

1. Wrong login → error.  
2. Register new member → first login password change (seeded PU0001/PU0002 skip forced change).  
3. Guest checkout → tracking lookup.  
4. Member + loyalty (PU0001 / ninth vs tenth order).  
5. Commercial application → row in SA intake.  
6. Admin: campaigns + overlap + reports.  
7. Optional: `external_comms_queue` test insert → `email_outbox` / SMTP.  
8. With CA: status updates → PU order list shows CA status; trigger optional for customer email.

---

## 13. Honest limits

- **Payment:** validated simulated card, not a live PSP.  
- **SMTP:** optional; outbox always records intent.  
- **PU0003:** commercial **application** in DB; not necessarily a full commercial **portal login** unless extended.  
- **Tracking “link”:** desktop app; email uses code + instructions unless you add a URL string.

---

## 14. Maintenance

When integration or seed changes, update **this file** and **`src/main/resources/db/seed.sql`** / **`db.properties.info`** as needed. Keep **`docs/sql/`** in sync with DBA steps you actually run on the shared server.

*Team 39 — IPOS-PU.*
