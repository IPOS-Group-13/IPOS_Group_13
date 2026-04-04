# IPOS-PU Final Shared DB and Delivery Checklist

This checklist is the final execution guide for Team 39 to complete IPOS-PU delivery with Aden's shared database setup.

## 1) Progress Snapshot

- Overall delivery progress: **78% complete**
- Standalone IPOS-PU features: **90% complete**
- Shared/cross-team integration: **55% complete**

Progress bar:

- `Overall            [###############-----] 78%`
- `Core features      [##################--] 90%`
- `Cross-team finish  [###########---------] 55%`

## 2) Final DB Configuration (Aden Method)

For **`db.init.mode`** (`LOCAL` vs `SHARED`), env overrides, and a full scenario/runbook in one place, see **[PROJECT_STATUS_AND_RUNBOOK.md](PROJECT_STATUS_AND_RUNBOOK.md)**.

Expected outcome:
- Every teammate can run the app against the shared DB without editing Java code.
- Password is not committed to Git.

How to do it:
1. In `src/main/resources`, keep `db.properties.info` tracked in Git.
2. Each teammate creates a local-only file named `db.properties.local` in `src/main/resources`.
3. Add these keys in `db.properties.local`:
   - `db.url=jdbc:mysql://<HOST>:<PORT>/<DB_NAME>?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC`
   - `db.username=<USERNAME>`
   - `db.password=<PASSWORD>`
4. If provider requires SSL, use:
   - `db.url=jdbc:mysql://<HOST>:<PORT>/<DB_NAME>?useSSL=true&requireSSL=true&serverTimezone=UTC`
5. Never push `db.properties.local`; it is gitignored.

Pass criteria:
- App startup log shows the shared host URL.
- No DB auth or connection error on startup.

## 3) How To Run The Program

Expected outcome:
- Team can compile and run IPOS-PU consistently on any machine.

How to do it (from `IPOS-PU` folder):
1. Compile:
   - `.\mvnw.cmd -DskipTests compile`
2. Run:
   - `.\mvnw.cmd javafx:run`

Pass criteria:
- JavaFX window opens.
- Login screen/portal loads.
- No startup exception in terminal.

## 4) Required Team Work Still Left

### A) Shared DB and permissions sign-off

Expected outcome:
- Team 39 has only the rights needed for agreed integration flows.

How to do it:
- Confirm with Team 37/38:
  - integration object names (handoff/order/stock/status)
  - required SQL permissions (`SELECT`, `INSERT`, minimal `UPDATE`)
  - order status lifecycle (`RECEIVED`, `DISPATCHED`, `DELIVERED`, `VOID`)
  - error semantics (`OUT_OF_STOCK`, `INVALID_ITEM`, `SYSTEM_ERROR`, etc.)

Pass criteria:
- `INTEGRATION_READINESS_CHECKLIST.md` is fully checked with owners and date.

### B) Shared DB smoke test evidence

Expected outcome:
- Core write/read flows are proven on shared DB.

How to do it:
1. Run startup check.
2. Execute:
   - non-commercial registration
   - commercial application submission
   - add-to-cart and checkout
   - promotions view and add-to-cart metric update
   - one report query flow
3. Intentionally run failures:
   - wrong password
   - wrong DB name
   - SSL mismatch (if relevant)
   - missing permission

Pass criteria:
- One screenshot/log for each success flow and failure case.
- Evidence attached for implementation report appendix.

### C) Cross-team integration closure

Expected outcome:
- Mock adapters are replaced or validated against real Team 37/38 interfaces/tables.

How to do it:
- Validate `IMemberAPI` handoff is readable by Team 37.
- Validate stock/order propagation expectations with Team 38.
- Confirm no contract-field mismatches.

Pass criteria:
- Group contract notes updated and signed off by all teams.

## 5) IPOS-PU Capability Checklist (Expected Outcome + How + Verification)

### 1. Login and authentication
- Expected outcome: valid users can log in; invalid credentials are rejected.
- How: test valid/invalid credentials from seeded users.
- Verify: valid login opens tabs; invalid login shows error.

### 2. Non-commercial registration with generated password
- Expected outcome: account is created and onboarding email event is recorded.
- How: register with unique non-commercial email.
- Verify: user row exists and `email_outbox` has matching record.

### 3. First-login forced password change
- Expected outcome: first login requires password reset.
- How: sign in with seeded first-login account.
- Verify: forced password dialog appears; login only continues after successful change.

### 4. Commercial membership application handoff
- Expected outcome: commercial registration fields are stored for SA processing.
- How: submit commercial registration form with valid fields.
- Verify: row appears in `commercial_applications`.

### 5. Catalogue and search
- Expected outcome: products load and keyword filter is accurate.
- How: run several keyword searches.
- Verify: returned list only includes matching products.

### 6. Cart, campaign discount, loyalty discount
- Expected outcome: campaign and loyalty rules are applied to totals correctly.
- How: add campaign-eligible items; use member at 9 completed orders for loyalty scenario.
- Verify: line totals and final total match expected discounts.

### 7. Checkout and payment recording
- Expected outcome: paid checkout creates order, items, and payment records.
- How: complete checkout with valid cart.
- Verify: rows exist in `orders`, `order_items`, and `payments`.

### 8. Stock race/out-of-stock handling
- Expected outcome: unavailable items are rejected/removed and user is informed.
- How: trigger low-stock scenario during checkout.
- Verify: unavailable lines removed from cart and no invalid order is confirmed.

### 9. Promotions engagement metrics
- Expected outcome: campaign view/add/purchase counters increment as defined.
- How: open promotions, add item, complete paid checkout.
- Verify: `campaign_metrics` counters increase correctly.

### 10. Admin campaign management
- Expected outcome: admin can create/cancel campaigns and overlap protection works.
- How: create campaigns with overlapping and non-overlapping windows.
- Verify: overlap attempt is blocked; valid campaign persists.

### 11. Reports (sales, campaign, engagement)
- Expected outcome: report outputs render and aggregate correctly.
- How: generate all three reports after sample activity.
- Verify: rows render with expected totals/counts.

## 6) Submission-Ready Exit Criteria

All items below must be true before final submission:

- Shared DB config method (`db.properties.local`) works for every teammate.
- No secrets are committed.
- All capability checks in section 5 pass.
- Shared DB smoke evidence is collected and archived.
- Integration readiness checklist is fully signed off with Team 37/38.
