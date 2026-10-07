# FoodMe – Critical Acceptance Test Cases

**Environment:** Production – https://foodme-aniras.onrender.com/
**Explored on:** 2026-10-03 (TC-01–05), 2026-10-06 (TC-06–15). Chrome desktop, guest session

| ID | Title | Priority | Flow |
|----|-------|----------|------|
| [TC-01](TC-01-browse-chefs-and-menu.md) | Browse chefs and open a chef's menu | P1 | Discovery |
| [TC-02](TC-02-cart-management-and-pricing.md) | Cart management and price calculation | P1 | Cart |
| [TC-03](TC-03-auth-gating-and-redirect.md) | Checkout/Orders require sign-in and redirect back | P1 | Auth gating |
| [TC-04](TC-04-register-and-sign-in.md) | Create account and sign in | P1 | Authentication |
| [TC-05](TC-05-place-cod-order-e2e.md) | Place a cash-on-delivery order end to end | P0 | Ordering |
| [TC-06](TC-06-single-kitchen-cart-switch.md) | Cart holds one kitchen; switching asks for confirmation | P1 | Cart |
| [TC-07](TC-07-dish-additions-and-quantity.md) | Add-ons and quantity in the dish modal | P1 | Cart / Menu |
| [TC-08](TC-08-payment-method-selection.md) | Selected payment method is the one recorded | P0 | Checkout |
| [TC-09](TC-09-takeaway-vs-delivery.md) | Takeaway vs Delivery: address and fee | P1 | Checkout |
| [TC-10](TC-10-checkout-contact-validation.md) | Checkout contact pre-fill and validation | P1 | Checkout |
| [TC-11](TC-11-order-confirmation-and-tracking.md) | Order confirmation and tracking page | P0 | Post-order |
| [TC-12](TC-12-admin-order-status-lifecycle.md) | Backoffice status changes shown to customer | P0 | Admin + Tracking |
| [TC-13](TC-13-catalog-integrity-every-listed-chef-opens.md) | Every chef listed on Explore opens and has a menu | P0 | Discovery / Data |
| [TC-14](TC-14-admin-deactivate-dish-and-chef.md) | Deactivating a dish/chef hides it on the storefront | P1 | Admin + Catalog |
| [TC-15](TC-15-order-data-access-control.md) | Customers see only their own orders; private APIs need sign-in | P0 | Security |

## Flows mapped

- **Home (`/`)**: hero, "Explore chefs" / "Order now" CTAs, "How it works", featured chefs
- **Explore (`/explore`, `/explore?q=`)**: chef grid, header search
- **Chef page (`/chef/:id`)**: info (ETA, delivery fee, free-delivery threshold, phone), menu by category, "Your order" side cart
- **Checkout (`/checkout`)**: guests see a Sign in / Create account panel
- **Auth (`/login?next=`, `/register?next=`)**: email + password; register adds full name + phone (+374)
- **Orders (`/orders`)**: redirects guests to `/login?next=/orders`
- **Payment**: UI offers Cash on delivery, Bank card and Idram, but the client always sends `CASH` (see TC-08)
- **Dish modal**: add-ons (checkboxes with +price), quantity stepper, "Switch kitchens?" dialog when the cart holds another chef
- **Checkout (signed in)**: Delivery/Takeaway, contact (name/phone/email), address (city/street required for delivery), note, payment
- **Post-order**: `/orders/success?number=`, `/orders/failed`, `/tracking/:number` (Received → Preparing → Delivered / Declined)
- **Backoffice (`/backoffice`)**: Orders (status transitions, reject needs a reason), Chefs and Dishes (ACTIVE/INACTIVE)

## Observations from exploration (worth raising)

1. **Chef count mismatch:** `/explore` says "6 chefs cooking near you" but shows only 5 cards. With `?q=Sakura` it still says "6 chefs" while showing 1 result. TC-01 currently fails on this.
2. **Chef data mismatch:** the chef called "Argentinean" (`/chef/39`) serves only sushi, poke, wok and onigiri. Its description also contains placeholder/test text instead of a real description.
3. **Header "Sign in" link:** on `/login?next=/checkout` and `/register`, the header link points to `next=/orders` and not to the current `next` target.
4. **Listed chef can't be opened (TC-13):** Italiano Margarino (`/chef/32`) appears on `/explore` but its page shows "Chef not found", and the API returns 0 active dishes.
5. **Payment choice is ignored (TC-08):** Bank card and Idram can be selected, but `Checkout/index.tsx` hard-codes `paymentType: "CASH"`.
6. **Fake success page (TC-11):** from the code, `/orders/success?number=ANYTHING` shows "Order placed!" for any value without checking that the order exists.
7. **Unstable add-on data (TC-07):** `GET /api/dish/24/active` returned add-ons for Miso Soup on one call and none on later calls.
8. **Public order lookup (TC-15):** `GET /api/order/number/{n}` doesn't require sign-in. Check which personal data it returns.
9. **Backoffice login page shows the sandbox admin credentials** in the UI. That's fine for training, but it must never happen in a real production environment.

## Not verified on production

I didn't sign in (storefront or backoffice), register, place orders or change catalog data on production, because that would create real data. TC-04, TC-05, TC-08–TC-12, TC-14 and TC-15 (steps 3+) come from the UI and the source code and still need to be run with a dedicated QA account. Clean up any orders afterwards, or run them on staging.
