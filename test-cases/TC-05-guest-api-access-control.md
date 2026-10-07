# TC-05: Guests can't read or create customer data through the API

**Priority:** P0 | **Type:** Acceptance / Security (API) | **User:** Guest (no token)

## Objective
Without a sign-in, the storefront API refuses every customer-only action and doesn't hand out other people's orders.

## Preconditions
- None. All calls are made with `curl` and **no** `Authorization` header
- Order numbers have the format `FM-<6 digits>` and are given out in sequence (`FM-100001`, `FM-100002`, …)

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | `GET /api/customer/me` | **401**, empty body |
| 2 | `GET /api/customer/orders` | **401**, empty body |
| 3 | `POST /api/order` with any JSON body | **401**. No order is created |
| 4 | `GET /api/customer/orders` with `Authorization: Bearer invalid.token.value` | **401** |
| 5 | `GET /api/order/number/INVALID123` | **404** "Order INVALID123 not found". No other order's data |
| 6 | `GET /api/order/number/FM-100001` … `FM-100010` (sequential guesses) | No order with personal data (receiver name, phone, email, address) is returned to a guest: **401/403/404** for every number |
| 7 | Open `/tracking/FM-100001` in a signed-out browser | Either a sign-in is required, or only non-personal data (status, items) is shown |

## Pass criteria
No customer-only endpoint works without a valid token, and order details can't be read by guessing order numbers.

> ⚠️ **Risk (2026-10-07):** `GET /api/order/number/{number}` is `permitAll` in `SecurityConfig` and numbers are sequential. On prod, guesses `FM-100001`, `FM-100002`, `FM-100010` and `FM-100050` all returned 404 (no such orders), so no data was exposed, but any existing order would be readable by a guest. Steps 1, 2 and 5 passed.
