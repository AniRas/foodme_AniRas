# TC-04: Create an account and sign in

**Priority:** P1 | **Type:** Acceptance / Functional | **User:** New visitor

## Objective
A new user can register with valid details, gets clear validation errors otherwise, and can sign in with the new account.

## Preconditions
- Signed out
- Unique test email (e.g. `qa+<timestamp>@yourdomain.test`), test phone `+374 XX XXX XXX`
- ⚠️ Creates a real record. Use a QA-tagged email and clean it up afterwards, or run on staging

## Steps & Expected Results
| # | Step | Expected result |
|---|------|-----------------|
| 1 | Open `/register?next=%2Fcheckout` (or click **Create account**) | Form shows Full name, Email, Phone (+374 placeholder) and Password (min 8) fields, plus a **Create account** button |
| 2 | Submit an empty form | Every required field shows a validation message. No request creates a user |
| 3 | Enter an invalid email, a malformed phone and a 7-character password | Field-level errors for email format, phone format and "At least 8 characters" |
| 4 | Register with an email that already exists | A clear "account already exists" error. No duplicate account is created |
| 5 | Enter valid unique data and submit | Account is created, the user is signed in automatically and redirected to `/checkout` (the `next` target). Header shows the signed-in state |
| 6 | Sign out, then sign in at `/login` with the new credentials | Sign-in succeeds and the session persists across a page reload |
| 7 | Check that the password is not exposed | The password field is masked. The password does not appear in URL, console or network response bodies |

## Pass criteria
Registration and sign-in work for valid input, invalid input is rejected with helpful messages, and no credential leaks.
