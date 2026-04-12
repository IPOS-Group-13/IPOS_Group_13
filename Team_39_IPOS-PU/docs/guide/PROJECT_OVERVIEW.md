# IPOS-PU — Technical project overview

A structured overview of the public portal subsystem: **scope**, **architecture**, **persistence**, and **integration**, written for clear spoken explanation without sacrificing precision. Operational detail remains in [MASTER.md](MASTER.md); examination-oriented notes appear in [DEMO_PREP.md](DEMO_PREP.md). For a **feature-by-feature map of classes and flows** (catalogue, checkout, reports, print, campaigns, mail), see [PROGRAM_KEY_COMPONENTS_SHEET.md](PROGRAM_KEY_COMPONENTS_SHEET.md).

---

## 1. Subsystem scope

- **IPOS-PU** implements the **InfoPharma public portal**: a **JavaFX desktop client** through which end users **browse the catalogue**, **apply promotional pricing**, **manage a shopping cart**, **complete checkout** (including a **simulated payment gateway**), **track orders**, and **register** as non-commercial members or **submit commercial membership applications** intended for processing by IPOS-SA.
- Administrative functions include **campaign lifecycle management** (creation, adjustment, cancellation) and **operational reports** (sales, campaign performance, engagement metrics), with optional **print output** via the JavaFX printing API.
- The implementation follows a **Java-first, single-process** model: the user interface and **JDBC database access** run in one JVM. There is **no separate HTTP service** in this deliverable; coordination with IPOS-CA and IPOS-SA is achieved through **shared MySQL schemas** and agreed table contracts.
- The build is a **single Maven module** (`pom.xml`, Maven Wrapper), which simplifies cloning, continuous integration, and alignment with module submission expectations.

---

## 2. Technology stack and rationale

- **Java 17** — language baseline consistent with compiler settings in `pom.xml` and with contemporary standard-library features used in the codebase.
- **JavaFX (controls, FXML)** — structured UI definition through **FXML** documents and **CSS** stylesheets; stateful controls (for example **TableView**, **TabPane**) bind to **ObservableList** collections where appropriate.
- **Maven and `mvnw`** — reproducible dependency resolution and build execution across developer machines without assuming a global Maven installation.
- **JDBC** — explicit SQL via **PreparedStatement** for **parameter binding** and integration transparency. Primary deployment targets **MySQL**; **SQLite** is supported when the JDBC URL uses the `jdbc:sqlite:` scheme. An object–relational mapper was omitted so that **schema alignment** and **cross-team DDL** remain visible and reviewable.
- **MySQL Connector/J** and **SQLite JDBC** — drivers declared in the POM for the respective database backends.
- **Jakarta Mail (Angus implementation)** — optional **SMTP dispatch** after persisting messages to **`email_outbox`**. When SMTP is disabled, the application still records outbound content in the database, preserving an **auditable trail**.
- **JUnit 5** — automated tests concentrated on **service-layer rules** and **utility logic**; the JavaFX surface is validated primarily through **manual exercise**.
- **`module-info.java`** — declares **module dependencies** (`javafx.*`, `java.sql`, `jakarta.mail`), **exports** for application entry and controllers, and **opens** controller packages to `javafx.fxml` for **reflective injection** of `@FXML` fields and handlers.

---

## 3. Architectural layering

- **Presentation** — `MainView.fxml`, `LoginView.fxml`, `app.css`, and **`PortalController`**, which coordinates authentication panes, catalogue, cart, checkout, order history, administrative campaign controls, and reporting. **`LoginController`** is minimal; **authentication and registration flows** are implemented within **`PortalController`**.
  - *Rationale:* separation of **declarative layout** (FXML) from **imperative behaviour** (Java), with **CSS** centralising visual consistency.

- **Services** — `OrderService`, `AuthService`, `PaymentService`, `CatalogService`, `CampaignService`, `CommsService`, `ReportService`, `ExternalCommsQueueService` encapsulate **use-case logic** and orchestrate repositories and integration clients.
  - *Rationale:* **thin controllers**, **testable** business rules, and a stable boundary between UI events and persistence.

