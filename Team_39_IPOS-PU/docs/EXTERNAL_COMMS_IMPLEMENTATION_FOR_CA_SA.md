# Handoff: CA / SA → `external_comms_queue` (SQL + Java you can copy)

PU reads this queue and copies rows into `email_outbox`. CA and SA can populate it in **two** ways:

1. **Recommended for their apps:** JDBC `PreparedStatement` right after they change status in their own code.  
2. **Optional (no Java changes):** MySQL **triggers** on their tables (run once as DBA). Good for demos if CA/SA never add the INSERT themselves.

**Status names (align with brief / PU):** `RECEIVED`, `DISPATCHED`, `DELIVERED`, `VOID` on **`ipos_ca.pu_online_order.status`**.

**Commercial intake:** SA should **`UPDATE`** `ipos_sa.pu_commercial_application_intake.submission_status` when staff decide (e.g. `APPROVED`, `REJECTED`, `MORE_INFO_REQUIRED`) — pick literals your group agrees; the examples below use those three.

---

## A) Java (JDBC) — CA team: after order status changes

Use the same MySQL connection CA already uses. Call this **immediately after** a successful `UPDATE` on `pu_online_order` (or inside the same transaction **after** commit, if you prefer).

```java
private static final String SQL_ENQUEUE_PU_COMMS = """
    INSERT INTO ipos_pu.external_comms_queue
        (recipient_email, subject, body, purpose, source_system, reference_key)
    VALUES (?, ?, ?, ?, 'CA', ?)
    """;

/** Call when status changed to dispatched/delivered/received as needed. */
public void notifyCustomerOrderStatus(Connection conn, String orderId, String customerEmail, String newStatus)
        throws SQLException {
    if (customerEmail == null || customerEmail.isBlank()) {
        return;
    }
    String subject = "Your order " + orderId + " — " + newStatus;
    String body = "Your online order " + orderId + " status is now: " + newStatus + ".\n\n"
            + "You can track your order in IPOS-PU with the email you used at checkout and your tracking code.\n";
    String purpose = "ORDER_STATUS_" + newStatus.trim().toUpperCase().replace(' ', '_');

    try (PreparedStatement ps = conn.prepareStatement(SQL_ENQUEUE_PU_COMMS)) {
        ps.setString(1, customerEmail.trim());
        ps.setString(2, subject);
        ps.setString(3, body);
        ps.setString(4, purpose.length() > 64 ? purpose.substring(0, 64) : purpose);
        ps.setString(5, orderId);
        ps.executeUpdate();
    }
}
```

**Wire it:** wherever CA runs `UPDATE ipos_ca.pu_online_order SET status = ? WHERE order_id = ?`, load `customer_email` for that `order_id`, then call `notifyCustomerOrderStatus(...)`.

**`purpose` length:** `email_outbox` / queue use `VARCHAR(64)` — keep `purpose` ≤ 64 characters (example above truncates if needed).

---

## B) Java (JDBC) — SA team: after commercial intake status changes

When staff change **`submission_status`** on a row (by `UPDATE`, not only on first `INSERT`):

```java
private static final String SQL_ENQUEUE_PU_COMMS = """
    INSERT INTO ipos_pu.external_comms_queue
        (recipient_email, subject, body, purpose, source_system, reference_key)
    VALUES (?, ?, ?, ?, 'SA', ?)
    """;

public void notifyCommercialApplicant(Connection conn, int intakeId, String applicantEmail, String directorName,
        String newStatus) throws SQLException {
    if (applicantEmail == null || applicantEmail.isBlank()) {
        return;
    }
    String subject = "Commercial membership application — " + newStatus;
    String body = "Dear " + (directorName != null ? directorName : "applicant") + ",\n\n"
            + "Your commercial membership application (reference " + intakeId + ") status is: " + newStatus + ".\n\n"
            + "If approved, further instructions for IPOS-SA access will follow from InfoPharma.\n\n"
            + "Regards,\nInfoPharma Ltd\n";
    String purpose = "COMMERCIAL_" + newStatus.trim().toUpperCase().replace(' ', '_');
    if (purpose.length() > 64) {
        purpose = purpose.substring(0, 64);
    }

    try (PreparedStatement ps = conn.prepareStatement(SQL_ENQUEUE_PU_COMMS)) {
        ps.setString(1, applicantEmail.trim());
        ps.setString(2, subject);
        ps.setString(3, body);
        ps.setString(4, purpose);
        ps.setString(5, String.valueOf(intakeId));
        ps.executeUpdate();
    }
}
```

**Wire it:** after `UPDATE ipos_sa.pu_commercial_application_intake SET submission_status = ? WHERE id = ?`.

---

## C) Raw SQL — manual test in Workbench (anyone)

**CA-style row:**

```sql
INSERT INTO ipos_pu.external_comms_queue
    (recipient_email, subject, body, purpose, source_system, reference_key)
VALUES (
    'you@gmail.com',
    'Your order ORD-TEST123 — DISPATCHED',
    'Your online order ORD-TEST123 status is now: DISPATCHED.',
    'ORDER_STATUS_DISPATCHED',
    'CA',
    'ORD-TEST123'
);
```

**SA-style row:**

```sql
INSERT INTO ipos_pu.external_comms_queue
    (recipient_email, subject, body, purpose, source_system, reference_key)
VALUES (
    'you@gmail.com',
    'Commercial membership application — APPROVED',
    'Your application (ref 1) has been approved. Further details for IPOS-SA will follow.',
    'COMMERCIAL_APPROVED',
    'SA',
    '1'
);
```

With PU running, within ~45 seconds the row should be copied to `email_outbox` and `consumed_at` set on the queue row.

---

## D) Optional: MySQL triggers (no CA/SA Java)

**Caveats:** Runs on **every** qualifying `UPDATE`; no built-in deduplication; needs a user with rights to insert into `ipos_pu` from triggers (often run as `root` / admin). **DROP** old triggers before re-creating.

Full script: **`docs/sql/OPTIONAL_triggers_CA_SA_to_external_comms_queue.sql`**

---

## E) What you (PU) already did

- Table + GRANTs: `docs/sql/pu_external_comms_queue.sql`  
- PU polls and drains to `email_outbox`: `ExternalCommsQueueService` + `PortalController` timer  

No PU change needed when CA/SA add JDBC or triggers, as long as the table exists and grants are correct.
