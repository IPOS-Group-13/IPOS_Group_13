# IPOS-PU Implementation Runbook

This runbook supports the Team 39 Week 11/12 demo and implementation report.

## 1) Environment

- Java: 17+
- Maven: wrapper included (`mvnw.cmd`)
- DB: MySQL 8+

## 2) Database Setup

Create database:

```sql
CREATE DATABASE ipos_pu;
```

Preferred setup: create `src/main/resources/db.properties.local` (this file is gitignored):

```properties
db.url=jdbc:mysql://<HOST>:<PORT>/<DB_NAME>?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
db.username=<USERNAME>
db.password=<PASSWORD>
```

For SSL-required hosts:

```properties
db.url=jdbc:mysql://<HOST>:<PORT>/<DB_NAME>?useSSL=true&requireSSL=true&serverTimezone=UTC
db.username=<USERNAME>
db.password=<PASSWORD>
```

Fallback setup: set environment variables before running:

- `IPOS_DB_URL` (example: `jdbc:mysql://localhost:3306/ipos_pu?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC`)
- `IPOS_DB_USERNAME` (example: `root`)
- `IPOS_DB_PASSWORD` (example: `root`)

Shared/cloud template (replace placeholders):

- `IPOS_DB_URL=jdbc:mysql://<HOST>:<PORT>/<DB_NAME>?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC`
- If SSL is required by host:
  - `IPOS_DB_URL=jdbc:mysql://<HOST>:<PORT>/<DB_NAME>?useSSL=true&requireSSL=true&serverTimezone=UTC`

Configuration resolution order at runtime:

1. `db.properties.local` (classpath resource)
2. `IPOS_DB_URL`, `IPOS_DB_USERNAME`, `IPOS_DB_PASSWORD`
3. Built-in localhost defaults (`root` / `root`)

The app auto-runs:

- `db/schema.sql`
- `db/seed.sql`

## 3) Build & Run

From `IPOS-PU`:

- Compile: `.\mvnw.cmd -DskipTests compile`
- Run UI: `.\mvnw.cmd javafx:run`

Team setup note:

- Commit only `src/main/resources/db.properties.info`.
- Every teammate creates their own `src/main/resources/db.properties.local` locally.
- Share passwords privately (chat), never in Git.

If your machine has no `JAVA_HOME`, set it first.

## 4) Demo Accounts

Seed data includes:

- Admin: `admin@ipos.local` / `admin123`
- Member (forced password change): `member@ipos.local` / `Temp@1234`

## 5) Required Demo Flows

1. Non-commercial registration -> generated 10-char password -> email outbox record.
2. Login failure and success flows.
3. First login forced password change for non-commercial user.
4. Catalogue keyword search + add to cart.
5. Promotions visibility and click tracking.
6. Checkout with final stock check and payment record.
7. Order creation and order tracking status display.
8. Commercial application submission to SA adapter.
9. Admin campaign creation and overlap conflict protection.
10. Sales/Campaign/Engagement reports rendered on screen.

## 6) Resetting Demo Data

Use `db/reset.sql`, then re-run the app so seed is applied again by startup.

For shared cloud integration day, use:

- [Shared DB smoke test checklist](SHARED_DB_SMOKE_TEST_CHECKLIST.md)
- [Schema compatibility mapping worksheet](SCHEMA_COMPATIBILITY_MAPPING.md)
- [Deterministic demo scenarios](DEMO_SCENARIOS_DETERMINISTIC.md)

## 7) Implementation Report Inputs

Use this structure:

- Runtime requirements and setup commands.
- Subsystem packaging:
  - `controllers`
  - `services`
  - `repositories`
  - `integrations`
  - `dto`
- DB table summary from `schema.sql`.
- Cross-team API contract from `docs/GROUP_API_CONTRACT_TEAM37_38_39.md`.
- Test evidence:
  - unit tests (utility/business rules),
  - component flows (registration, checkout, reports),
  - negative tests (invalid login, stock race, invalid campaign overlaps).
