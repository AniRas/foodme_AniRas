# TC-05: Place a cash-on-delivery order end to end

**Priority:** P0 (core business flow) | **Type:** Acceptance / E2E | **User:** Registered user

## Objective
A signed-in user can order dishes from a chef with cash on delivery and then see the order in their order history with correct details.

## Preconditions
- QA test account exists and is signed out at the start
- Cart is empty
- ⚠️ Creates a real order on production. Coordinate with the chef/ops team, mark it as TEST in notes if supported, and cancel or clean it up afterwards. Prefer staging when possible

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | From `/explore`, open a chef and add 2 different dishes (one with qty 2) | "Your order" shows correct line totals. Subtotal, Delivery and Total match the pricing rules |
| 2 | Click **Go to checkout** | `/checkout` asks the user to sign in or create an account |
| 3 | Sign in with the QA account | User returns to checkout with the cart unchanged. Delivery details form (address/phone/notes) and order summary are shown |
| 4 | Try to place the order with a required field (e.g. address) empty | Order is blocked and the missing field is highlighted |
| 5 | Fill in valid delivery details | **Cash on delivery** is the payment method. No card fields are requested |
| 6 | Click **Place order** once | One order is created (double-clicking does not create a duplicate). A confirmation shows an order number/status, items, total and ETA (~25–40 min) |
| 7 | Check the cart | Cart is emptied and the header badge is cleared |
| 8 | Open `/orders` | The new order is at the top with the matching date, chef, items, quantities, total and status (e.g. "Placed"/"Pending") |
| 9 | Reload `/orders` and sign in again | The order is still there (persisted on the server) |

## Pass criteria
Exactly one order is created, every amount on the confirmation and in the history matches the cart, and the cart resets.
