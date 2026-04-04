# IPOS-PU Test Plan (Part 2)

## Unit Tests

- `SecurityUtilTest`
  - verifies generated password length and required character classes.
- `UserTest`
  - verifies 10th-order loyalty condition for non-commercial members.
- `PaymentServiceTest`
  - verifies payment acceptance/rejection logic in simulated payment processor.
- `OrderServiceTest`
  - verifies loyalty total calculation and checkout guard rails (not logged in, empty cart).

## Component/Subsystem Tests (Manual)

1. Login:
   - invalid credentials -> error message.
   - valid credentials -> app tabs become available.
2. Non-commercial registration:
   - valid email -> account created and email record in `email_outbox`.
3. First-login password change:
   - login with first-login account -> forced password dialog appears.
4. Commercial registration:
   - valid business fields -> row inserted into `commercial_applications`.
5. Catalogue and search:
   - keyword filter returns only matching products.
6. Cart and discounts:
   - active campaign discount applies to eligible items.
   - 10th-order discount applies for non-commercial member at order count 9.
7. Checkout:
   - successful payment creates rows in `orders`, `order_items`, `payments`.
   - stock race failure removes unavailable items from cart.
8. Promotions:
   - promotions view increments `campaign_hits`.
   - add-to-cart increments `item_added_count`.
   - paid checkout increments `item_purchased_count`.
9. Reports:
   - Sales report renders aggregated item rows.
   - Campaign report renders campaign rows by date.
   - Engagement report renders hits/purchases/conversion.

## Demo Readiness Notes

- Run from a clean database state (`reset.sql` + seed).
- Capture screenshots of each flow and include them in the implementation report appendix.
- Execute deterministic scenario sheet in `docs/DEMO_SCENARIOS_DETERMINISTIC.md`.