- **Repositories** — `UserRepository`, `ProductRepository`, `OrderRepository`, `PaymentRepository`, `CampaignRepository`, `ReportRepository`, `CommsRepository`, `CommercialApplicationRepository`, `AppConfigRepository`, `ExternalCommsQueueRepository` consolidate **SQL** per concern.
  - *Rationale:* localised change when **GRANTs** or **physical schema names** evolve during integration.

- **Integration implementations** — `MockInventoryApiClient` (PU-local catalogue), `CaJdbcInventoryApiClient` (IPOS-CA tables under `ipos_ca`), `SaJdbcMemberApiClient` (IPOS-SA intake under `ipos_sa`).
  - *Rationale:* **strategy pattern**: checkout and catalogue logic depend on **`I_InventoryAPI`** and **`I_MemberAPI`**, not on a specific deployment mode.

- **Factories** — `InventoryApiFactory`, `MemberApiFactory` resolve implementations from **configuration** (for example `db.inventory.api` / `IPOS_INVENTORY_API`).
  - *Rationale:* **centralised selection** avoids conditional wiring duplicated across the codebase.

- **Cross-cutting utilities** — `DatabaseManager` (connection and initialisation), `MailConfig` / `SmtpOutboxDispatcher`, `PasswordUtil`, `SecurityUtil`, `RetailPricing`, `ReportDateRange`, `LegacySampleProductIds`.
  - *Rationale:* **single responsibility** for connectivity bootstrap, cryptographic helpers, CA retail pricing from package cost, reporting windows, and catalogue deduplication.

---

## 4. Application bootstrap (`Main`)

- **`Main`** constructs the **primary `Stage`**, loads **`MainView.fxml`**, applies **`app.css`**, and configures **initial and minimum window dimensions**.
- **`main` invokes `DatabaseManager.getConnection()` before `Application.launch()`** so that **connectivity failures**, **driver issues**, or **initialisation errors** surface before the UI is shown.
- **`stop()`** delegates shutdown to **`DatabaseManager.closeConnection()`**, preserving an explicit **lifecycle** boundary.

---

## 5. PU relational model (`schema.sql`)

Tables are created from **`src/main/resources/db/schema.sql`** when **`db.init.mode=LOCAL`**. On a **shared** database, objects are assumed to exist unless **targeted DDL** (for example queue creation) is executed by the application under permitted privileges.

| Area | Tables | Purpose |
|------|--------|---------|
| Identity | `users` | Credentials, `member_type`, `first_login_required`, `completed_order_count` (loyalty eligibility), optional `login_alias` for brief-aligned sample logins. |
| Commercial | `commercial_applications` | PU-side record of commercial submissions; integrated handoff additionally uses SA’s intake table. |
| Catalogue (mock mode) | `products` | Local product catalogue and stock when inventory is not sourced from CA. |
| Promotions | `campaigns`, `campaign_items`, `campaign_metrics` | Campaign windows, per-product discount percentages, and **hit / add / purchase** counters for analytics. |
| Sales | `orders`, `order_items` | Order header, line items, pricing and discount **snapshots**, tracking identifier, delivery address. |
| Payments | `payments` | Simulated payment outcomes; supports success and failure audit paths. |
| Messaging | `email_outbox` | **Authoritative log** of outbound email content and purpose codes. |
| Cross-subsystem mail | `external_comms_queue` | **Ingress** for CA/SA-generated notifications; PU **drains** into `email_outbox` and sets **`consumed_at`**. |
| Configuration | `app_config` | Key–value parameters (for example **retail markup** and **VAT rate**) used when deriving retail prices from CA package cost. |

**Design intent:** **`ipos_pu`** holds PU-owned transactional and promotional data; **CA** and **SA** data reside in **separate schemas** on the same server instance where integration applies, subject to **GRANT** statements issued by the database administrator.

---

## 6. `DatabaseManager`: connectivity and initialisation modes

- **Configuration** is assembled from **`db.properties.local`** (classpath and **discovered filesystem paths**), optional **environment variables** (`IPOS_DB_*`, `IPOS_PU_CONFIG_DIR`, and related properties), with **sensible defaults** for local MySQL.
- **`LOCAL` initialisation** executes **`schema.sql`**, ensures the **`login_alias`** column where applicable, and runs **`seed.sql`** — appropriate for a **developer-owned** database.
- **`SHARED` initialisation** **suppresses** full schema replay and seeding; it applies **`shared_align_sample_data.sql`** for **idempotent** alignment of sample rows and may **create `external_comms_queue`** on MySQL when privileges allow, so the queue exists without manual intervention on some deployments.
- **Rationale for dual modes:** protect the **shared team database** from destructive reinitialisation on every application start while preserving a **one-command** setup path for individual developers.
- **`db.properties.info`** may serve as a **classpath marker** whose filesystem location enables discovery of **`db.properties.local`** beside Maven or IDE output directories.

