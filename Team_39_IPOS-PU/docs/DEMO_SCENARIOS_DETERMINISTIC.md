# Deterministic Demo Scenarios (From Clean Reset)

Run from clean state:
1. execute `db/reset.sql`
2. restart app to apply `db/schema.sql` and `db/seed.sql`

## Scenario 1: Login Validation

- Attempt login with invalid email/password.
- Expected: error message shown, no app tabs unlocked.

## Scenario 2: First Login Forced Password Change

- Login as `member@ipos.local` / `Temp@1234`.
- Expected: forced password change prompt appears.
- Set new password + confirm.
- Expected: password updated, first-login flag cleared, app opens.

## Scenario 3: Non-Commercial Registration

- Register a new non-commercial email.
- Expected:
  - new `users` row with `first_login_required = TRUE`
  - new `email_outbox` row with registration purpose

## Scenario 4: Commercial Application Submission

- Use Commercial registration mode with all required fields.
- Expected:
  - row inserted in `commercial_applications`
  - status reflects submission to SA handoff

## Scenario 5: Catalogue + Cart + Promotions

- Search for `paracetamol`.
- Add quantity to cart.
- Open promotions page/link.
- Expected:
  - matching products filtered
  - cart total reflects campaign discount if active
  - campaign hit/add counters increment

## Scenario 6: Checkout Success

- Use valid card fragments (`first4`, `last4`) and address.
- Complete checkout.
- Expected:
  - rows in `orders`, `order_items`, `payments`
  - promo purchased counters increment for promo items
  - order appears in order tracking table

## Scenario 7: Race-Condition Stock Failure

- Force low stock and attempt checkout with excess quantity.
- Expected:
  - stock failure message
  - unavailable item removed from cart
  - no successful order confirmation

## Scenario 8: Campaign Admin Rules

- Create campaign with overlapping item already active.
- Expected: creation blocked with overlap message.
- Cancel active campaign.
- Expected: campaign status updated and no longer active.

## Scenario 9: Reports On Screen

- Generate all three reports over chosen date period:
  - Sales
  - Campaign
  - Engagement
- Expected: each report displays in table form first (on screen).
