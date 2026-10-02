# Bug report template

Modeled on KAN-4. Copy everything below the line into the description. Replace the `<…>` parts.

**Summary field:** `<Area>: <what goes wrong, in user terms>`

---

## Summary

<1–3 sentences: what happens, what should happen instead, and why it matters to customers or the business.>

Priority: <level> — <impact, who is affected, workaround>.

## Where

<App (customer storefront / admin back office / API) → page → component. List every other place the same UI appears.>

## Steps to reproduce

1. <Start point, e.g. "Open the storefront home page.">
2. <One user action per step. Bold the exact button labels, e.g. **Add to cart**.>
3. <Say what the screen shows at key points, e.g. "Quantity shows 2.">

## Expected result

* <Concrete, checkable outcome.>
* <Related behavior that should stay the same.>

## Actual result

* <What actually happens.>
* <Side effects, e.g. wrong totals.>

## More observations

* <Other inputs or states that show the same problem, or don't.>
* <Related controls that work correctly.>
* <Persistence, refresh, sign-in or role effects.>

## Environment

* App URL: https://foodme-aniras.onrender.com/
* <Browsers/devices; signed in or not; role.>

## Definition of done

* Steps above give the expected result.
* <Behavior that must stay the same.>
* An automated <end-to-end / backend> test covers: <scenario>.
* All automated checks pass.