---

## 7. Inventory integration: mock versus CA

- **`I_InventoryAPI`** defines **catalogue retrieval**, **stock checks**, **pre-payment reservation validation**, **order propagation**, and **order status lookup**.
- **`MockInventoryApiClient`** uses **`ProductRepository`** against **`products`** in **`ipos_pu`**. Stock changes run inside **transactions** with **conditional updates** so concurrent checkouts cannot commit **negative availability** without detection.
- **`CaJdbcInventoryApiClient`** reads **`ipos_ca.Inventory`**, computes **customer retail unit price** from **`Package_cost`** using **`RetailPricing`** and **`app_config`**. On successful checkout it **decrements CA availability** and inserts **`pu_online_order`** and **`pu_online_order_line`** within a **single transaction**, while PU still persists **`orders`** / **`order_items`** for portal history and payment linkage.
- **`InventoryApiFactory`** selects the implementation from **`db.inventory.api`** (or environment), enabling **configuration-driven** switching without altering checkout orchestration.

**Concurrency note:** **`reserveOrReject`** performs a **read-consistent feasibility check** before payment; **`submitOnlineOrder`** performs the **authoritative stock mutation**. This **optimistic** pattern is appropriate to the module’s scale; a production system might add stronger isolation or compensating transactions around payment capture.

---

## 8. SA commercial intake

- **`AuthService`** validates commercial application fields (company registration format, director name, business description, address bounds, email uniqueness against **`users`**).
- **`SaJdbcMemberApiClient`** implements **`I_MemberAPI`** by inserting into **`ipos_sa.pu_commercial_application_intake`** with initial **`submission_status`** `SUBMITTED_TO_SA`.
- **JDBC** was chosen to align with the **shared MySQL integration model** and **DBA-managed privileges**, rather than introducing a separate REST tier for this coursework slice.

---

## 9. Authentication, registration, and guest checkout

- **Authentication** supports **email** and, where present, **`login_alias`**, via **`UserRepository.authenticate`**. **SHA-256** hashes are compared for normal accounts; **plaintext-stored** passwords remain acceptable for **PDF-aligned sample users**, which should be described as a **demonstration limitation**, not a security standard.
- **Non-commercial registration** generates a **temporary password** (`SecurityUtil`), stores **`PasswordUtil.hash`** output, and triggers **`CommsService.sendRegistrationEmail`**.
- **Forced password change** enforces **minimum length** and **confirmation matching**, then clears **`first_login_required`**.
- **Guest checkout** allocates or reuses a **synthetic `users` row** through **`ensureGuestCheckoutUser`** so **`orders.user_id`** remains a valid foreign key while the customer is not a registered member.

---

## 10. Checkout pipeline, payment simulation, loyalty

- **`OrderService.checkout`** sequences **validation**, **stock reservation check**, **cart total calculation** (including **10% loyalty** for qualifying non-commercial members on every tenth completed order, per **`User.isEligibleForLoyaltyDiscount`**), **payment processing**, **inventory propagation**, **persistence** of orders, line items, and payments, **campaign purchase metrics**, **increment of completed order count**, and **order confirmation messaging**.
- **`PaymentService`** (implementing **`I_PaymentAPI`**) validates **card metadata** and **expiry** (`MM/yy` against the current month), returning a **synthetic transaction identifier** on success. This **simulates** a card processor without PCI scope.

---

## 11. Catalogue search and campaign administration

- **`CatalogService`** queries **`ProductRepository`** in mock mode; in CA mode it loads the **CA catalogue** and applies **in-memory keyword filtering**. **`LegacySampleProductIds`** resolves **duplicate display names** by preferring **non-legacy** product identifiers where duplicates arise from sample data evolution.
- **`CampaignService`** parses administrative **item specifications**, enforces **non-overlapping** campaign coverage for the same product within intersecting time windows, and records **promotional metrics** consumed by **`ReportService`**.

---

