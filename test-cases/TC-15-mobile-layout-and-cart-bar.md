# TC-15: Phone-size layout: no sideways scrolling, cart reachable

**Priority:** P1 | **Type:** Acceptance / Responsive UI | **User:** Guest

## Objective
On a phone-size screen, every page fits the width, and the cart and checkout are always one tap away.

## Preconditions
- Chrome DevTools device mode at **375 × 812** (iPhone-size). A normal window can't be made that narrow
- Cart is empty

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Open `/` | Header shows logo, **Sign in** and cart badge. The search box moves to its own row. No sideways scrolling |
| 2 | Open `/explore` | Chef cards are in one column at full width. No sideways scrolling |
| 3 | Open `/chef/39` | Banner, name, ETA, "500 AMD delivery · free from 5,000 AMD" and phone wrap cleanly. Menu cards don't overflow |
| 4 | Add **Mini Corn Dog** with "+" | A bar at the bottom of the screen shows "1 item", "1,000 AMD" and **Checkout** |
| 5 | Scroll the menu | The bottom bar stays visible and doesn't cover the last dish's "+" button |
| 6 | Tap **Checkout** in the bar | `/checkout` opens (Sign in panel for guests) |
| 7 | Open a dish modal | The modal fits the screen. **Add to cart** and ✕ are reachable without zooming |
| 8 | On each page, run `document.documentElement.scrollWidth <= innerWidth` in the console | `true` |
| 9 | Remove the item | Cart is empty and the bottom bar disappears |

## Pass criteria
No page scrolls sideways at 375 px, and the cart and checkout are always reachable.

> Partly observed on prod 2026-10-07 at Chrome's minimum window width (609 px): no sideways scrolling on `/chef/39`, and the bottom bar showed "1 item · 1,000 AMD · Checkout". Not yet run at 375 px.
