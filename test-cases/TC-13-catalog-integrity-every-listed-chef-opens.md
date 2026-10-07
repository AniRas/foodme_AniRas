# TC-13: Every chef listed on Explore opens and has a menu

**Priority:** P0 | **Type:** Acceptance / Data integrity | **User:** Guest

## Objective
Every chef card on Explore and the home page leads to a working chef page with at least one dish you can order. Broken or empty kitchens are never shown to customers.

## Preconditions
- None (guest)

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Open `/explore` and note every chef card link (`/chef/:id`) | A list of IDs, e.g. 39, 24, 21, 17, 32 |
| 2 | Open each chef link | Every page loads the chef details. **None** shows "Chef not found" |
| 3 | On each chef page, check the menu | At least one active dish with a price and a working "+" button. No "No dishes available right now" for a chef that appears on Explore |
| 4 | Compare with the API: `GET /api/chef/active` and `GET /api/dish/{id}/active` | Every chef in the active list returns `200` from `GET /api/chef/{id}` and has `count > 0` active dishes |
| 5 | Repeat steps 1–3 for the home page "Chefs worth knowing" cards | Same results |
| 6 | Open a chef ID that doesn't exist (`/chef/99999`) | "Chef not found – This kitchen may be offline or the link is outdated" with a way back (link to Explore or the header still works) |
| 7 | Open an unknown route (`/this-page-does-not-exist`) | Redirects to `/` with no blank screen |

## Pass criteria
Customers never reach a dead-end kitchen from a listing, and invalid links fail cleanly.

> ❌ **Fails on prod 2026-10-06:** **Italiano Margarino** (`/chef/32`) is shown on `/explore`, but its page says **"Chef not found"**, and `GET /api/dish/32/active` returns 0 dishes. This is likely related to the "6 chefs / 5 cards" mismatch in TC-01. Also, the "Chef not found" page has no back/Explore button.
