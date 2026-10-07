# TC-07: Add-ons and quantity in the dish details modal

**Priority:** P1 | **Type:** Acceptance / Functional | **User:** Guest

## Objective
Paid add-ons and the quantity picked in the dish modal are priced correctly, shown in the cart and checkout, and kept separate from the same dish without add-ons.

## Preconditions
- Cart is empty
- Alans Kitchen `/chef/24`. **Yassai curry, 2,900 AMD**, has add-ons: Soy Sauce +100, Sweet Chile +200, Unagi +200, Teriyaki +200 and several free ones

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Click the **Yassai curry** card (not "+") | A modal opens with image, name, portion (450g), an **Additions** list with "+N AMD" prices, a quantity stepper (1) and **Add to cart · 2,900 AMD** |
| 2 | Tick **Soy Sauce** and **Sweet Chile** | The button updates to **2,900 + 300 = 3,200 AMD** |
| 3 | Click **Increase quantity** | Qty is 2 and the button shows **6,400 AMD** |
| 4 | Untick and re-tick an add-on, and try to go below the minimum quantity | The price recalculates each time. Qty can't go below 1 (or the dish's `minimumOrderCount`) |
| 5 | Click **Add to cart** | The modal closes. The cart line shows "Yassai curry · 6,400 AMD · 3,200 AMD each · **+ Soy Sauce, Sweet Chile**" with qty 2. Delivery is Free because 6,400 is over 5,000 |
| 6 | Quick-add Yassai curry with "+" (no add-ons) | It is added as a **separate line** at 2,900 AMD and not merged with the add-ons line |
| 7 | Close the modal with ✕ or Esc without adding | Nothing is added to the cart |
| 8 | Open `/checkout` (signed in) | The order summary lists the add-ons under the dish, and the totals match the cart |

## Pass criteria
Unit price = dish price + selected add-ons. Line total = unit × qty. Add-ons are shown everywhere the line appears.

> Verified on prod 2026-10-06: steps 1, 2, 3 and 5 passed.
> ⚠️ **Data issue:** `GET /api/dish/24/active` returned 7 add-ons for *Miso Soup* on one call and 0 on later calls, so add-on availability isn't stable. Check against the admin dish data before running.
