# Professor Marking Sheet Demo Runbook

Use this runbook to demonstrate IPOS-PU against the professor's published feature list in a clean, marks-focused order.

## 1. Best Demo Order

Use this sequence in the live demo:

1. Start the app and show the shared DB connection.
2. Show invalid login rejection.
3. Show first-login forced password change for the seeded member.
4. Show guest/public checkout.
5. Show tracking lookup using the guest order email and tracking code.
6. Show non-commercial registration and password copy.
7. Show commercial application validation, then successful submission.
8. Log in as admin and show campaign create, overlap blocking, update, terminate, and delete.
9. Show the three reports and open the print dialog.
10. Finish with MySQL evidence queries for the inserted rows and counters.

This order proves the biggest rubric items early and keeps the later admin/report actions easy to explain.

## 2. Clean-State Option

If you want a predictable demo from reset state:

1. Connect to MySQL and run:

```sql
SOURCE src/main/resources/db/reset.sql;
```

2. Restart the app so `schema.sql` and `seed.sql` repopulate the database.

After a clean reset, these seeded credentials should exist again:

- Admin: `admin@ipos.local` / `admin123`
- Seeded member: `member@ipos.local` / `Temp@1234`

If you already changed the member password in a previous session and do not reset the DB, use the most recent password you set.

## 3. Demo Inputs To Prepare

Use values like these during the demo:

- Guest order email: `guest.demo+01@example.com`
- New non-commercial registration email: `demo.user+01@example.com`
- Commercial registration email: `demo.company+01@example.com`
- Valid company registration number: `SC123456`
- Invalid company registration number: `ABC-123`
- Director name: `Ava Johnson`
- Business type: `Community Pharmacy`
- Address: `12 Demo Street, Middlesbrough`
- Valid card type: `VISA`
- Valid first4: `1234`
- Valid last4: `5678`
- Valid expiry: any future `MM/YY`, for example `12/30`

## 4. App Startup Proof

Action:

1. From the `IPOS-PU` folder run:

```powershell
.\mvnw.cmd javafx:run
```

Expected on screen:

- Login screen appears.

Expected in terminal:

- Shared DB connection is established.

Evidence:

- Screenshot of app startup.
- Screenshot of terminal line showing `Database connection established`.

## 5. MEMBERS Package

### 5.1 Invalid Login Rejection

Action:

1. Enter any incorrect email/password combination.
2. Press `Login`.

Expected:

- Login is rejected.
- Main tabs remain locked.

Evidence:

- Screenshot of the error/status message.

### 5.2 First-Login Forced Password Change

Action:

1. Log in with `member@ipos.local` / `Temp@1234`.
2. When the forced password dialog appears, enter a new password and confirm it.

Expected:

- Password change dialog appears before the portal opens.
- After success, the member enters the portal.

Evidence:

- Screenshot of the forced password dialog.
- Screenshot after successful member login.

Optional DB check:

```sql
SELECT email, first_login_required
FROM users
WHERE email = 'member@ipos.local';
```

Pass condition:

- `first_login_required` becomes `0`.

### 5.3 Non-Commercial Registration

Action:

1. Return to the auth screen if needed.
2. Keep `Non-Commercial` selected.
3. Register `demo.user+01@example.com`.

Expected:

- Temporary password appears.
- Copy action is available.
- Registration email event is recorded.

Evidence:

- Screenshot of generated password/status.

DB checks:

```sql
SELECT id, email, member_type, first_login_required, completed_order_count
FROM users
WHERE email = 'demo.user+01@example.com';
```

```sql
SELECT id, recipient_email, subject, purpose, created_at
FROM email_outbox
WHERE recipient_email = 'demo.user+01@example.com'
ORDER BY id DESC;
```

Pass condition:

- User exists as `NON_COMMERCIAL`.
- `first_login_required = 1`.
- Matching registration email row exists.

### 5.4 Commercial Application Validation And Submission

Action:

1. Switch to `Commercial`.
2. First try an invalid registration number such as `ABC-123`.
3. Then submit a valid application using:
   `SC123456`, `Ava Johnson`, `Community Pharmacy`, `12 Demo Street, Middlesbrough`, `demo.company+01@example.com`.

