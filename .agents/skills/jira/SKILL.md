---
name: jira
description: Work with the FoodMe Jira project (KAN) — create bug reports and tasks from the repo templates, triage and set bug priority, add tickets to the current sprint, move tickets through the workflow, and post fix-verification comments. Use whenever the user mentions Jira, a ticket/issue/bug report/task, a KAN-NN key, or asks to file, update, triage, or close a ticket.
---

# Working with FoodMe Jira

## Site facts

| | |
|---|---|
| Site | `https://anirayisyan.atlassian.net` |
| cloudId | `3ff68581-e9ae-4e29-9dad-128b01304db1` (pass it top-level on every call) |
| Project | `KAN` — "FoodMe" (team-managed, board id `2`, "KAN board") |
| Issue types | Epic, Task, Subtask, Bug (`10008`) |
| Workflow | To Do → In Progress → Review → Testing → Done |
| Priorities | Highest, High, Medium, Low, Lowest |
| App URL | https://foodme-aniras.onrender.com/ (storefront `/`, admin `/backoffice`) |

`SAM1` is Atlassian's sample fitness project. Never file FoodMe work there.

The Atlassian MCP tools are deferred, so load them with ToolSearch first. Use these:
- `createJiraIssue`
- `editJiraIssue`
- `getJiraIssue`
- `searchJiraIssuesUsingJql` (JQL must be bounded, e.g. `project = KAN ...`)
- `addOrEditJiraIssueComment`

For transitions and sprints, call `discover` and then the matching execute tool: `listJiraIssueTransitions`, `transitionJiraIssue`, `listJiraBoardSprints`, `manageJiraSprint`.

## Templates

- Bug: [templates/bug.md](templates/bug.md). This is the house standard, taken from KAN-4.
- Task: [templates/task.md](templates/task.md)
- Fix verification comment: [templates/verification-comment.md](templates/verification-comment.md). Taken from the comment on KAN-4.

Read the matching template before writing a ticket. Fill in every section. If a section doesn't apply, write "None" rather than deleting it.

Write descriptions in markdown, for a reader who isn't technical:
- Use UI labels exactly as they appear on screen (**Add to cart**, "−").
- Write user-visible steps, not code paths. Code references belong in the verification comment.

Summary format: `<Area>: <what goes wrong, in user terms>`. For example: `Basket: pressing − at quantity 2 removes the dish instead of reducing it to 1`. Task summaries start with a verb.

## Creating a ticket

1. **Check for duplicates.** Search `project = KAN AND summary ~ "<keywords>" AND statusCategory != Done`. If a match exists, show it to the user instead of filing a second ticket.
2. **Draft from the template.** Get the facts from the user and the repo. Never invent reproduction results. Mark anything you haven't observed yourself as "Not verified".
3. **Confirm before creating.** Show the user the summary, type and priority (for bugs, with your reasoning) before calling `createJiraIssue`. Creating a ticket is visible to the whole team.
4. **Create it** in `KAN` with the right issue type and `assignToSprint: "active"` (see the sprint rule below).
5. **Report back.** Give the user the key and the link (`https://anirayisyan.atlassian.net/browse/KAN-NN`), plus the sprint result.

## Rule: always add to the current sprint

Every ticket you create or pick up goes into the board's active sprint:

- **New tickets:** pass `assignToSprint: "active"` to `createJiraIssue`.
- **Existing tickets:** find the active sprint with `listJiraBoardSprints` (`boardId: 2`, `state: "active"`), then add the ticket with `manageJiraSprint` (`action: "addIssues"`).
- **If assignment fails**, the issue is still created, and the response has `sprintAssignment.assigned: false`. Don't create the ticket again. Tell the user it landed on the board without a sprint, and why.
- **Current state:** the KAN board is a simple board with sprints turned off ("The board does not support sprints"). Until the user turns sprints on (Project settings → Features → Sprints), assignment will fail. Report that each time.
- **No active sprint:** if sprints are on but none is active, tell the user. Never create, start or close a sprint on your own.

## Rule: set bug priority from your own review

For every bug you file or triage, decide the priority from the evidence, not from what the reporter guessed.

1. **Review the bug.** Read the relevant code (`apps/web`, `apps/admin`, `apps/backend`). Reproduce it, or check it through tests or the live app, when you can.
2. **Score it** with the table below. Pick the highest row that matches.
3. **Set the priority.** Pass `priority` on create, or use `editJiraIssue` for an existing ticket.
4. **Add a priority note.** Add one line under **Summary** in the description, or a comment on an existing ticket: `Priority: <level> — <impact, who is affected, workaround>.`

| Priority | When |
|---|---|
| Highest | App or a core flow is down: checkout or ordering fails, data loss or corruption, security/auth bypass, secrets exposed, every user affected with no workaround |
| High | A core flow (browse → cart → checkout → order tracking, admin order handling) gives wrong results or blocks many users; money/price/quantity is wrong; workaround is hard |
| Medium | A feature misbehaves but users can still finish, or there is an easy workaround; limited to some dishes, chefs or roles (KAN-4 was rated Medium) |
| Low | Cosmetic, copy or layout issues; rare edge cases; admin-only inconvenience |
| Lowest | Polish or nice-to-have; no user impact |

Adjust for these:
- Raise one level if it affects revenue or conversion, or hits every user.
- Lower one level if it only reproduces in deliberate demo behavior. See below.

**Not bugs.** Before filing, check that the behavior isn't one of the course's deliberate demo behaviors:
- simulated API latency
- flaky heartbeat errors in GlitchTip
- `FM-FLAKE` tests

If it is one of these, tell the user instead of filing.

**Changing an existing priority.** If your review disagrees with the current priority, change it. Then tell the user the old value, the new value, and why.

## Moving tickets and closing out

- **Moving status:** call `listJiraIssueTransitions`, then pick the transition whose *target status* matches. Don't match on the transition's name.
- **Fix commits:** reference the ticket. Repo convention is `FM-BUG-NN` in the commit message, and KAN-4 ↔ `FM-BUG-07`. Mention the KAN key in the verification comment so the two can be traced to each other.
- **Moving a bug to Done:** post a verification comment from the template first. List what was checked *and* what wasn't. Never claim checks you didn't run.
