# FoodMe – Critical Acceptance Test Cases

**Environment:** Production – https://foodme-aniras.onrender.com/
**Explored on:** 2026-10-03, 2026-10-06 and 2026-10-07. Chrome desktop, guest session

| ID | Title | Priority | Flow |
|----|-------|----------|------|
| [TC-01](TC-01-browse-chefs-and-menu.md) | Browse chefs and open a chef's menu | P1 | Discovery |
| [TC-02](TC-02-cart-management-and-pricing.md) | Cart management and price calculation | P1 | Cart |
| [TC-03](TC-03-auth-gating-and-redirect.md) | Checkout/Orders require sign-in and redirect back | P1 | Auth gating |
| [TC-04](TC-04-free-delivery-threshold-boundary.md) | Free-delivery threshold is the same in the cart and on the server | P0 | Pricing |
| [TC-05](TC-05-guest-api-access-control.md) | Guests can't read or create customer data through the API | P0 | Security (API) |
| [TC-06](TC-06-single-kitchen-cart-switch.md) | Cart holds one kitchen; switching asks for confirmation | P1 | Cart |
| [TC-07](TC-07-dish-additions-and-quantity.md) | Add-ons and quantity in the dish modal | P1 | Cart / Menu |
| [TC-08](TC-08-api-errors-no-internal-details.md) | API errors return the right status and hide internal details | P1 | Security / API |
| [TC-09](TC-09-order-result-and-tracking-pages-guest.md) | Order result and tracking pages handle wrong or missing order numbers | P1 | Post-order |
| [TC-10](TC-10-registration-form-validation.md) | Registration form rejects invalid details before submitting | P1 | Authentication |
| [TC-11](TC-11-explore-search-and-no-results.md) | Explore search: partial matches, no results and clearing filters | P1 | Discovery |
| [TC-12](TC-12-backoffice-blocks-guests.md) | Backoffice and admin API are closed to guests | P0 | Security (Admin) |
| [TC-13](TC-13-catalog-integrity-every-listed-chef-opens.md) | Every chef listed on Explore opens and has a menu | P0 | Discovery / Data |
| [TC-14](TC-14-menu-matches-api.md) | Each chef's menu on the page matches the API | P1 | Data integrity |
| [TC-15](TC-15-mobile-layout-and-cart-bar.md) | Phone-size layout: no sideways scrolling, cart reachable | P1 | Responsive UI |

All 15 cases run as a **guest** and don't create or change data on production. On 2026-10-07, the 9 cases that needed a sign-in (register, place order, payment, takeaway, checkout contact, confirmation, admin status, admin deactivate, order access) were replaced. They are still in git history (commit `e8fb68a`) for a later run on staging or local. TC-03 steps 3 and 5–8 and TC-07 step 8 still need a QA account.

## Flows mapped

- **Home (`/`)**: hero, "Explore chefs" / "Order now" CTAs, "How it works", featured chefs
- **Explore (`/explore`, `/explore?q=`)**: chef grid, header search (chef names only), "No chefs match" + **Clear filters**
- **Chef page (`/chef/:id`)**: info (ETA, delivery fee, free-delivery threshold, phone), menu by category, "Your order" side cart
- **Checkout (`/checkout`)**: guests see a Sign in / Create account panel
- **Auth (`/login?next=`, `/register?next=`)**: email + password; register adds full name + phone (+374)
- **Orders (`/orders`)**: redirects guests to `/login?next=/orders`
- **Payment**: UI offers Cash on delivery, Bank card and Idram, but the client always sends `CASH`
- **Dish modal**: add-ons (checkboxes with +price), quantity stepper, "Switch kitchens?" dialog when the cart holds another chef
- **Checkout (signed in)**: Delivery/Takeaway, contact (name/phone/email), address (city/street required for delivery), note, payment
- **Post-order**: `/orders/success?number=`, `/orders/failed`, `/tracking/:number` (Received → Preparing → Delivered / Declined)
- **Public APIs (guest)**: `/api/chef/**`, `/api/dish/**`, `POST /api/order/delivery-price`, `GET /api/order/number/{n}`, `GET /admin/dish/**`. Customer APIs `/api/customer/**` and `POST /api/order` need a token
- **Backoffice (`/backoffice`)**: Orders (status transitions, reject needs a reason), Chefs and Dishes (ACTIVE/INACTIVE)

## Observations from exploration (worth raising)

1. **Chef count mismatch:** `/explore` says "6 chefs cooking near you" but shows only 5 cards. With `?q=Sakura` it still says "6 chefs" while showing 1 result. TC-01 and TC-11 fail on this.
2. **Chef data mismatch:** the chef called "Argentinean" (`/chef/39`) serves only sushi, poke, wok and onigiri. Its description also contains placeholder/test text instead of a real description.
3. **Header "Sign in" link:** on `/login?next=/checkout` and `/register`, the header link points to `next=/orders` and not to the current `next` target.
4. **Listed chef can't be opened (TC-13):** Italiano Margarino (`/chef/32`) appears on `/explore` but its page shows "Chef not found", and on 2026-10-07 the API returns 500 with a stack trace.
5. **Payment choice is ignored:** Bank card and Idram can be selected, but `Checkout/index.tsx` hard-codes `paymentType: "CASH"`.
6. **Fake success page (TC-09):** from the code, `/orders/success?number=ANYTHING` shows "Order placed!" for any value without checking that the order exists.
7. **Unstable add-on data (TC-07):** `GET /api/dish/24/active` returned add-ons for Miso Soup on one call and none on later calls.
8. **Public order lookup (TC-05):** `GET /api/order/number/{n}` doesn't require sign-in, and order numbers are sequential (`FM-100001`, …), so any order can be guessed.
9. **Backoffice login page shows the admin credentials (TC-12)** to everyone on the production URL. That's fine for a training sandbox, but it must never happen in a real production environment.
10. **Quick-add overcharges (TC-07):** "+" on a dish card adds every add-on the dish offers (Yassai curry: 3,600 AMD instead of 2,900).
11. **Search ✕ doesn't reset results (TC-01, TC-11):** it clears the text but keeps `?q=` and the filtered list.
12. **Free delivery at exactly 5,000 AMD (TC-04):** the cart shows Free, but `POST /api/order/delivery-price` charges 500.
13. **Stack traces in API errors (TC-08):** missing parameters return 500 with the full Java stack trace instead of 400.
14. **Phone accepts letters (TC-10):** `abcdefgh` passes registration validation.

## Not verified on production

I didn't sign in (storefront or backoffice), register, place orders or change catalog data on production. Expected results for TC-04 steps 2–4, TC-09, TC-14 steps 3–6 and TC-15 at 375 px come from the UI and the source code and haven't been run yet.
