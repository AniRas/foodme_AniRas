# TC-06: Cart holds one kitchen only, and switching kitchens asks for confirmation

**Priority:** P1 | **Type:** Acceptance / Functional | **User:** Guest

## Objective
A cart only ever holds dishes from one chef. Adding a dish from another chef asks the user to confirm. If they confirm, the old cart is cleared. If they cancel, nothing changes.

## Preconditions
- Cart contains at least 1 item from chef A (e.g. Argentinean `/chef/39`)
- Chef B has an active menu (e.g. Chef Verona `/chef/17`)

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Open chef B's page | The "Your order" panel shows **"Cart has another kitchen"** with a **Go to current cart** button. Header badge still shows chef A's item count |
| 2 | Click **Go to current cart** | `/checkout` opens with chef A's items (or the sign-in panel for guests). Nothing is lost |
| 3 | Go back to chef B and click "+" on any dish (e.g. **Mushroom soup – 1,950 AMD**) | A **"Switch kitchens?"** dialog opens with **Clear & continue** and **Keep cart & browse** |
| 4 | Click **Keep cart & browse** | The dialog closes. The cart still holds only chef A's items and the badge is unchanged |
| 5 | Click "+" on the same dish again, then **Clear & continue** | Chef A's items are removed. The cart now has only Mushroom soup ×1. Subtotal is 1,950 AMD, Delivery is 500 AMD and Total is 2,450 AMD. Badge shows 1 |
| 6 | Do the same from the dish details modal (open a dish, then **Add to cart**) | The same "Switch kitchens?" dialog appears. The selected quantity and add-ons are kept after **Clear & continue** |
| 7 | Reload the page | The cart still holds only chef B's item |

## Pass criteria
Dishes from two chefs never end up in the same cart, and nothing is cleared until the user confirms.

> Verified on prod 2026-10-06: steps 1, 3, 4, 5 and 6 passed.
