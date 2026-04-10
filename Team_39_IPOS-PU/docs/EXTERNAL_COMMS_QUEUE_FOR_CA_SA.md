# External comms queue (CA / SA → PU → customer email log)

PU owns **`ipos_pu.external_comms_queue`**. **CA** and **SA** insert rows when they want a **customer** to receive an email. PU’s desktop app **polls** the table (about every 45s), copies each pending row into **`email_outbox`** (simulated SMTP), then sets **`consumed_at`**.

This matches the brief idea that **IPOS-PU-COMMS** provides outbound comms: other subsystems **do not** send mail themselves in the prototype; they **request** PU to record the same message customers would see.

## SQL

Run **`docs/sql/pu_external_comms_queue.sql`** on the shared server (create table + `GRANT`s). Adjust `'ca_user'`, `'sa_user'`, `'pu_user'` and hosts to match your group.

## Row shape

| Column | Notes |
|--------|--------|
| `recipient_email` | Customer’s address (e.g. from `pu_online_order.customer_email` or intake `email`). |
| `subject` | Short subject line. |
| `body` | Plain text; PU appends a small footer with `source_system` and `reference_key`. |
| `purpose` | Short tag for demos, e.g. `ORDER_STATUS_DISPATCHED`, `ORDER_STATUS_DELIVERED`, `COMMERCIAL_APPROVED`. |
| `source_system` | `'CA'` or `'SA'`. |
| `reference_key` | Optional; e.g. `order_id`, intake `id`, or company reg — for traceability. |

Do **not** set `consumed_at`; PU sets it after a successful copy to `email_outbox`.

## What **CA** implements (Java, in IPOS-CA)

1. **Where:** In the same code path that updates **`ipos_ca.pu_online_order.status`** (or immediately after), when the new status is **`DISPATCHED`** or **`DELIVERED`** (and optionally **`RECEIVED`** if you want parity with checkout mail).
2. **What:** Load **`customer_email`** (and `order_id`) for that order.
3. **How:** Open your existing JDBC connection, prepare:

   `INSERT INTO ipos_pu.external_comms_queue (recipient_email, subject, body, purpose, source_system, reference_key) VALUES (?, ?, ?, ?, 'CA', ?)`

4. **Idempotency:** If your status update can run twice, avoid duplicate queue rows (e.g. only enqueue when status **changes**, or use a unique business rule on `(source_system, reference_key, purpose)` in your own logic).

## What **SA** implements (Java, in IPOS-SA)

1. **Where:** When staff **approve** or **reject** (or request more info) on **`ipos_sa.pu_commercial_application_intake`**.
2. **What:** Use the applicant’s **`email`** from that row; build subject/body (e.g. IPOS-SA access instructions per brief, or rejection reason).
3. **How:** Same `INSERT` as above with `source_system = 'SA'` and `purpose` like `COMMERCIAL_APPROVED` / `COMMERCIAL_REJECTED`.

## What **PU** already does

- **`ExternalCommsQueueService`** drains pending rows into **`email_outbox`**.
- **`PortalController`** starts a background poll when the app opens.

No change is required on PU when CA/SA add their `INSERT`s, beyond running the DDL/GRANT script on the shared DB.

**Copy-paste JDBC + Workbench SQL + optional triggers (no Java):** see [EXTERNAL_COMMS_IMPLEMENTATION_FOR_CA_SA.md](EXTERNAL_COMMS_IMPLEMENTATION_FOR_CA_SA.md) and `docs/sql/OPTIONAL_triggers_CA_SA_to_external_comms_queue.sql`.
