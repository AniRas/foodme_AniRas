---
paths:
  - "apps/backend/**"
---

# Backend (`apps/backend`)

Spring Boot 3.3, Java 17, Gradle, PostgreSQL + Flyway, JWT auth. Package `am.foodme.backend`.

## Commands

Run from `apps/backend`:
```bash
./gradlew build                                   # compile + tests (what CI runs)
./gradlew test                                    # tests only (H2 in-memory, profile "test")
./gradlew test --tests am.foodme.backend.OrderControllerTest            # single class
./gradlew test --tests 'am.foodme.backend.OrderControllerTest.someMethod' # single method
./gradlew bootRun                                 # needs Postgres on localhost:5432, db/user/pass "foodme"
```

## Structure

- Layering is controller → service → repository (Spring Data JPA), with DTOs in `dto/` and entities in `model/`.
- Controllers are split into `controller/api` (storefront) and `controller/admin` (back office). Admin logic lives in the `Admin*Service` classes.
- Errors go through `exceptionHandler/GlobalExceptionHandler`. Throw `BadRequestException` for 400s.

## API surface and security (`security/SecurityConfig`)

- `/api/**` — public storefront API. Exceptions: `/api/customer/**` and `POST /api/order` require the `CUSTOMER` role. `/api/auth/**` handles customer register/login.
- `/admin/**` — admin API; needs an admin JWT except `/admin/auth/login`. `GET /admin/dish/**` is deliberately public.
- Auth is stateless JWT (`JwtService`, `JwtAuthenticationFilter`). Unauthenticated requests get a 401.
- CORS origins come from `FOODME_CORS_ALLOWED_ORIGINS` (default `*`).

## Serving the SPAs (`config/SpaWebConfig`)

- The Docker image bundles the storefront into `static/` (served at `/`) and the admin build into `static/backoffice/` (served at `/backoffice`).
- Paths with a file extension are served as assets. Other paths fall back to the matching SPA's `index.html`.
- `api/`, `admin/`, `actuator/`, `swagger-ui` and `v3/` never get an SPA shell. If you add a new top-level backend path, add it to that exclusion list.

## Database

- Flyway migrations live in `src/main/resources/db/migration`, and all tables are in the `foodme` schema.
- Hibernate runs with `ddl-auto=validate`, so every entity change needs a new `V<n>__*.sql` migration. Don't edit existing migrations.
- Images are stored in Postgres (`foodme.image`) and served at `/api/images/**`. `ImageSeedRunner` loads `resources/img-seed/` on first start.
- `DatabaseUrlEnvironmentPostProcessor` (registered in `META-INF/spring.factories`) converts `DATABASE_URL=postgresql://…` (Render/Neon style) into JDBC properties. If no URL is given, it uses the `DB_*` vars.

## Tests

- Tests are `@SpringBootTest` + MockMvc classes with `@ActiveProfiles("test")`.
- They skip Flyway and use H2 in PostgreSQL mode with `create-drop`, seeded from `src/test/resources/data.sql`. Keep that file in sync with any schema or seed changes.
- When writing tests, use the `backend-tests` skill (`.claude/skills/backend-tests/SKILL.md`).

## Intentional demo behaviors (not bugs)

- `config/SimulatedLatencyConfig` adds a random 200–1500 ms delay to `/api/**` and `/admin/**` (not `/api/images/**`). The test profile sets it to 0.
- `observability/FlakyHeartbeatJob` reports a simulated failure to Sentry about one run in ten. It is disabled under the `test` profile.
- `observability/HttpLoggingFilter` logs request/response bodies with secrets redacted. It is toggled by `HTTP_LOGGING_ENABLED`.

## Observability

- Actuator exposes `health`, `info` and `prometheus`.
- `logback-spring.xml` emits JSON logs and ships them to Loki only when `LOKI_PUSH_URL` is set.
- Errors go to GlitchTip via `SENTRY_DSN`.
