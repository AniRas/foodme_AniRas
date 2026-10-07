# TC-11: Explore search: partial matches, no results and clearing filters

**Priority:** P1 | **Type:** Acceptance / Functional | **User:** Guest

## Objective
Searching from the header finds chefs by partial, case-insensitive names, shows a clear empty state when nothing matches, and always gives a way back to the full list.

## Preconditions
- 5 chefs are shown on `/explore` (Argentinean, Alans Kitchen, Sakura Kitchen, Chef Verona, Italiano Margarino)

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Search `sak` | URL `/explore?q=sak`. Only **Sakura Kitchen** is shown |
| 2 | Search `SAKURA` | The same single result (case doesn't matter) |
| 3 | Search `zzzz` | **"No chefs match"**, "Try another filter or clear search." and a **Clear filters** button. No cards |
| 4 | Click **Clear filters** | URL is `/explore`, the search box is empty and all chef cards are back |
| 5 | Search `<b>x</b>` | Shown as plain text. "No chefs match". Nothing is rendered as HTML |
| 6 | Search `soup` (a dish, not a chef) | Decide and record: either chefs that serve soup are shown, or "No chefs match" |
| 7 | While a search is active, check "N chefs cooking near you" | N equals the number of cards shown |
| 8 | With a search active, click the ✕ in the search box | The full list comes back (same as **Clear filters**) |

## Pass criteria
Matching is partial and case-insensitive, the empty state always has a working way out, and the count matches the cards.

> Observed on prod 2026-10-07: steps 1–5 passed. Step 6: `soup` gives "No chefs match", because search only checks chef names even though `/api/dish/search` exists. **Fails:** step 7 always says "6 chefs" (known issue K-1), and step 8 clears the text but keeps the filtered list (found in the 2026-10-07 regression run).