Expected:

- Invalid registration number is rejected before submission.
- Valid application is accepted and handed off to SA storage.

Evidence:

- Screenshot of invalid validation message.
- Screenshot of successful submission message.

DB check:

```sql
SELECT id, company_registration_number, director_name, business_type, address, email, submission_status, submitted_at
FROM commercial_applications
WHERE email = 'demo.company+01@example.com'
ORDER BY id DESC;
```

Pass condition:

- One row exists with `submission_status = 'SUBMITTED_TO_SA'`.

## 6. SALES Package

### 6.1 Guest/Public Purchase

Action:

1. On the login screen, press `Continue As Guest`.
2. Search for `paracetamol`.
3. Add one promotional item to cart.
4. Checkout using:
   `guest.demo+01@example.com`, a delivery address, `VISA`, `1234`, `5678`, future expiry.

Expected:

- Guest can browse, add to cart, and checkout without logging in.
- Order succeeds and returns a tracking code.

Evidence:

- Screenshot of guest-mode status.
- Screenshot of successful checkout status with tracking code.

DB checks:

```sql
SELECT o.id, o.status, o.tracking_code, o.delivery_address, o.created_at, p.payee_details, p.amount, p.status AS payment_status
FROM orders o
JOIN payments p ON p.order_id = o.id
WHERE p.payee_details = 'guest.demo+01@example.com'
ORDER BY o.created_at DESC;
```

```sql
SELECT oi.order_id, oi.product_id, oi.quantity, oi.discount_percent, oi.line_total
FROM order_items oi
JOIN payments p ON p.order_id = oi.order_id
WHERE p.payee_details = 'guest.demo+01@example.com'
ORDER BY oi.id DESC;
```

Pass condition:

- Order, order items, and payment rows exist.
- Payment payee email matches the guest email used in the app.

### 6.2 Tracking Lookup

Action:

1. Open the `Orders / Tracking` tab.
2. Enter `guest.demo+01@example.com`.
3. Enter the tracking code returned by the successful guest checkout.
4. Press `Track Order`.

Expected:

- Current status is shown even for a guest order.

Evidence:

- Screenshot of tracking lookup result.

### 6.3 Member Loyalty Discount

Action:

1. Log in as the seeded member after password change.
2. Add items to cart and checkout once.

Expected:

- Because the seeded member starts on `9` completed orders after a clean reset, the next order gets the 10 percent loyalty discount.
- The summary section shows subtotal, promotions, loyalty, and final total clearly.

Evidence:

- Screenshot of checkout summary before payment.

DB check:

```sql
SELECT email, completed_order_count
FROM users
WHERE email = 'member@ipos.local';
```

Pass condition:

- `completed_order_count` increases after checkout.

### 6.4 Failed Payment Handling

Action:

1. Attempt checkout with an invalid payment input such as a past expiry or invalid card type.

Expected:

- Checkout fails cleanly.
- User sees a helpful message.
- No successful order confirmation is shown.

Evidence:

- Screenshot of the failure message.

DB check:

```sql
SELECT id, order_id, payee_details, status, message, created_at
FROM payments
ORDER BY id DESC
LIMIT 10;
```

Pass condition:

- Failed payment attempt is recorded with an explanatory message.

## 7. PRM Package

Log in as `admin@ipos.local` / `admin123` before this section.

### 7.1 Create A New Campaign

Action:

1. In the admin campaign editor create a temporary campaign such as:
   name `Demo Campaign`,
   future dates,
   items `P1002:20`.

Expected:

- Campaign is created and appears in the campaign table.

Evidence:

- Screenshot of the new campaign row.

DB checks:

```sql
SELECT id, name, start_time, end_time, status
FROM campaigns
WHERE name = 'Demo Campaign'
ORDER BY id DESC;
```

```sql
SELECT c.name, ci.product_id, ci.discount_percent
FROM campaigns c
JOIN campaign_items ci ON ci.campaign_id = c.id
WHERE c.name = 'Demo Campaign'
ORDER BY ci.product_id;
```

### 7.2 Overlap Blocking

Action:

1. Try to create another campaign that overlaps the active seeded campaign and uses `P1001`.

Expected:

