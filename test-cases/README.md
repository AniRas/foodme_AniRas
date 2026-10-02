# FoodMe – Critical Acceptance Test Cases

**Environment:** Production – https://foodme-aniras.onrender.com/
**Explored on:** 2026-10-03 (Chrome, desktop 1568×767, guest session)

| ID | Title | Priority | Flow |
|----|-------|----------|------|
| [TC-01](TC-01-browse-chefs-and-menu.md) | Browse chefs and open a chef's menu | P1 | Discovery |
| [TC-02](TC-02-cart-management-and-pricing.md) | Cart management and price calculation | P1 | Cart |
| [TC-03](TC-03-auth-gating-and-redirect.md) | Checkout/Orders require sign-in and redirect back | P1 | Auth gating |
| [TC-04](TC-04-register-and-sign-in.md) | Create account and sign in | P1 | Authentication |
| [TC-05](TC-05-place-cod-order-e2e.md) | Place a cash-on-delivery order end to end | P0 | Ordering |

## Flows mapped

- **Home (`/`)**: hero, "Explore chefs" / "Order now" CTAs, "How it works", featured chefs
- **Explore (`/explore`, `/explore?q=`)**: chef grid, header search
- **Chef page (`/chef/:id`)**: info (ETA, delivery fee, free-delivery threshold, phone), menu by category, "Your order" side cart
- **Checkout (`/checkout`)**: guests see a Sign in / Create account panel
- **Auth (`/login?next=`, `/register?next=`)**: email + password; register adds full name + phone (+374)
- **Orders (`/orders`)**: redirects guests to `/login?next=/orders`
- **Payment**: cash on delivery only

## Observations from exploration (worth raising)

1. **Chef count mismatch:** `/explore` says "6 chefs cooking near you" but shows only 5 cards. With `?q=Sakura` it still says "6 chefs" while showing 1 result. TC-01 currently fails on this.
2. **Chef data mismatch:** the chef called "Argentinean" (`/chef/39`) serves only sushi, poke, wok and onigiri. Its description also contains placeholder/test text instead of a real description.
3. **Header "Sign in" link:** on `/login?next=/checkout` and `/register`, the header link points to `next=/orders` and not to the current `next` target.

## Not verified on production

I didn't sign in, register or place an order on production, because that would create real data. TC-04 and TC-05 come from the UI and still need to be run with a dedicated QA account. Clean up any orders afterwards, or run them on staging.
