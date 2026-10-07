# TC-08: The selected payment method is the one recorded on the order

**Priority:** P0 (money / order integrity) | **Type:** Acceptance / Functional | **User:** Registered user

## Objective
The Payment section offers **Cash on delivery**, **Bank card** and **Idram**. The method the user chooses must be the one saved on the order. If a method isn't supported yet, the user must not be able to select it.

## Preconditions
- Signed in with the QA account. Cart has items
- DevTools Network tab is open to inspect `POST /api/order`
- ⚠️ Creates real orders. Mark them as TEST and reject or clean them up in the backoffice afterwards

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Open `/checkout` and scroll to **Payment** | Three options are shown. **Cash on delivery** is selected by default |
| 2 | Select **Bank card** | Either (a) the card flow or fields appear, or (b) the option is disabled / marked "coming soon" and can't be selected. It must **not** look selected while still being sent as cash |
| 3 | Select **Idram** | Same rule as step 2 |
| 4 | With **Cash on delivery** selected, place the order | `POST /api/order` body has `"paymentType":"CASH"`. Confirmation page opens |
| 5 | If Card or Idram can be selected, place an order with it | The request `paymentType` equals the selected method (`CARD` / `IDRAM`). Backoffice order details show the same method |
| 6 | Open the order in the backoffice (`/backoffice` → Orders → order) | The payment type matches what the customer chose |

## Pass criteria
The payment method shown to the customer, in the API request and in the backoffice is always the same.

> ⚠️ **Likely failure (from code review, 2026-10-06):** `apps/web/src/pages/Checkout/index.tsx` always sends `paymentType: "CASH"`, but `order-delivery-form.tsx` lets the user pick Bank card or Idram. A customer who chooses card would get a cash order without being told.
