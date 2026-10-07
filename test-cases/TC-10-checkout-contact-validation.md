# TC-10: Checkout contact details are pre-filled and validated

**Priority:** P1 | **Type:** Acceptance / Validation | **User:** Registered user

## Objective
The receiver's contact details are pre-filled from the signed-in profile and can be edited. Invalid values are blocked before the order is created.

## Preconditions
- Signed in with a QA account that has a full name, phone and email. Cart has items
- Delivery method: Delivery, with a valid City and Street

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Open `/checkout` | The top bar shows "Signed in as <full name>" and <email>, plus **Sign out**. Name, Phone and Email are pre-filled from the profile |
| 2 | Clear the name, or enter 1 character, then submit | **"Enter your name"**. No `POST /api/order` request is sent |
| 3 | Enter a phone shorter than 8 digits, then submit | **"Enter a valid phone number"** |
| 4 | Enter `ara@` as the email, then submit | **"Enter a valid email"** |
| 5 | Type a note longer than 300 characters | Input stops at 300 or an error is shown. The order is not sent with >300 characters |
| 6 | Change the receiver name to someone else (ordering for a friend) and submit valid data | The order is created with the edited receiver details. The user's own profile is **not** changed |
| 7 | Click **Sign out** on the checkout page | The page switches back to the Sign in / Create account panel. The cart is kept |
| 8 | Enter HTML/script in Note/Street (e.g. `<img src=x onerror=alert(1)>`) and place the order | The text is stored and shown as plain text in the confirmation, tracking and backoffice. No script runs |

## Pass criteria
Invalid contact data never reaches the API, pre-filled data comes from the profile, and user input is never executed as HTML.
