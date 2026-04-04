# Shared DB Integration Test Plan

This is the step-by-step test plan for integration between:

- Team 39: `IPOS-PU`
- Team 38: `IPOS-CA`
- Team 37: `IPOS-SA`

This plan assumes:

- everyone is using the same MySQL server
- Team 39 uses `db.properties.local`
- on shared DB, Team 39 sets `db.init.mode=SHARED`

---

## 0. Before testing

### Preconditions

1. Team 38 has provided:
   - catalogue object name
   - online-order object name(s)
   - order-status object name
   - permissions for PU user
2. Team 37 has provided:
   - commercial-application intake object name
   - column mapping
   - permissions for PU user
3. Team 39 has replaced or configured the mock integration classes to use the agreed shared DB objects.

### Expected result

- All three teams agree which DB objects are used for integration.
- The PU app starts without mutating shared data.

---

## 1. Startup / connection test

### Steps

1. In Team 39 `db.properties.local`, set:

```properties
db.init.mode=SHARED
```

2. Start PU:

```powershell
cd "d:\Year2\TERM 2\TEAM MODULE\TheProject\IPOS-PU"
.\mvnw.cmd javafx:run
```

3. Verify PU opens normally.

### Expected result

- App starts successfully.
- No startup error about DB connection.
- No shared DB schema/seed writes are triggered by startup.

---

## 2. PDF account login test

### Steps

1. Log in as:
   - `PU0001` / `12ss_56_SS`
2. Log out.
3. Log in as:
   - `PU0002` / `34pp_78_LL`
4. Log out.
5. Log in as:
   - `sysdba` / `masterkey`

### Expected result

- Both sample PU member accounts can log in.
- Admin login enables the `Admin` tab.
- Wrong credentials are rejected.

---

## 3. PU -> CA catalogue integration test

### Steps

1. Start PU.
2. Open the catalogue.
3. Search for:
   - `paracetamol`
   - `aspirin`
   - `ospen`
4. In MySQL, Team 38 runs a `SELECT` on the CA-agreed catalogue object.

### Expected result

- PU shows the same products that exist in CA's agreed catalogue object.
- Product ids shown/used in PU match CA's ids exactly.
- Sample ids such as `10000001`, `10000002`, `30000001` are consistent between both teams.

---

## 4. Promotions and product-id consistency test

### Steps

1. In PU, open `Promotions`.
2. Confirm seeded campaigns:
   - `March Promotion`
   - `April Promotion`
3. Verify campaign items use ids:
   - `10000002`
   - `10000003`
   - `10000004`
   - `10000006`
   - `30000001`
   - `40000001`
4. Team 38 verifies those same ids exist in CA's catalogue.

### Expected result

- PU promotions reference valid CA product ids.
- No campaign item points to a product id unknown to CA.

---

## 5. PU -> CA checkout integration test

### Steps

1. In PU, continue as guest.
2. Search for a known item, for example `aspirin`.
3. Add one item to cart.
4. Checkout with:
   - valid email
   - delivery address
   - valid card fragments
   - future expiry
5. Team 38 checks the CA-agreed order table(s).
6. Team 38 checks stock for the same product id.

### Expected result

- PU checkout succeeds.
- PU writes its own order, order_items, and payment rows.
- A new row also appears in the CA-agreed online-order object(s).
- CA confirms stock was reduced or reserved according to the agreed design.
- PU shows status `RECEIVED`.

---

## 6. CA -> PU status tracking integration test

### Steps

1. Use the successful order from test 5.
2. Team 38 manually changes or progresses the order to:
   - `DISPATCHED`
3. In PU, open `Orders / Tracking` and refresh / track the same order.
4. Team 38 then changes it to:
   - `DELIVERED`
5. Refresh again in PU.

### Expected result

- PU can read the status from CA's agreed status object.
- Status moves from `RECEIVED` to `DISPATCHED` to `DELIVERED`.
- Same `order_id` is used by both teams.

---

## 7. PU -> SA commercial application integration test

### Steps

1. In PU, choose commercial registration.
2. Submit a valid application using a unique email.
3. Team 37 checks the SA-agreed intake table.

### Expected result

- PU accepts the form and reports successful submission.
- A new row appears in the SA-agreed intake object.
- The row contains the agreed fields:
  - company registration number
  - director name
  - business type
  - address
  - email
  - timestamp
  - status

---

## 8. Duplicate / validation test (SA handoff)

### Steps

1. In PU, try to submit:
   - invalid registration number: `ABC-123`
2. Then try a commercial application using an email already used by an existing PU member.
3. Then, if Team 37 has a duplicate-company rule, submit the same company registration number twice.

### Expected result

- Invalid format is blocked in PU.
- Existing member email is blocked in PU.
- Duplicate company behavior matches the rule agreed with Team 37.

---

## 9. Failure-path test with CA

### Steps

1. Attempt checkout with invalid/expired payment details.
2. Attempt checkout for a product that CA marks unavailable or out of stock.
3. If possible, simulate a permission failure on the CA order object.

### Expected result

- Payment failure: no confirmed order is created; PU records failed payment.
- Out-of-stock case: PU shows an error and does not create an invalid confirmed order.
- Permission failure: PU shows a failure message and the issue is visible in logs / SQL behavior.

---

## 10. Evidence to collect

Collect these for the report / appendix:

### Screenshots

- successful DB connection / app startup
- successful PU member login
- successful guest checkout
- successful CA order row visible
- successful SA application row visible
- tracking status change visible in PU
- one payment failure / one validation failure

### SQL evidence

Save or screenshot the key `SELECT`s used by each team:

- CA catalogue rows
- CA online-order rows
- CA status row
- SA commercial-application row
- PU orders / payments / email_outbox rows

---

## 11. Pass criteria

Integration is considered working when:

- PU uses shared DB safely (`db.init.mode=SHARED`)
- PU reads real catalogue data from CA-agreed object(s)
- PU writes online orders to CA-agreed object(s)
- CA can move status and PU can display the new status
- PU writes commercial applications to SA-agreed object
- Product ids are consistent across PU and CA
- Validation / failure behavior matches agreed rules
