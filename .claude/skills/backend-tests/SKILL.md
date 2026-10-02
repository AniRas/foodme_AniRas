---
name: backend-tests
description: Write or extend unit and integration tests for the FoodMe Spring Boot backend (apps/backend) following this repo's established test style. Use when asked to add, write, or fix backend/Java/JUnit/MockMvc tests, cover a controller, service, or bug fix with a test, or increase backend test coverage.
---

# Writing FoodMe backend tests

The existing tests in `apps/backend/src/test/java/am/foodme/backend/` are the standard. Read the closest one before writing a new test, and match it:

- `CustomerAuthControllerTest` — reference for POST bodies, error messages, auth and helper methods
- `ChefControllerTest`, `DishControllerTest` — reference for simple GET + `jsonPath` checks
- `OrderControllerTest` — reference for authenticated flows that chain several requests

**Exception:** tests marked `// FM-FLAKE-NN` are *deliberately flaky* course exercises (see "Anti-patterns" below). Never copy their patterns. Don't fix or delete them unless the user asks you to.

## Default: integration test through MockMvc

Most backend tests here are full-context HTTP tests. Use this shape unless the logic is pure enough for a unit test (see the next section).

```java
package am.foodme.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SomethingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void action_condition_expectedOutcome() throws Exception {
        mockMvc.perform(post("/api/...")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("field", "value"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.field").value("value"));
    }
}
```

Conventions:

- **Location and naming.** Put tests flat in package `am.foodme.backend` under `src/test/java/am/foodme/backend/`. Name the class `<Controller>Test`. Name methods `action_condition_expectedOutcome` (for example `login_wrongPassword_rejected`). Classes and methods are package-private, and every test method `throws Exception`.
- **Always `@ActiveProfiles("test")`.** This profile switches to H2 seeded from `data.sql`, turns off simulated latency, the flaky heartbeat job and HTTP logging, and uses the JWT secret `test-secret`.
- **Request bodies.** Build them with `objectMapper.writeValueAsString(Map.of(...))`. Use `List.of(Map.of(...))` for nested lists. Don't use raw JSON strings, and don't construct DTOs unless a test truly needs one.
- **Assertions.** Chain `.andExpect(status()...)` with `.andExpect(jsonPath(...))`. Check concrete values (`.value(...)`), not only `.exists()`. Use `.isArray()`, `.isNotEmpty()` and `.exists()` for shape checks.
- **Reading a response.** To pull a value out of a response, use `objectMapper.readTree(mvc...andReturn().getResponse().getContentAsString()).get("x").asText()`.
- **Error cases.** Assert both the status and the exact `$.message`. `GlobalExceptionHandler` maps `BadRequestException` → 400, `NotFoundException` → 404, and bean validation → 400 with the first field error's message. The error body contains `status`, `error`, `message` and `path`.
- **Private helpers.** Repeated payloads and setup go into private helpers at the bottom of the class (`registerPayload(...)`, `uniqueEmail()`, `customerToken()`, `cashOrderPayload()`). Don't create shared base classes or test utilities unless several classes would actually reuse them.

### Auth

- **Customer endpoints** (`/api/customer/**`, `POST /api/order`): register a fresh customer via `/api/auth/register` and read `token` from the response. Send it as `.header("Authorization", "Bearer " + token)`. Copy the `customerToken()` helper from `OrderControllerTest`.
- **Admin endpoints** (`/admin/**`): the seeded admin's password isn't documented, so don't log in through the API. Instead, `@Autowired JwtService jwtService` and call `jwtService.generateToken("admin", "ADMIN")`. The filter turns the role into `ROLE_ADMIN`.
- **Check the unauthenticated case.** For protected endpoints, assert `status().isUnauthorized()` without a token, as `meAndOrders_requireCustomerToken` does.

### Test data

- `src/test/resources/data.sql` is the fixed seed:
  - chefs 1 `marta-k` and 2 `ararat-grill` are ACTIVE; chef 3 is INACTIVE
  - dishes 1 and 2 (chef 1), 3 (chef 2) and 4 (chef 1) exist, and dish 4 is INACTIVE
  - dish tags 1 and 2 exist
  - admin 1 `admin` exists
- **Shared context.** The Spring context, and therefore the H2 database, is shared by **all test classes in one JVM run**. The data you write persists across tests.
- **Unique data per test.** Each test creates what it owns, with unique values such as `"ann-" + UUID.randomUUID() + "@example.com"`. Assert relative to that data (for example, "this new customer has 1 order"). Never assert global counts or ids that other tests can change.
- **Read-only seed checks.** It's fine to assert against seed rows for read-only endpoints, as `ChefControllerTest` does (active chef count = 2).
- **New seed rows.** If a test needs new fixed seed data, add it to `data.sql` with explicit ids. Check that existing count assertions still hold.

## Unit tests (services and pure logic)

There are no unit tests in the repo yet. When logic can be tested without HTTP or a database (for example `DatabaseUrlEnvironmentPostProcessor`, `JwtService`, or a service branch with mocked repositories), write a plain JUnit 5 test:

- No Spring context. Services use constructor injection, so build them directly: `new AdminAuthService(mock(AdminRepository.class), passwordEncoder, jwtService)`.
- Mockito and AssertJ ship with `spring-boot-starter-test`. Use `@ExtendWith(MockitoExtension.class)` with `@Mock`, or call `mock(...)` inline, and use `assertThat(...)` / `assertThatThrownBy(...)`.
- Use the same `action_condition_expectedOutcome` naming and the same flat package. Name the class `<ClassUnderTest>Test`.
- Prefer an integration test when the behavior that matters lives in security config, validation annotations, JPA queries or JSON shape. A mocked unit test won't exercise those.

## Bug-fix tests

When covering a ticketed bug (`FM-BUG-NN`):

1. Write a test that reproduces the bug and fails on the unfixed code.
2. Name the test after the correct behavior.
3. Put a `// FM-BUG-NN` comment above `@Test`, matching the existing `// FM-FLAKE-NN` comment style.

## Anti-patterns (what the FM-FLAKE tests demonstrate — don't do these)

- **Order dependence:** `@TestMethodOrder` / `@Order`, or relying on another test having run first (FM-FLAKE-02).
- **Global sequence values:** asserting values like order number `FM-100001`. They change with whatever else ran in the shared context (FM-FLAKE-02).
- **Unspecified ordering:** asserting `list[0]` when the endpoint doesn't define a sort order (FM-FLAKE-03). Assert membership instead, or assert the documented sort.
- **Wall clock and time zones:** comparing against `LocalDate.now()` without controlling the clock or time zone (FM-FLAKE-04). Assert a tolerance window, or parse the value and compare instants.
- **Fire and forget:** calling `mockMvc.perform(...)` without any `andExpect` for setup steps. Always assert that setup requests succeeded.
- **Sleeps and retries:** `Thread.sleep`, retries, or `@Disabled` to hide flakiness.

## Running and verifying

Run from `apps/backend`:
```bash
./gradlew test --tests am.foodme.backend.NewThingTest           # the new class
./gradlew test --tests 'am.foodme.backend.NewThingTest.method'  # one method
./gradlew test                                                  # full suite before finishing
```

Before finishing:

- Run the new tests both on their own and in the full suite, so you catch dependence on shared state.
- Expect the FM-FLAKE tests to fail now and then; that's by design.
- Report results honestly, including any failures that existed before your change.
- Never edit `src/main` just to make a test pass unless the user asked for a fix.
