# IPOS-PU Demo Cheat Sheet (One Page)

Seed data follows **IPOS_SampleData_2026.pdf** (Cosymed catalogue ids, March/April promotions, sample PU accounts).  
Use this for live delivery. Keep the full version in `docs/PROFESSOR_MARKING_SHEET_DEMO_RUNBOOK.md`.

**Clean PDF dataset:** use `db.init.mode=LOCAL`, run `db/reset.sql` on your own/local DB, then restart the app (so `seed.sql` runs again).
For Railway/shared DB, use `db.init.mode=SHARED` so startup does **not** mutate shared data.

## 0) Setup

Run app:

```powershell
cd "d:\Year2\TERM 2\TEAM MODULE\TheProject\IPOS-PU"
.\mvnw.cmd javafx:run
```

Run MySQL (if using shared host):

```powershell
mysql -h caboose.proxy.rlwy.net -P 15730 -u pu_user -p
```

Then:

```sql
USE ipos_pu;
```

In `src/main/resources/db.properties.local` on shared DB, include:

```properties
db.init.mode=SHARED
```

## 1) Auth (PDF PU accounts)

**Say:** "Invalid credentials are rejected; sample members match the university sheet."

**Click:**
1. Try wrong login.
2. Log in as **PU0002** (username) or `cool1@example.com` / `34pp_78_LL` (no forced first-login in seed).
3. Or **PU0001** / `12ss_56_SS` (same as `cool@example.com`; eight prior orders in seed for scenario 20).
4. **Optional — first-login flow:** register a **new** non-commercial email first, then log in with the generated password and complete the forced password change dialog.

**Query:**

```sql
SELECT email, member_type, first_login_required, completed_order_count
FROM users
WHERE email IN ('cool@example.com', 'cool1@example.com', 'sysdba@ipos.local');

-- PDF usernames (same rows): PU0001, PU0002, sysdba, manager
SELECT email, login_alias, member_type FROM users WHERE login_alias IS NOT NULL;
```

## 2) Admin (PDF PU admins)

**Say:** "PU-Admin / Administrator accounts from the sample data."

**Click:** Log in as `sysdba@ipos.local` / `masterkey` or `manager@ipos.local` / `GetPU_it_done` — **Admin** tab available.

## 3) Public/Guest Purchase

**Say:** "Public users can purchase without registration."

**Click:**
1. `Continue As Guest`.
2. Search `paracetamol` or `aspirin`.
3. Add to cart (product ids **10000001**, **10000002**, … per PDF).
4. Checkout with valid email, address, card (`VISA`, `1234`, `5678`, future `MM/YY`).

**Query:**

```sql
SELECT o.id, o.status, o.tracking_code, p.payee_details, p.amount, p.status AS payment_status
FROM orders o
JOIN payments p ON p.order_id = o.id
ORDER BY o.created_at DESC
LIMIT 5;
```

## 4) Tracking Lookup

**Say:** "Order status can be tracked by email + tracking code."

**Click:** `Orders / Tracking` — use checkout email + code from success banner / `email_outbox`.

## 5) Scenario 20 style — PU0001 (`cool@example.com`)

**Say:** "Sample member matches PDF PU0001 email/password; seed stores **8** prior completed orders (PDF: eight purchases). Next checkout is the **9th** order — **no** loyalty discount yet (PU applies 10% on every **10th** order: need `completed_order_count = 9` before checkout)."

**Click:** Log in `cool@example.com` / `12ss_56_SS`. Add e.g. Ospen (`30000001`), checkout per scenario 20.

**Demo 10th-order loyalty:** `UPDATE users SET completed_order_count = 9 WHERE email = 'cool@example.com';` then checkout once.

**Query:**

```sql
SELECT email, completed_order_count FROM users WHERE email = 'cool@example.com';
```

## 6) Non-Commercial Registration (live)

**Say:** "Registration generates credentials and `email_outbox` row."

**Click:** Register a **new** email (not already in `users`).

## 7) Commercial Application (PDF PU0003 + UK reg)

**Say:** "Validation and SA handoff; university sample company number accepted."

**Click:**
1. Commercial mode.
2. Fail: `ABC-123`.
3. Success example: `UK10003429CompH`, director name, `Community Pharmacy`, full address, **unique** company email.

**Query (seeded Pond row):**

```sql
SELECT company_registration_number, email, submission_status
FROM commercial_applications
WHERE email = 'pondPharma@example.com';
```

## 8) Promotions — March & April (PDF scenarios 17–18)

**Say:** "Seeded **March Promotion** and **April Promotion** with PDF dates and discount lines."

**Click:** Open **Promotions**; confirm Aspirin / Analgin / Celebrex 100 / Retin-A (March) and Ospen / Vitamin C (April).

```sql
SELECT name, start_time, end_time, status FROM campaigns ORDER BY start_time;
SELECT c.name, ci.product_id, ci.discount_percent
FROM campaigns c
JOIN campaign_items ci ON ci.campaign_id = c.id
ORDER BY c.name, ci.product_id;
```

## 9) Campaign Lifecycle (Admin)

**Say:** "Create / overlap protection / update / terminate / delete."

**Click:** Log in as `sysdba@ipos.local` / `masterkey`. Create a **new** campaign with non-overlapping dates, e.g. `Summer Demo` with `40000002:5`, then try an overlapping line on an active campaign product and expect block.

## 10) Reports + Print

Generate Sales, Campaign, Engagement reports; `Print Report`.

## 11) Payment Failure

Expired `MM/YY` on checkout; inspect `payments` for failed row.

## 12) Final Proof Query

```sql
SELECT id, recipient_email, purpose, created_at FROM email_outbox ORDER BY id DESC LIMIT 20;
SELECT id, status, tracking_code, created_at FROM orders ORDER BY created_at DESC LIMIT 20;
SELECT id, order_id, payee_details, status, created_at FROM payments ORDER BY id DESC LIMIT 20;
```

## Product ID quick reference (Cosymed / PDF, no spaces)

| id       | Name (short)   |
|----------|----------------|
| 10000001 | Paracetamol    |
| 10000002 | Aspirin        |
| 10000003 | Analgin        |
| 10000006 | Retin-A        |
| 30000001 | Ospen          |
| 40000001 | Vitamin C      |

(PDF shows **100 00001** style; DB uses **10000001**.)
