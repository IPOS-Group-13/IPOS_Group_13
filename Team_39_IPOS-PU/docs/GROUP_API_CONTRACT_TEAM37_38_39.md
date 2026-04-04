# Group API Contract (Teams 37, 38, 39)

This document freezes the integration contract for Group 13:

- Team 37: `IPOS-SA`
- Team 38: `IPOS-CA`
- Team 39: `IPOS-PU`

## Ownership

- `IPOS-PU` provides:
  - `ICommsAPI` (generic outbound email service)
  - `IPaymentAPI` (generic payment service abstraction)
- `IPOS-CA` provides:
  - `IInventoryAPI` (catalogue, stock verification, stock deduction and order propagation)
- `IPOS-SA` provides:
  - `IMemberAPI` (commercial application intake from PU)

## Interface Signatures (Contract Freeze)

### ICommsAPI (PU provided)

- `boolean sendEmail(String recipientEmail, String subject, String body)`

### IPaymentAPI (PU provided)

- `PaymentResult processPayment(PaymentRequest paymentRequest)`

### IInventoryAPI (CA provided, consumed by PU)

- `List<InventoryItemDto> getCatalogue()`
- `boolean checkStock(String productId, int requestedQty)`
- `StockReservationResult reserveOrReject(List<CartLineDto> lines)`
- `OnlineOrderResult submitOnlineOrder(OnlineOrderRequest request)`
- `OrderStatusDto getOrderStatus(String orderId)`

### IMemberAPI (SA provided, consumed by PU)

- `boolean submitCommercialApplication(CommercialApplicationDto application)`

## Data Contract Notes

- `productId`, `orderId`, `campaignId`, `userId` are stable immutable identifiers.
- Monetary values are represented as decimal doubles in this prototype.
- `orderStatus` lifecycle must support:
  - `RECEIVED`
  - `DISPATCHED`
  - `DELIVERED`
  - `VOID`
- If payment fails or final stock check fails, order must not be confirmed.
- Promo metrics semantics:
  - `campaignHits`: increments when user opens promotions view/campaign.
  - `itemAddedCount`: increments by quantity added to basket.
  - `itemPurchasedCount`: increments by quantity only for paid/confirmed orders.

## Transport

This prototype supports adapter-based integration:

- Preferred: local HTTP + JSON between desktop apps.
- Fallback: shared MySQL tables if live service unavailable.

All teams must preserve the method semantics and payload fields even if transport differs.

## Version

- Contract version: `v1.0-freeze`
- Date: `2026-03-23`
