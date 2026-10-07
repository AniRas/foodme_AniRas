# TC-12: Backoffice order status changes are shown to the customer

**Priority:** P0 | **Type:** Acceptance / E2E (admin + storefront) | **User:** Admin + Registered customer

## Objective
An admin can move an order through its allowed statuses. Each change appears on the customer's tracking page and order history. Statuses that aren't allowed can't be set.

## Preconditions
- A fresh TEST order `<N>` in status **NEW** (from TC-05)
- Admin access to `/backoffice` (training sandbox account)
- Allowed transitions: NEW → ACCEPTED | REJECTED; ACCEPTED → DELIVERED | REJECTED; DELIVERED and REJECTED are final

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Sign in to `/backoffice` and open **Orders** | The list shows order `<N>` with a **NEW** chip. The status filter lists NEW/ACCEPTED/DELIVERED/REJECTED |
| 2 | Open order `<N>` | Details show items, totals, receiver and address. Only **ACCEPTED** and **REJECTED** actions are offered |
| 3 | Click **ACCEPTED** | "Order status updated" toast. Chip shows ACCEPTED. Only DELIVERED and REJECTED are offered now |
| 4 | As the customer, reload `/tracking/<N>` and `/orders` | Tracking: **"Preparing your order"**, with step 2 active. Orders list: amber **Preparing** badge |
| 5 | In the backoffice, click **DELIVERED** | Chip shows DELIVERED. **No more actions** are offered |
| 6 | Customer reloads tracking and orders | **"Delivered"**, all 3 steps complete, green **Delivered** badge |
| 7 | On a second NEW order, click **REJECTED** and confirm with an **empty reason** | Warning "A rejection reason is required". Status doesn't change |
| 8 | Enter a reason and confirm | Status is REJECTED. Customer tracking shows **"Order declined"** with the ✕ state, and the orders badge is red **Declined** |
| 9 | Try a disallowed change through the API (e.g. DELIVERED → NEW) | The server rejects it (4xx). Status doesn't change |

## Pass criteria
Only allowed transitions are possible, a rejection always has a reason, and the customer sees each status change after a reload.

> Not run on prod. It needs admin sign-in and a real order. Run with the sandbox admin and TEST orders only.
