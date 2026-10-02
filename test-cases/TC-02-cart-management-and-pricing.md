# TC-02: Cart management and price calculation

**Priority:** P1 | **Type:** Acceptance / Functional | **User:** Guest

## Objective
Users can add, change and remove dishes. Subtotal, delivery fee, total and the header badge stay correct, and the cart survives a page reload.

## Preconditions
- Cart is empty (remove items or use a fresh browser profile)
- Chef with menu, e.g. `/chef/39` (delivery 500 AMD, free from 5,000 AMD)

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Open the chef page | The "Your order" panel is empty or shows an empty state. Header cart badge shows 0 or no count |
| 2 | Click "+" on a low-priced item (e.g. **Mini Corn Dog – 500 AMD**) | Item appears in "Your order" with qty 1. Subtotal is 500 AMD, **Delivery is 500 AMD**, Total is 1,000 AMD. Header badge shows 1 |
| 3 | Click "+" on **Philadelphia Lux – 6,000 AMD** | Both items are listed. Subtotal is 6,500 AMD and goes over the 5,000 AMD threshold, so **Delivery is Free** and Total is 6,500 AMD. Badge shows 2 |
| 4 | Click **Increase quantity** on Philadelphia Lux | Qty is 2. Line shows "12,000 AMD · 6,000 AMD each". Subtotal and Total are 12,500 AMD. Badge shows 3 |
| 5 | Click **Decrease quantity** on Philadelphia Lux twice | The item is removed when qty reaches 0. Subtotal is 500 AMD and Delivery goes back to 500 AMD |
| 6 | Reload the page | Cart contents and totals are unchanged |
| 7 | Click the **Remove item** (trash) icon | The item is removed, the cart is empty and the badge is cleared |

## Pass criteria
All amounts are calculated correctly (line × qty, subtotal, threshold-based delivery fee, total), and the badge always matches the item count.

> Verified on prod 2026-10-03: add, increase, line total ("each" price), decrease to remove, and free delivery above 5,000 AMD all behaved correctly. The below-threshold fee in step 2 was not observed and still needs to be verified.
