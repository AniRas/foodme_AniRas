# TC-10: Registration form rejects invalid details before submitting

**Priority:** P1 | **Type:** Acceptance / Validation | **User:** Guest

## Objective
The **Create account** form catches empty and badly formatted details on the page, with a message under each field, so bad data never reaches the server. This case never submits a valid form, so no account is created.

## Preconditions
- Signed out. Open `/register`
- Leave **Password empty** in every step, so the form can never be submitted successfully

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Check the form | Fields Full name ("Ara Petrosyan" placeholder), Email, Phone ("+374 XX XXX XXX"), Password, and a **Create account** button |
| 2 | Click **Create account** with every field empty | Messages: "Enter your name", "Enter a valid email", "Enter a valid phone number", "Password must be at least 8 characters". URL stays `/register` |
| 3 | Full name `A`, Email `qa-test@`, Phone `123`, then submit | The same 4 messages are shown |
| 4 | Phone `abcdefgh` (letters only), then submit | **"Enter a valid phone number"** is shown. Letters are not a phone number |
| 5 | Phone `+374 99 123 456`, Email `qa-test@example.com`, Full name `QA Test`, then submit | Name, email and phone messages disappear. Only the password message is left |
| 6 | Switch to the **Sign in** tab and back | No crash. Field values are either kept or cleared consistently |

## Pass criteria
Each invalid field shows its own message, and a phone number has to look like a phone number.

> ❌ **Fails on prod 2026-10-07:** step 4 accepts `abcdefgh` as a phone number. `registerSchema` only checks `min(8)` characters. Steps 1–3 passed.
