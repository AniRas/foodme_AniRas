# TC-09: Order result and tracking pages handle wrong or missing order numbers

**Priority:** P1 | **Type:** Acceptance / Negative | **User:** Guest

## Objective
The pages a customer lands on after checkout (`/orders/success`, `/orders/failed`, `/tracking/:number`) never confirm an order that doesn't exist, never crash and never show someone else's data.

## Preconditions
- Signed out

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Open `/orders/success` (no `number`) | Doesn't claim an order was placed. Offers a way back (Explore / Orders) |
| 2 | Open `/orders/success?number=FAKE-000` | Doesn't show "Order placed!" for an order that doesn't exist. Either checks the number or shows a neutral message |
| 3 | Open `/orders/success?number=<b>bold</b>` | The text is shown literally. No HTML is rendered and no script runs |
| 4 | Open `/orders/failed` | "Order failed" with "Your cart is still available". The cart is unchanged |
| 5 | Open `/tracking/INVALID123` | **"Couldn’t load this order"** with **Try again** and **Back to explore**. No crash, no data from another order |
| 6 | Click **Back to explore** | `/explore` opens with chef cards |
| 7 | Check the console during steps 1–6 | No uncaught errors |

## Pass criteria
Result pages only confirm real orders, render user-supplied text safely and recover cleanly from bad links.

> ⚠️ **Known issue K-4:** from the code (`pages/OrderStatus/index.tsx`), `/orders/success?number=ANYTHING` shows "Order placed!" without checking that the order exists, so step 2 is expected to fail. React escapes the number, so step 3 is expected to pass.
