# TC-11: Order confirmation and tracking page

**Priority:** P0 | **Type:** Acceptance / E2E | **User:** Registered user (+ guest for negative checks)

## Objective
After an order is placed, the confirmation page leads to a tracking page with the correct status, items and total. Invalid or fake order numbers are handled safely.

## Preconditions
- An order was just placed with the QA account (see TC-05), order number = `<N>`

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Check the success page `/orders/success?number=<N>` | ✓ icon, **"Order placed!"**, "Your order number is **<N>**", plus **Track order**, **View my orders** and **Back to explore** |
| 2 | Click **Track order** | `/tracking/<N>` opens with "Order <N>", "Chef · <chef name>" and an **All orders** link (signed in) |
| 3 | Check the status block | Status is **"Order received"**. The step bar shows Received (active), Preparing and Delivered (pending) |
| 4 | Check the items list | Each line shows "qty × name" and qty × unit price. **Total** matches the checkout total (including the delivery fee, if any) |
| 5 | Open `/tracking/INVALID123` | **"Couldn’t load this order"** with **Try again** and **Back to explore**. No crash and no data from another order |
| 6 | Open `/orders/success?number=FAKE-999` (no such order) | The page should not confirm an order that doesn't exist. Either it validates the number or shows a neutral message |
| 7 | Open `/orders/failed` | **"Order failed"**, "Your cart is still available", and **Try checkout again** leads to `/checkout` with the cart intact |
| 8 | Open `/tracking/<N>` as a guest (signed out, other browser) | Matches the agreed privacy rule: either sign-in is required, or only non-personal data (status, items) is shown. No receiver phone, email or address |

## Pass criteria
The confirmation and tracking pages show the right order and total, errors are handled cleanly, and order data isn't exposed to people who shouldn't see it.

> Prod 2026-10-06: steps 5 and 7 passed. **Step 6 is expected to fail:** `OrderStatus/index.tsx` shows "Order placed!" for any `number` query value without checking it, so anyone can produce a fake success page with a URL.
