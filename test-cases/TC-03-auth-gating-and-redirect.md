# TC-03: Checkout and Orders require sign-in and redirect back afterwards

**Priority:** P1 | **Type:** Acceptance / Security & Navigation | **User:** Guest, then registered user

## Objective
Guests cannot reach protected pages. After signing in, they return to the page they came from with their cart intact.

## Preconditions
- Signed out
- Cart contains at least 1 item
- A valid QA test account exists

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Open `/orders` directly | Redirected to `/login?next=/orders`. No order data is shown |
| 2 | From a chef page, click **Go to checkout** | `/checkout` shows the "Account" panel with **Sign in / Create account** tabs. No address or "Place order" form is shown to the guest |
| 3 | Sign in with **invalid** credentials | An inline error is shown, the user stays on the page and no session is created |
| 4 | Submit with an empty email or a password under 8 characters | Client-side validation blocks submission and shows a field-level message |
| 5 | Sign in with the valid QA account | User returns to **checkout** (the `next` target). Cart items and totals are unchanged. The header shows the signed-in state instead of "Sign in" |
| 6 | Open `/orders` | The orders page loads (history or empty state) with no redirect |
| 7 | Sign out, then try `/checkout` and `/orders` again | The auth gate is back. Previous user data is not visible |
| 8 | Manually open `/login?next=https://evil.example.com` and sign in | User is redirected inside FoodMe only (no open redirect to an external domain) |

## Pass criteria
Protected routes are never readable by guests, `next` redirects work for internal paths only, and the cart is preserved through sign-in.
