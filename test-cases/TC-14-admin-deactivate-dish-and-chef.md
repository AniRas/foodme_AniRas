# TC-14: Deactivating a dish or chef in the backoffice hides it on the storefront

**Priority:** P1 | **Type:** Acceptance / E2E (admin + storefront) | **User:** Admin + Guest

## Objective
Setting a dish or chef to **INACTIVE** in the backoffice removes it from the storefront, and customers can't order it. Setting it back to **ACTIVE** restores it.

## Preconditions
- Admin access to `/backoffice`
- A test dish (e.g. **Mushroom soup** at Chef Verona `/chef/17`) and a test chef you're allowed to toggle
- ⚠️ This changes live catalog data. Coordinate the time, and always restore it to ACTIVE at the end

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | As a guest, add **Mushroom soup** to the cart | It is in the cart |
| 2 | Backoffice → **Dishes** → Mushroom soup → Status **INACTIVE** → Save | Saved. The list chip shows INACTIVE |
| 3 | Guest reloads `/chef/17` | Mushroom soup is no longer in the menu. The count from `GET /api/dish/17/active` drops by 1 |
| 4 | Guest (dish still in local cart) signs in and tries to place the order | The order is rejected with a clear message ("item no longer available"), or the item is removed before the order is sent. An inactive dish is never ordered |
| 5 | Backoffice → **Chefs** → Chef Verona → **INACTIVE** → Save | Saved |
| 6 | Guest reloads `/explore` and `/` | Chef Verona isn't listed. The "N chefs" count goes down by 1. `/chef/17` shows "Chef not found" |
| 7 | Restore the chef and the dish to **ACTIVE** | Both appear again on the storefront after a reload |

## Pass criteria
The storefront always reflects the admin's active/inactive status, and inactive items can't be ordered.

> Not run on prod: it needs admin sign-in and changes live data.
