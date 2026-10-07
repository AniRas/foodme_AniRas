# TC-15: Customers can only see their own orders, and private APIs require sign-in

**Priority:** P0 (security / privacy) | **Type:** Acceptance / Security | **User:** Two customers (A, B), guest

## Objective
Order history and order details belong only to the customer who placed them. Customer and admin APIs refuse requests that aren't signed in or aren't allowed.

## Preconditions
- Two QA accounts, **A** and **B**. A has at least one order `<NA>`. B has none (or different orders)

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Without signing in, call `GET /api/customer/orders` and `GET /api/customer/me` | **401** with no data |
| 2 | Without signing in, call `GET /admin/orders` | **401** with no data |
| 3 | Sign in as **B** and open `/orders` | Only B's orders, or "No orders yet". `<NA>` is **not** listed |
| 4 | As B, call `GET /api/customer/orders` | The response has only B's orders |
| 5 | As B, open `/tracking/<NA>` | Either access is denied, or only non-personal data is shown. A's name, phone, email and address must **not** be shown |
| 6 | Call the storefront token on admin APIs: `GET /admin/orders` with B's token | **401/403**. A customer token never gives admin access |
| 7 | Sign out as B. Press browser Back to `/orders` | Redirects to `/login?next=/orders`. B's data isn't shown from cache |
| 8 | Try to guess order numbers (`/api/order/number/<N±1>`) | No customer personal data is returned for orders the caller doesn't own. Repeated guessing should be rate-limited |

## Pass criteria
No customer can see another customer's personal or order data, and storefront tokens never unlock admin endpoints.

> Prod 2026-10-06: steps 1–2 passed (`/api/customer/orders` → 401, `/admin/orders` → 401). `GET /api/order/number/{n}` is **public** (a missing number returns 404 "Order … not found"). Check what fields it returns for a real order (step 8).
