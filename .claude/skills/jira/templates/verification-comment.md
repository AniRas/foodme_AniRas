# Fix verification comment template

Modeled on the KAN-4 comment. Post it with `addOrEditJiraIssueComment` before moving a bug to Done. Only list checks you actually ran.

---

## Fix verified on <production / local / staging>

**Fix commit:** `<short sha>` — <commit subject, incl. FM-BUG-NN>
**Change:** `<file path>` — <one-line description of what changed>
**Environment:** <URL> — <browser/device, signed in or not>

### Checks

| Step | Result |
| --- | --- |
| <action> | <observed result> |
| <action> | ✅ <expected result confirmed> |

### Not covered in this check

* <Scenario not verified>
* <Definition-of-done item still open, if any>
