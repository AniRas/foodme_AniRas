# Goal: Jira skill self-improvement (10 runs)

Paste everything in the block below after `/goal`. It runs in **dry-run mode**: Jira is only read (duplicate search), and no tickets are created, edited, commented on or moved. Without that, 10 runs would file the same bugs 10 times.

```text
Run the `jira` skill 10 times in a row on the same input, in DRY-RUN mode, then improve the skill based on how consistent the 10 outputs were.

Input (the same for every run): file bug reports for these three findings from regression-reports/2026-10-07-1155.md and the regression skill's known issues:
  A. TC-07 step 6 / K-6: "+" quick-add adds every add-on the dish offers (Yassai curry 3,600 AMD instead of 2,900).
  B. TC-09 step 1: /orders/success with no order number still shows "Order placed!".
  C. TC-10 step 6: switching Sign in → Create account clears Full name but keeps Email and Phone.

DRY-RUN rules (apply to every run):
- Allowed Jira calls: read-only only (getAccessibleAtlassianResources, searchJiraIssuesUsingJql, getJiraIssue). Never call createJiraIssue, editJiraIssue, addOrEditJiraIssueComment, transitionJiraIssue, manageJiraSprint or any executeWrite/executeDestructive operation.
- Where the skill says "confirm before creating" or "create it", write the draft to a file instead.

For each run i = 1..10:
1. Follow the jira skill exactly as written for "Creating a ticket" and "Rule: set bug priority from your own review", for A, B and C. Don't change SKILL.md or the templates during the 10 runs.
2. Save the output to jira-drafts/goal-run-<i>.md with, for each finding:
   - the duplicate-check JQL you ran and the keys it returned
   - the decision: "new ticket" or "duplicate of KAN-NN"
   - Summary field, issue type, priority, and the full description built from templates/bug.md
3. Start each run fresh: don't reread earlier drafts before writing the next one.

After run 10:
4. Build a comparison in jira-drafts/goal-summary.md. For each finding (A, B, C), one row per run with: duplicate decision, issue type, priority, Summary field, and which template sections were filled or "None". Mark each column "Consistent?".
5. For every field that differs between runs, decide whether the skill is ambiguous (two readings were both reasonable) or the agent didn't follow it. Examples to look for:
   - priority level differs → is the priority table or the "raise/lower one level" rule unclear for this kind of bug?
   - Summary wording differs → does the `<Area>: <what goes wrong>` format need fixed area names (Basket, Checkout, Sign in, Order status…)?
   - JQL differs → should the skill prescribe how to pick duplicate-search keywords?
   - sections skipped or code paths placed in the description → is the template guidance unclear?
6. For each ambiguity, edit .agents/skills/jira/SKILL.md (and templates/bug.md if needed):
   - make the rule specific enough that all 10 runs would have produced the same answer
   - add worked examples where it helps (e.g. "price wrong on some dishes → High")
   - add a dated "Learnings" section at the end of SKILL.md if it doesn't exist, with one line per change and the run evidence
   - don't remove existing rules unless a run proved them wrong (then correct them and say why)
7. In goal-summary.md, list every change with its evidence and give a consistency score per finding: the number of fields (duplicate decision, type, priority, summary area, sections) that were identical in all 10 runs, out of 5.

Done when: 10 draft files exist, goal-summary.md has the comparison, the scores and the change log, and the skill files are updated (or goal-summary.md explains why no change was needed). Then commit jira-drafts/ and the skill changes with a message referencing "jira skill self-improvement (10 runs)". Don't create the real tickets. Ask me afterwards whether to file A, B and C for real.
```

## What to expect

- The duplicate search should agree in every run. If it doesn't, the JQL keyword guidance is the gap.
- Priority is the most likely field to vary. B (fake success page) and C (form tab state) sit between Low and Medium on the current table. A (wrong price) should be High every time.
- Evidence for the homework: `jira-drafts/goal-summary.md` and the `SKILL.md` diff from the goal's commit.
