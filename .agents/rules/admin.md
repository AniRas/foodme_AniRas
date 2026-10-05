---
paths:
  - "apps/admin/**"
---

# Back office (`apps/admin`)

React 18 with plain JS/JSX (no TypeScript), react-admin 5 + MUI, axios.

## Commands

Run from `apps/admin`:
```bash
npm run dev            # Vite dev server at "/"; talks to backend at http://localhost:8081
npm run lint           # eslint
npm run build          # vite build
npx playwright test    # e2e; auto-starts Vite on :5174, runs serially
```

The e2e tests (`e2e/admin-flows.spec.ts`) need the backend running at `VITE_API_BASE_URL` (default `http://localhost:8081`). Set `ADMIN_BASE_URL` to point the tests at another frontend.

## Structure

- react-admin wiring lives in `src/App.jsx` and `src/providers/` (`dataProvider.js`, `authProvider.js`).
- One API module per resource is in `src/api/*-api.js`. Each resource has its pages under `src/pages/<resource>/`.
- Route guards are in `src/security/`.

## Base path and API

- In production the backend serves this app under `/backoffice/`, because `/admin/**` is the backend's admin REST API. `vite.config.js` sets `base: '/backoffice/'` only in production mode.
- Derive in-app URLs from `import.meta.env.BASE_URL`; don't hardcode `/`.
- `src/api/base-api.js` prefixes every call with `/admin`. The base URL follows the same rule as the storefront: `VITE_API_BASE_URL`, else `localhost:8081` in dev, else same-origin.
- The JWT is stored in `localStorage.token`. A 401 clears it and redirects to the login page.

## Intentional demo behaviors (not bugs)

- `src/lib/flakyHeartbeat.js` reports simulated errors to Sentry, the same as in the storefront.