## 12. Communications architecture

- **`CommsRepository.saveOutboundEmail`** **inserts** into **`email_outbox`**, then invokes **`SmtpOutboxDispatcher.dispatchIfEnabled`**. **Persistence precedes SMTP** so that **delivery failure does not remove** the record from the audit log.
- **`MailConfig.load()`** merges **`mail.properties`**, keys from **`db.properties.local`**, and **environment overrides** for SMTP endpoints and credentials.
- **`ExternalCommsQueueService`** transfers pending rows from **`external_comms_queue`** into **`email_outbox`**, appends a **provenance footer** (`source_system`, `reference_key`), and updates **`consumed_at`**.
- **`PortalController`** schedules a **JavaFX `Timeline`** (approximately **45 seconds**) to invoke the drain operation, balancing **responsiveness** with **moderate polling frequency** while the application is running.

---

## 13. Reporting and printing

- **`ReportService`** enforces **valid date ranges** and delegates aggregation to **`ReportRepository`** for **sales**, **campaign**, and **engagement** summaries.
- The UI exposes **date selection** and **printer output** through standard JavaFX APIs.

---

## 14. Order history, tracking, and CA status reconciliation

- **`OrderService.getOrderHistory`** loads PU orders; when **CA inventory** is enabled, **`mergeCaStatusOntoOrder`** overlays **fulfilment status** from **`getOrderStatus`**, treating **`VOID`** as a sentinel when no authoritative row exists.
- **`findOrderByTracking`** normalises the customer email and resolves orders by **tracking code**, supporting **guest and registered** purchasers.

---

## 15. Domain models and data transfer objects

- **Models** (`User`, `Order`, `Product`, `CartItem`) carry **UI session state** and **derived quantities** (for example **line totals**, **loyalty eligibility**).
- **DTOs** (`CartLineDto`, `OnlineOrderRequest`, `CommercialApplicationDto`, `InventoryItemDto`, `PaymentRequest`, `PaymentResult`, queue records) provide **stable, minimal payloads** across **service** and **integration** boundaries, reducing coupling to presentation-layer state.

---

## 16. Concurrency and the JavaFX application thread

- Long-running work uses **`javafx.concurrent.Task`** on **daemon worker threads** (`runButtonAction`, `runAsyncAction` in **`PortalController`**) so that **JDBC latency** does not block **input and rendering** on the JavaFX **application thread**.

---

## 17. Security posture (coursework context)

- Password storage uses **SHA-256**; sample data may retain **plaintext** for brief fidelity. A production system would employ **slow key-derivation functions** and **eliminate reversible storage**.
- **Credentials and connection strings** belong in **`db.properties.local`**, which is **excluded from version control**.
- **SQL injection** is mitigated through **parameterised statements** throughout repositories.

---

## 18. Automated testing

- Tests under **`src/test/java`** target **services**, **models**, and **utilities** (`AuthService`, `OrderService`, `PaymentService`, `User`, `SecurityUtil`, `RetailPricing`, `ReportDateRange`). **End-to-end UI** scenarios are not automated.

---

## 19. Supplementary SQL under `docs/sql/`

- Scripts for **privilege grants**, **queue provisioning**, and **optional database triggers** are maintained under **`docs/sql/`** for **administrator execution**. They are **not** universally executed by the application on startup, by design, so that **production policy** remains explicit and reviewed.

---

## 20. Implementation notes

- **`SessionState`** exists as a **session holder** type; the running application primarily stores the **current user** and **guest mode** on **`PortalController`**. Refactoring toward **`SessionState`** would consolidate that responsibility if desired.
- **`LoginController`** does not host the primary authentication UI; that responsibility lies with **`PortalController`**.

---

## 21. Closing summary

IPOS-PU is a **layered JavaFX application** backed by **JDBC**: it maintains **portal state and promotions** in **`ipos_pu`**, can operate against **local mock inventory** or **IPOS-CA’s** catalogue and order tables, submits **commercial intake** to **IPOS-SA**, records **all outbound correspondence** in **`email_outbox`** with **optional SMTP delivery**, and **integrates asynchronous notifications** from CA and SA through **`external_comms_queue`**. **Interface-based inventory and member APIs**, together with **factories**, allow these modes to share a **single checkout orchestration** path.

---

*Revise this document when integration contracts or major features change.*
