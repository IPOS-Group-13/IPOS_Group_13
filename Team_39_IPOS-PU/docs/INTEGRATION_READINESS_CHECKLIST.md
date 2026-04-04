# Integration Readiness Checklist (While Waiting For Shared DB Access)

Use this checklist with Team 37 (IPOS-SA) and Team 38 (IPOS-CA) before final integration.

## A. Integration Objects To Confirm

### 1) PU -> SA (Commercial Membership Handoff)
- [ ] Target object name agreed (table or endpoint)
- [ ] Required fields agreed:
  - company registration number
  - director name
  - business type
  - address
  - email
  - submission timestamp
- [ ] Submission status lifecycle agreed (e.g., `SUBMITTED`, `RECEIVED`, `APPROVED`, `REJECTED`)
- [ ] Idempotency rule agreed (duplicate submission handling)

### 2) PU <-> CA (Inventory + Online Order Propagation)
- [ ] Catalogue object/source agreed (items + retail price + stock)
- [ ] Final stock check object agreed (race-condition safe)
- [ ] Stock deduction object agreed (post-payment only)
- [ ] Order status object agreed (`RECEIVED`, `DISPATCHED`, `DELIVERED`, `VOID`)
- [ ] Error states agreed (`OUT_OF_STOCK`, `INVALID_ITEM`, `PERMISSION_DENIED`, `SYSTEM_ERROR`)

### 3) PU-provided shared services (Comms/Payment abstractions)
- [ ] Generic email contract agreed (`recipient`, `subject`, `body`, timestamp)
- [ ] Payment contract agreed (amount + card fragments + timestamp + status + transaction id)
- [ ] Failure semantics agreed (do not confirm order on payment failure)

## B. Permissions Checklist (Shared DB Mode)

For Team 39 credentials, confirm exact permissions:

- [ ] `SELECT` on shared/read tables required by PU
- [ ] `INSERT` on commercial application handoff object
- [ ] `UPDATE` on stock/order status integration objects (only where required)
- [ ] No unnecessary `DROP`, `ALTER`, or broad admin permissions
- [ ] Access restricted to Team 39 schema plus explicitly approved shared objects

## C. Data Integrity Rules

- [ ] Primary keys and foreign keys agreed for shared objects
- [ ] NULL vs NOT NULL constraints agreed for all integration columns
- [ ] Numeric precision agreed for money/discounts
- [ ] Timezone format agreed for timestamps
- [ ] Character encoding agreed (UTF-8)

## D. Operational Readiness

- [ ] Host, port, db name, username, password distributed privately
- [ ] SSL requirement confirmed (`required` or `not required`)
- [ ] IP allowlist requirement confirmed
- [ ] Backup and restore owner nominated
- [ ] Fallback plan agreed (local MySQL restore) for demo day

## E. Evidence For Implementation Report

- [ ] Screenshot or export of successfully connected DB session
- [ ] At least one successful PU->SA handoff record
- [ ] At least one successful PU->CA stock-deduction flow
- [ ] At least one race-condition failure flow with expected PU behavior
- [ ] Versioned copy of contract document shared with Teams 37/38
