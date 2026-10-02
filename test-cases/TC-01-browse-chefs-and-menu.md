# TC-01: Browse chefs and open a chef's menu

**Priority:** P1 | **Type:** Acceptance / Functional | **User:** Guest

## Objective
A visitor can find chefs from the home page and see a chef's full menu with prices.

## Preconditions
- Production is reachable at https://foodme-aniras.onrender.com/
- At least one active chef with menu items exists

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Open `/` | Hero "Real food, made by real people near you." is shown, plus "Explore chefs", "How it works", "Order now" and a "Chefs worth knowing" section |
| 2 | Click **Order now** (or **Explore chefs**) | User lands on `/explore`. Loading skeletons are replaced by chef cards within ~5s |
| 3 | Check the explore header and cards | The "N chefs cooking near you" count **matches the number of cards shown**. Each card shows image, name, "25–40 min", delivery-fee badge and rating/"New" badge |
| 4 | Type a chef name (e.g. `Sakura`) in the header search and press Enter | URL becomes `/explore?q=Sakura`. Only matching chefs are shown and the count updates to match |
| 5 | Clear the search (✕) | The full chef list is back |
| 6 | Click a chef card (e.g. Sakura Kitchen) | `/chef/:id` opens with banner, name, ETA, "500 AMD delivery · free from 5,000 AMD", Takeaway, a clickable `tel:` phone link and description |
| 7 | Scroll the menu | Items are grouped by category. Each item shows name, price in AMD, weight/portion and a "+" add button. No broken images |

## Pass criteria
All expected results are met, there are no console errors, and no blank or incorrect counts.

> ⚠️ **Known failure (2026-10-03):** step 3 shows "6 chefs" with 5 cards, and step 4 keeps saying "6 chefs" when only 1 result is shown.
