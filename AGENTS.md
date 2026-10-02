# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

FoodMe is a food-ordering demo app used for a QA / agentic-QA course. It is a monorepo with three apps plus a monitoring stack:

- `apps/backend` — Spring Boot REST API, which also serves both SPAs in production. Details: `.claude/rules/backend.md`
- `apps/web` — customer storefront (React 19 + TypeScript). Details: `.claude/rules/web.md`
- `apps/admin` — back office (react-admin, plain JS). Details: `.claude/rules/admin.md`
- `infra/monitoring` — Prometheus + Loki + Grafana + Grafana MCP bundled into one Render service (see its README)

The rule files load automatically when you work on files in the matching app. Each app has its own `package.json` / Gradle wrapper. There is no root workspace, so run commands from inside the app directory.

## How the pieces fit

- **Single-origin deploy.** `apps/backend/Dockerfile` uses the repo root as its build context. It builds both SPAs and bundles them into the backend jar. One service then serves:
  - the storefront at `/`
  - the admin app at `/backoffice`
  - the storefront API at `/api/**`
  - the admin API at `/admin/**`
- **API base URL.** Both frontends resolve the base URL the same way: `VITE_API_BASE_URL` if set; otherwise `http://localhost:8081` in dev; otherwise `""` (same-origin) in production.
- **Local dev.** Run Postgres (db/user/pass `foodme`), then `./gradlew bootRun` in `apps/backend` (port 8081), then `npm run dev` in `apps/web` and/or `apps/admin`.
- **Observability.** Errors go to GlitchTip through the Sentry SDKs. The backend uses `SENTRY_DSN`. The frontends use `VITE_SENTRY_DSN`, baked in at build time. Metrics flow from Actuator to Prometheus, and logs from the backend to Loki when `LOKI_PUSH_URL` is set. Grafana reads both.
- **Intentional flakiness.** The app includes simulated latency, background jobs that report fake errors, and `flake-*` e2e specs for the QA course. These are deliberate and should not be "fixed".

## Deployment and CI

- `render.yaml` deploys the app as one Docker web service plus a free Postgres.
- `render-monitoring.yaml` deploys the monitoring stack.
- The Dockerfile's JVM flags are tuned for Render's 512 MB / 0.1 CPU free tier.
- The root `README.md` is a step-by-step deployment guide for students.
- `.github/workflows/ci.yml` runs the backend build, lint + build for web and admin, the Playwright e2e suite, and Docker image builds. The e2e job boots `infra/docker-compose.yml --profile core`, but that file is not in this repo, so the job can't succeed as written. `.env.example` also mentions `docs/deployment.md` and Jenkins/GlitchTip compose profiles, which are missing too.
- The `Claude PR Review` workflow auto-reviews PRs.

## Conventions

- Bug-fix commits reference ticket IDs such as `FM-BUG-07`.
