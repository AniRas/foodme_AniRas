# TC-14: Each chef's menu on the page matches the API

**Priority:** P1 | **Type:** Acceptance / Data integrity | **User:** Guest

## Objective
What a customer sees on a chef page (dishes, prices, portions, categories) is exactly what the server will charge for. No missing dishes, no stale prices.

## Preconditions
- None. Use the chefs that open correctly: 39, 24, 21, 17

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | `GET /api/dish/{chefId}/active` for each chef and note `count` | 39 → 41, 24 → 136, 21 → 35, 17 → 49 (as of 2026-10-07; update if the catalog changes) |
| 2 | Open `/chef/{chefId}` and count the "Add … to cart" buttons | Equals `count` from step 1 for each chef |
| 3 | For 5 dishes per chef, compare the price on the card with `price` in the API | Same amount, shown in AMD with a thousands separator (e.g. `2,900 AMD`) |
| 4 | Compare the portion text on the card with `portionEn` | Shown when `portionEn` is set. When it's empty, nothing is shown and the layout doesn't break |
| 5 | `GET /api/dish/tags?chefId={chefId}` and compare with the category headings on the page | Same categories, in `priorityIndex` order |
| 6 | Open one dish modal per chef | Name, price and portion match the card |

## Pass criteria
The menu on the page and the API agree on every dish, price and category.

> Not run yet. On 2026-10-07 the step 2 counts matched step 1 for all four chefs. Note that `/api/dish/{chefId}/active` is paged (50 per call), so use `count`, not the list length.
