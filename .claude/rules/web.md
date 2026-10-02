---
paths:
  - "apps/web/**"
---

# Storefront (`apps/web`)

React 19 + TypeScript + Vite, Tailwind v4, shadcn-style UI primitives (`src/components/ui`), TanStack Query, react-hook-form + zod (`src/schemas`), i18next (`src/locales`), Dexie (IndexedDB) cart.

## Commands

Run from `apps/web`:
```bash
npm run dev            # Vite dev server; talks to backend at http://localhost:8081
npm run lint           # oxlint
npm run build          # tsc -b && vite build (type errors fail the build)
npx playwright test                              # e2e; auto-starts Vite on :5180
npx playwright test e2e/cart-decrement.spec.ts   # single spec
npx playwright test -g "test title"              # single test by name
npm run test:e2e:all   # web e2e then admin e2e
```

The e2e tests hit a real backend at `VITE_API_BASE_URL` (default `http://localhost:8081`), so the backend and Postgres must be running. Set `PLAYWRIGHT_BASE_URL` to test a deployed site. Shared helpers such as account creation are in `e2e/auth.ts`.

## Structure

- Routes are defined in `src/router.tsx`, with one folder per page in `src/pages/<Name>/index.tsx`.
- Feature components live in `src/components/sections`, and layout pieces in `src/components/layout`.
- Imports use the `@/` alias for `src`.

## API calls (`src/api/client.ts`, `src/api/foodme.ts`)

- Base URL: `VITE_API_BASE_URL` if set; otherwise `http://localhost:8081` in dev; otherwise `""` (relative, same-origin) in production.
- The customer token comes from `lib/auth-storage.ts`. A 401 on `/api/customer/**` clears it.
- Non-2xx responses throw `ApiRequestError` with the backend's `message`.

## Cart (`src/hooks/useCart.ts`, `src/lib/db.ts`)

- The cart is client-side only, stored in IndexedDB (Dexie). All mutations go through `useCart.ts`.
- A cart can hold dishes from only one chef: `addDishToCart` returns `"mismatch"` unless `replaceOtherChef` is passed.
- A line item's `uid` is `chefId-dishId-sortedAdditionIds`.
- `minimumOrderCount` is enforced through `limitations.minQuantity`.

## Intentional demo behaviors (not bugs)

- `lib/flakyHeartbeat.ts` reports a simulated error to Sentry about once every ten minutes. It is a no-op without a DSN.
- The `e2e/flake-*.spec.ts` files are deliberate flaky-test exercises.
