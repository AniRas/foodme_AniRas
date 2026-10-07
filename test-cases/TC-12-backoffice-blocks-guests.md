# TC-12: Backoffice and admin API are closed to guests

**Priority:** P0 | **Type:** Acceptance / Security | **User:** Guest

## Objective
Someone who isn't signed in as an admin can't open any backoffice page, can't call the admin API and can't find admin credentials on the site.

## Preconditions
- Signed out of `/backoffice`
- Never sign in during this case. Only check what a guest can see

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Open `/backoffice` | Redirected to `/backoffice#/login` with Username, Password and **Sign in** |
| 2 | Open `/backoffice/#/orders`, `#/chefs` and `#/dishes` directly | Every one redirects to the login page. No admin data is shown, even briefly |
| 3 | Read the login page | **No usernames or passwords are shown** on the page |
| 4 | `GET /admin/order` and `GET /admin/chef` without a token | **401** |
| 5 | `PATCH /admin/order/1/status` and `PUT /admin/chef/1` without a token | **401**. Nothing changes |
| 6 | `GET /admin/dish` without a token | Decide and record: either 401, or only data that is already public on the storefront (ACTIVE dishes) |

## Pass criteria
No admin page, admin data or admin action is available without signing in, and no credentials are published.

> ❌ **Fails on prod 2026-10-07:** step 3, the login page shows "Training sandbox — use admin / admin123" to everyone on the production URL. Steps 1, 2 and 4 passed. Step 6: `GET /admin/dish` returns 200 to guests (by design: "menu previews are shown on the public storefront"), and only ACTIVE dishes were seen.