- Conflict warning appears.
- Campaign is not created.

Evidence:

- Screenshot of the conflict warning.

### 7.3 Update Campaign Dates And Discounts

Action:

1. Select the `Demo Campaign` row in the campaign table.
2. Change the dates or change `P1002:20` to `P1002:15`.
3. Press `Update Selected`.

Expected:

- Selected campaign loads into the editor.
- Update succeeds and the campaign row remains selectable.

Evidence:

- Screenshot of editor in update mode.
- Screenshot of success message.

DB check:

```sql
SELECT c.name, ci.product_id, ci.discount_percent, c.start_time, c.end_time, c.status
FROM campaigns c
JOIN campaign_items ci ON ci.campaign_id = c.id
WHERE c.name = 'Demo Campaign'
ORDER BY ci.product_id;
```

### 7.4 Terminate Campaign Early

Action:

1. Select a campaign row.
2. Press `Terminate Now`.

Expected:

- Campaign ends immediately and status reflects that it is no longer active.

Evidence:

- Screenshot of success message.

### 7.5 Delete Campaign

Action:

1. Select the temporary demo campaign row.
2. Press `Delete Selected`.
3. Confirm deletion.

Expected:

- Campaign disappears from the table.
- Linked items and metrics are removed safely.

Evidence:

- Screenshot of the confirmation dialog.
- Screenshot after deletion.

DB checks:

```sql
SELECT id, name, status
FROM campaigns
WHERE name = 'Demo Campaign';
```

```sql
SELECT *
FROM campaign_items
WHERE campaign_id NOT IN (SELECT id FROM campaigns);
```

Pass condition:

- Deleted campaign no longer exists.
- No orphaned campaign item rows remain.

## 8. RPT Package

### 8.1 Sales, Campaign, And Engagement Reports

Action:

1. In the admin reports area set a date range that includes the activity you created during the demo.
2. Generate:
   `Sales Report`,
   `Campaign Report`,
   `Engagement Report`.

Expected:

- Each report renders on screen with rows.

Evidence:

- Screenshot of each report type.

### 8.2 Print Report

Action:

1. With a report visible, press `Print Report`.

Expected:

- Print dialog opens or the selected printer workflow starts.

Evidence:

- Screenshot of the print dialog.

Note:

- If no printer is installed on the machine, showing the app's printer warning message is still useful evidence that the print action exists.

## 9. COMMS Package Evidence

Run this after registration and order flows:

```sql
SELECT id, recipient_email, subject, purpose, created_at
FROM email_outbox
ORDER BY id DESC
LIMIT 20;
```

Pass condition:

- Registration and order confirmation emails are recorded.

## 10. Promotions Metrics Evidence

Run this after opening promotions, adding a promo item, and purchasing it:

```sql
SELECT c.name, cm.product_id, cm.campaign_hits, cm.item_added_count, cm.item_purchased_count
FROM campaign_metrics cm
JOIN campaigns c ON c.id = cm.campaign_id
ORDER BY cm.id DESC;
```

Pass condition:

- `campaign_hits` increases after viewing promotions.
- `item_added_count` increases after adding the promoted item.
- `item_purchased_count` increases after successful checkout.

## 11. Final Evidence Pack To Capture

For the final submission/demo archive, try to leave with:

1. One startup screenshot showing shared DB connection.
2. One invalid login screenshot.
3. One forced password change screenshot.
4. One guest checkout success screenshot with tracking code.
5. One tracking lookup screenshot.
6. One non-commercial registration screenshot.
7. One commercial validation failure screenshot and one success screenshot.
8. One campaign overlap warning screenshot.
9. One campaign update or terminate screenshot.
10. One campaign delete screenshot.
11. One screenshot each for sales, campaign, and engagement reports.
12. One print dialog screenshot.
13. One MySQL screenshot for `users`, `commercial_applications`, `orders`, `payments`, `email_outbox`, and `campaign_metrics`.

## 12. Fast Backup Demo Plan

If time is short during the live assessment, prioritize:

1. Forced password change.
2. Guest checkout.
3. Tracking lookup.
4. Campaign overlap blocking plus update.
5. Reports plus print.
6. One MySQL query proving order, payment, and email rows.
