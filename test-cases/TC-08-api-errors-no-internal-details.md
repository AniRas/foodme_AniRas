# TC-08: API errors return the right status and hide internal details

**Priority:** P1 | **Type:** Acceptance / Security + API contract | **User:** Guest

## Objective
When a request is wrong, the API answers with a 4xx code and a short message. It never answers with a 500 for a client mistake and never sends stack traces or class names to the browser.

## Preconditions
- None. All calls are made with `curl`

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | `GET /api/dish/search` (missing `query`) | **400** with a short message. No `trace` field |
| 2 | `GET /api/dish/tags` (missing `chefId`) | **400**. No `trace` field |
| 3 | `POST /api/order/delivery-price` with `{}` | **400** ("chefId is required" or similar). No `trace` field |
| 4 | `POST /api/order/delivery-price` with `"chefId":99999` | **404** "Chef 99999 not found". No `trace` field |
| 5 | `GET /api/chef/99999` | **404**. No `trace` field |
| 6 | `GET /api/chef/32` (chef with incomplete data, see TC-13) | Not a 500 with a stack trace. Either the chef or a clean 404 |
| 7 | For every response above, search the body for `trace`, `java.`, `Exception`, `am.foodme` | None of them appear |
| 8 | `POST /api/order/delivery-price` with `"subtotal":-100` | **400** (negative subtotal rejected), or the fee is still correct. No 500 |

## Pass criteria
Client mistakes get 4xx codes, and no response body contains a stack trace or internal class names.

> ❌ **Fails on prod 2026-10-07:** steps 1, 2, 3 and 6 return **500** with a full Java stack trace (about 18 KB) in the body. Step 6 shows `NullPointerException ... Dish.getNameAm() is null`. Step 4 returned a clean 404. Step 8 returned `200` with the normal 500 AMD fee.
>
> Note: `GET /api/debug/boom` returning 500 is a deliberate test endpoint for the error tracker and is not part of this case.
