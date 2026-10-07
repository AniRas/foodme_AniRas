#!/usr/bin/env python3
"""PreToolUse hook: block new hard waits in Playwright e2e tests.

Runs before Edit, Write and MultiEdit. If the target file is a test under
apps/<app>/e2e/ and the change adds a hard wait (page.waitForTimeout,
setTimeout, sleep), the tool call is blocked with exit code 2.

Only *added* waits count: the number of waits after the change is compared
with the number before it, so existing deliberate waits (for example the one
in flake-dish-modal.spec.ts, see AGENTS.md) don't trigger the hook.
"""
import json
import os
import re
import sys

E2E_FILE = re.compile(r"(^|/)apps/[^/]+/e2e/.+\.(ts|tsx|js|jsx|mjs|cjs)$")
HARD_WAIT = re.compile(r"\bwaitForTimeout\s*\(|\bsetTimeout\s*\(|\bsleep\s*\(")


def count(text):
    return len(HARD_WAIT.findall(text or ""))


def main():
    payload = json.load(sys.stdin)
    tool = payload.get("tool_name", "")
    args = payload.get("tool_input", {}) or {}
    path = args.get("file_path", "")
    if not E2E_FILE.search(path.replace(os.sep, "/")):
        return 0

    try:
        with open(path, encoding="utf-8") as f:
            current = f.read()
    except OSError:
        current = ""

    if tool == "Write":
        before, after = count(current), count(args.get("content"))
    elif tool == "Edit":
        before, after = count(args.get("old_string")), count(args.get("new_string"))
    elif tool == "MultiEdit":
        edits = args.get("edits") or []
        before = sum(count(e.get("old_string")) for e in edits)
        after = sum(count(e.get("new_string")) for e in edits)
    else:
        return 0

    if after > before:
        rel = path.split("apps/", 1)[-1]
        print(
            f"Blocked: this change adds {after - before} hard wait(s) to apps/{rel}.\n"
            "Hard waits (page.waitForTimeout, setTimeout, sleep) make e2e tests slow and flaky.\n"
            "Wait for a condition instead, e.g. `await expect(locator).toBeVisible()`, "
            "`await page.waitForURL(...)`, `await page.waitForResponse(...)` or `expect.poll(...)`.",
            file=sys.stderr,
        )
        return 2
    return 0


if __name__ == "__main__":
    sys.exit(main())
