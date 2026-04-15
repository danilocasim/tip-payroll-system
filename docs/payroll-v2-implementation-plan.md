# Payroll V2 Implementation Plan

> Companion document to `docs/payroll-v2-architecture.md`
> Focus: implementation sequencing, Spring Boot MVC module design, and Next.js UX delivery

## 1. Goal

Implement the V2 architecture using the **existing backend stack** from `backend/`—Java 17, Maven, Spring Boot, Spring Web, Spring Data JPA, and MySQL—while upgrading the frontend to **two separate Next.js App Router applications**:

- `admin-web` for managers
- `employee-web` for employees

The implementation must:

- enforce real authentication and authorization on the backend
- prevent employees from authenticating into the manager portal
- protect manager-only resources from non-manager access
- preserve maintainability through a **modular MVC backend**
- deliver a polished, accessible, responsive UI with a strong shared design system

This plan intentionally keeps the backend as a **modular monolith**, not microservices.

---

## 2. Success criteria

### Functional

- Manager and employee users have **different portal entry points**, route trees, and visual shells.
- `POST /api/v1/admin/auth/login` accepts only manager accounts.
- `POST /api/v1/employee/auth/login` accepts only employee accounts.
- All protected backend routes return:
  - `401` when unauthenticated
  - `403` when authenticated but unauthorized
- Employees can access only their own self-service resources.
- Managers can manage employees and payroll from the admin portal.
- Legacy `sessionStorage`-based role gating is removed from the critical path.

### Maintainability

- Backend code follows **feature-first modular MVC** instead of a single flat controller/service/repository tree.
- Every backend module has clear ownership boundaries.
- API request/response DTOs are explicit; entities are not returned directly from controllers.
- Database changes are migration-driven, not ad hoc SQL edits.
- Shared UI primitives and portal-specific feature components are separated in the Next.js codebase.

### UX / UI

- Admin portal feels professional, structured, and efficient for high-volume tasks.
- Employee portal feels clear, calm, and mobile-friendly.
- Both portals meet WCAG AA contrast and keyboard accessibility baselines.
- All major screens support loading, empty, error, and forbidden states.

### Quality / Operations

- Backend has service, controller/security, and integration coverage for auth and authorization.
- Frontend has component tests and Playwright coverage for critical login and protected-route flows.
- Sessions are server-trusted and can scale beyond one backend instance.
- Rollout can happen gradually without a destructive cutover.

---

## 3. Assumptions

- Keep the current backend runtime stack:
  - Java 17
  - Spring Boot 3.3.4
  - Spring Web
  - Spring Data JPA
  - Maven
  - MySQL
- Additive backend dependencies are allowed when they are standard Spring Boot best practice:
  - Spring Security
  - Spring Validation
  - Flyway
  - Spring Session + Redis
  - Redis client starter
- Frontend implementation will use **Next.js App Router + TypeScript**.
- The architecture document’s earlier “React apps” recommendation will be implemented as **two Next.js apps** because Next.js is the selected React framework.
- A shared monorepo for frontend apps and packages is acceptable.
- UI design can introduce Tailwind CSS, shadcn/ui, Lucide icons, and a lightweight motion library if needed.
- Old static frontend pages can remain temporarily during rollout but will not remain the long-term UI.
- Any unresolved product rules are captured in [11. Open questions](#11-open-questions), not guessed.

---

## 4. Current state (files/flows)

### Current backend inventory

Observed files:

- `backend/pom.xml`
- `backend/src/main/java/com/payroll/controller/EmployeeController.java`
- `backend/src/main/java/com/payroll/controller/PayrollController.java`
- `backend/src/main/java/com/payroll/controller/GlobalExceptionHandler.java`
- `backend/src/main/java/com/payroll/service/EmployeeService.java`
- `backend/src/main/java/com/payroll/service/PayrollService.java`
- `backend/src/main/java/com/payroll/repository/EmployeeRepository.java`
- `backend/src/main/java/com/payroll/repository/PayrollRecordRepository.java`
- `backend/src/main/java/com/payroll/model/Employee.java`
- `backend/src/main/java/com/payroll/model/PayrollRecord.java`
- `backend/src/main/resources/application.properties`
- `backend/src/main/resources/schema.sql`

What happens today:

- Controllers expose open endpoints under `/api`.
- Business rules live in services.
- Persistence is flat and minimal.
- `GlobalExceptionHandler` returns a single-field map rather than a standardized API envelope.
- `Employee` and `PayrollRecord` are the only domain models.

Current backend limitations:

- No Spring Security.
- No authentication tables.
- No role-based authorization.
- No explicit DTO layer.
- No migration framework.
- No session store.

### Current frontend inventory

Observed files:

- `frontend/landing.html`
- `frontend/dashboard.html`
- `frontend/employees.html`
- `frontend/js/common.js`
- `frontend/js/landing.js`
- `frontend/js/dashboard.js`
- `frontend/js/employees.js`
- `frontend/js/add-employee.js`
- `frontend/js/edit-employee.js`
- `frontend/js/payroll-report.js`

What happens today:

- User picks Manager or Employee from `landing.html`.
- Role is stored in `sessionStorage`.
- `common.js` provides `checkRole()` and hides some manager-only UI.
- Frontend calls `http://localhost:8080/api` directly.

Current frontend limitations:

- Role is browser-managed, not server-managed.
- Manager and employee experiences are mixed.
- A determined user can still call manager endpoints directly.
- No component system, typed client, routing framework, or formal design system.

### Current data model

- `employees`
- `payroll_records`

Current limitations:

- No user account concept
- No credentials
- No role model in database
- No employee self-service identity mapping
- No payroll run entity

### Current testing

- Service-focused JUnit/Mockito tests under `tests/src/test/java/com/payroll`
- No controller/security tests
- No frontend tests
- No end-to-end browser coverage

### Current operational constraints

- No observed CI workflow in repo.
- No JavaScript package manager or monorepo tooling exists yet.
- Current CORS is permissive in controllers.

---

## 5. Proposed approach (recommended)

### 5.1 Keep the backend stack, but refactor to modular MVC

The backend should remain a **Spring Boot MVC monolith**, but not a flat package structure. The recommended structure is **feature-first modular MVC**:

```text
backend/src/main/java/com/payroll/
  common/
    api/
    config/
    exception/
    security/
    validation/
  auth/
    controller/
    service/
    repository/
    model/
    dto/
    mapper/
  identity/
    controller/
    service/
    repository/
    model/
    dto/
    mapper/
  admin/
    controller/
    service/
    repository/
    dto/
    mapper/
    policy/
  employee/
    controller/
    service/
    repository/
    dto/
    mapper/
    policy/
  payroll/
    controller/
    service/
    repository/
    model/
    dto/
    mapper/
  audit/
    service/
    repository/
    model/
```

### Why this is the recommended backend shape

- It keeps the current Spring stack intact.
- It still follows MVC best practice:
  - **Controller** = HTTP entrypoint
  - **Service** = use case orchestration/business rules
  - **Repository** = persistence access
  - **Model** = entity/domain model
  - **DTO / Mapper** = API view models
- It prevents a giant global `controller/`, `service/`, or `repository/` package from growing without boundaries.
- It makes future extraction to services possible if ever needed.

### 5.2 Backend standards to enforce

- Controllers never return JPA entities directly.
- Validation happens on request DTOs using Spring Validation.
- Authorization does not live only in controllers; business ownership checks also exist in services/policies.
- Shared response and error envelopes live in `common/api`.
- Shared exceptions and handlers live in `common/exception`.
- Security config, CORS, and auth helpers live in `common/security`.
- Use constructor injection only.
- Keep each class narrowly focused; avoid giant “god services.”

### 5.3 Database and migration strategy

Use **Flyway** for all forward schema changes.

Recommended migration path:

1. Keep current payroll tables working.
2. Add identity/auth tables.
3. Add `employee_profiles` and `payroll_runs`.
4. Migrate reads and writes to the new model.
5. Remove or archive legacy table usage only after cutover.

Recommended new tables:

- `user_accounts`
- `password_credentials`
- `employee_profiles`
- `payroll_runs`
- `audit_events`

Recommended simplification versus the architecture draft:

- Use **Spring Session + Redis** for active session storage.
- Do **not** create a custom relational `auth_sessions` table in the first release unless you explicitly need a “manage active sessions” admin UI.
- Capture login/logout/session-relevant history in `audit_events` instead.

This keeps the implementation simpler while preserving scalability.

### 5.4 Authentication and session model

Use **cookie-based session auth** with Spring Security.

Recommended details:

- session cookie: `HttpOnly`, `Secure` in production, `SameSite=Lax`
- CSRF required for all state-changing requests
- password hashing: `Argon2PasswordEncoder`
- session fixation protection on login
- rate limiting on login endpoints
- audit successful and failed login attempts

Role-specific login endpoints:

- `POST /api/v1/admin/auth/login`
- `POST /api/v1/employee/auth/login`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/me`

Behavior:

- Invalid credentials → `401`
- Valid credentials but wrong portal → `403 role_mismatch`
- Locked/disabled account → `423` or `403`, depending on final policy

### 5.5 Authorization model

Use **RBAC + ownership checks**.

Rules:

- `/api/v1/admin/**` → `MANAGER`
- `/api/v1/employee/**` → `EMPLOYEE`
- Employee self-service endpoints must derive employee identity from session, never from client-submitted user IDs.
- Any “employee record detail” endpoint must enforce that the record belongs to the authenticated employee.

Implementation pattern:

- Route-level security for broad access control
- Service/policy-level checks for resource ownership and finer authorization

### 5.6 API contract pattern

Use versioned REST under `/api/v1`.

Response envelope:

```json
{ "ok": true, "data": {} }
```

Error envelope:

```json
{
  "ok": false,
  "error": {
    "code": "validation_failed",
    "message": "invalid input",
    "fields": {}
  }
}
```

API rules:

- DTO-in, DTO-out
- pagination for admin collections
- field-level validation errors for forms
- centralized status code mapping

### 5.7 Next.js frontend strategy

Implement the frontend as **two separate Next.js App Router apps** in one monorepo:

```text
apps/
  admin-web/
  employee-web/
packages/
  ui/
  api-client/
  auth/
  types/
```

Recommended frontend tooling:

- Next.js App Router
- TypeScript strict mode
- pnpm workspaces
- Tailwind CSS
- shadcn/ui
- Lucide icons
- Zod
- React Hook Form for complex forms
- Vitest + React Testing Library
- Playwright

### Why two Next.js apps, not one

- Stronger separation of manager and employee experience
- Cleaner route protection and product language
- Easier theming and navigation divergence
- Less chance of leaking admin UI into employee flows

### Next.js implementation rules

- Default to **Server Components** for page-level reads.
- Use **Client Components** only for interactive islands: forms, filters, table controls, dialogs.
- Do not re-implement business logic in Next.js route handlers.
- Use the Spring Boot API as the system of record.
- Create shared auth helpers for server-side identity bootstrapping.
- Keep route groups separate for public vs protected areas.

Recommended app structure:

```text
apps/admin-web/
  app/
    (public)/login/page.tsx
    (protected)/layout.tsx
    (protected)/dashboard/page.tsx
    (protected)/employees/page.tsx
    (protected)/employees/[employeeId]/page.tsx
    (protected)/payroll-runs/page.tsx
    (protected)/payroll-runs/[payrollRunId]/page.tsx
    access-denied/page.tsx
  features/
    auth/
    dashboard/
    employees/
    payroll-runs/
  lib/
    server/
    client/

apps/employee-web/
  app/
    (public)/login/page.tsx
    (protected)/layout.tsx
    (protected)/dashboard/page.tsx
    (protected)/profile/page.tsx
    (protected)/payroll-history/page.tsx
    (protected)/payroll-history/[recordId]/page.tsx
    access-denied/page.tsx
  features/
    auth/
    dashboard/
    profile/
    payroll-history/
  lib/
    server/
    client/
```

### 5.8 Frontend data flow

Recommended pattern:

- Server Components fetch initial page data from Spring Boot.
- Interactive forms and tables use typed client helpers.
- Search/filter/sort state for admin tables should live in URL search params where practical.
- Protected page bootstrapping should call a shared server helper that verifies session and portal role.

### 5.9 UI/UX direction (recommended)

#### Shared design foundation

- Layout grid: 8px spacing system
- Radius: 12px default, 16px for hero/login cards
- Typography:
  - headings: `Plus Jakarta Sans`
  - body/data: `Inter`
- Motion: subtle only; 150–220ms transitions
- Icons: Lucide
- Contrast: WCAG AA minimum

#### Admin portal visual direction

- Mood: professional, trustworthy, efficient, data-oriented
- Structure: sidebar shell with sticky header
- Color direction:
  - primary: deep navy / indigo
  - accent: amber / gold for highlights and active states
  - surfaces: white / slate with strong contrast
- UI patterns:
  - stat cards
  - interactive data tables
  - filter bars
  - modal / drawer editing flows
  - report cards and trend visuals

Admin UX priorities:

- dense information without clutter
- fast scanning
- clear primary actions
- low-friction repetitive workflows

#### Employee portal visual direction

- Mood: calm, supportive, readable, mobile-first
- Structure: lighter shell with simpler navigation
- Color direction:
  - primary: blue / teal
  - accent: emerald for positive payroll states
  - surfaces: soft slate / white with generous spacing
- UI patterns:
  - summary cards
  - payroll history list or simplified table
  - profile card sections
  - friendly empty states and status callouts

Employee UX priorities:

- clarity over density
- easy mobile reading of payroll figures
- reduced jargon
- reassuring error and session-expired messaging

#### Shared design system package

`packages/ui` should include only domain-neutral primitives:

- button
- input
- select
- card
- badge
- alert
- dialog
- table primitives
- empty state
- skeleton
- shell/layout primitives

Feature-specific components stay inside each app’s `features/*/components` folders.

### 5.10 Recommended delivery strategy

- Backend-first foundation
- Admin portal before employee portal
- Shared design system before large page implementation
- Incremental cutover, not big-bang replacement

This is the best balance of security, maintainability, and UI quality.

---

## 6. Alternatives (with tradeoffs)

### Alternative A: Keep one frontend app and gate views by role

**Pros**

- fewer frontend deployables
- lower initial setup cost

**Cons**

- weaker product separation
- easier to leak admin UI into employee flows
- more complicated routing and shell conditions

**Recommendation**

- Not recommended.

### Alternative B: Keep the current static frontend and only add backend auth

**Pros**

- lowest initial engineering cost

**Cons**

- poor long-term maintainability
- weak UX foundation
- hard to scale design consistency and testability

**Recommendation**

- Not recommended.

### Alternative C: Move directly to microservices

**Pros**

- strong hard boundaries

**Cons**

- too much operational complexity for current scope
- slower delivery
- debugging and auth become much harder early on

**Recommendation**

- Not recommended for V2.

### Alternative D: Use JWT in browser storage instead of cookie sessions

**Pros**

- simpler for pure API clients

**Cons**

- worse default posture for browser apps
- logout/revocation more complex
- more exposure if frontend JavaScript is compromised

**Recommendation**

- Not recommended for this website.

### Recommended path

- **Spring Boot modular MVC backend**
- **Two Next.js App Router apps in one monorepo**
- **Cookie sessions + CSRF**
- **RBAC + ownership checks**
- **Shared design system + portal-specific shells**

---

## 7. Step plan (< 30 min each)

### Suggested PR breakdown

- **PR 1**: Backend common foundation + security dependencies
- **PR 2**: Database migrations + identity/auth model
- **PR 3**: Auth endpoints + session flow
- **PR 4**: Admin employee management APIs
- **PR 5**: Payroll run + employee self-service APIs
- **PR 6**: Next.js monorepo + shared design system foundation
- **PR 7**: Admin portal MVP
- **PR 8**: Employee portal MVP
- **PR 9**: Hardening, cutover, docs

> Verification commands below are **planned** commands for the future codebase where applicable.

### PR 1 — Backend common foundation

**Step 01 (20 min) — Add backend dependency foundation**

- Files: `backend/pom.xml`
- Changes: add Spring Security, Spring Validation, Flyway, Spring Session Redis, Redis starter dependencies.
- Verify: `cd backend && mvn test` still resolves dependencies and compiles.

**Step 02 (20 min) — Create common package skeleton**

- Files: `backend/src/main/java/com/payroll/common/**`
- Changes: add `api`, `config`, `exception`, `security`, `validation` package structure.
- Verify: app compiles with the new package structure present.

**Step 03 (20 min) — Introduce success/error envelope classes**

- Files: `backend/src/main/java/com/payroll/common/api/ApiResponse.java`, `ApiError.java`, `ApiSuccess.java`
- Changes: define shared response types used by all V2 controllers.
- Verify: simple unit test serializes the envelope correctly.

**Step 04 (25 min) — Replace ad hoc exception response mapping**

- Files: `backend/src/main/java/com/payroll/common/exception/GlobalExceptionHandler.java`, existing `controller/GlobalExceptionHandler.java`
- Changes: move handler to `common/exception`; map validation, auth, not-found, and conflict failures to the standard envelope.
- Verify: controller test confirms `400/401/403/404/409/422` shapes are consistent.

**Step 05 (20 min) — Add Spring MVC validation baseline**

- Files: `backend/src/main/java/com/payroll/common/validation/**`
- Changes: add reusable field-error mapping and request validation conventions.
- Verify: invalid DTO submission returns field-level errors.

**Step 06 (25 min) — Create security config skeleton**

- Files: `backend/src/main/java/com/payroll/common/security/SecurityConfig.java`
- Changes: define filter chain, public endpoints, protected namespaces, and default deny behavior.
- Verify: unauthenticated request to protected test endpoint returns `401`.

**Step 07 (20 min) — Centralize CORS configuration**

- Files: `backend/src/main/java/com/payroll/common/security/CorsConfig.java`, existing controllers
- Changes: remove `@CrossOrigin(origins = "*")` from controllers; move allowed origins into centralized config.
- Verify: existing local frontend origin works; unknown origin is rejected in integration tests.

**Step 08 (20 min) — Add application properties for security/session**

- Files: `backend/src/main/resources/application.properties`, `application-test.properties`
- Changes: add session, CSRF, Redis, allowed origins, and auth config placeholders.
- Verify: app starts with config keys present; tests still boot.

### PR 2 — Database migrations and identity/auth model

**Step 09 (25 min) — Introduce Flyway baseline**

- Files: `backend/src/main/resources/db/migration/V1__baseline_legacy_schema.sql`, `application.properties`
- Changes: move legacy schema bootstrap into Flyway-compatible migration strategy.
- Verify: clean local database boots through Flyway successfully.

**Step 10 (20 min) — Add identity/auth schema migration**

- Files: `backend/src/main/resources/db/migration/V2__identity_and_auth.sql`
- Changes: add `user_accounts`, `password_credentials`, `audit_events`, and supporting indexes.
- Verify: migration applies on a clean database.

**Step 11 (20 min) — Add employee profile and payroll run schema migration**

- Files: `backend/src/main/resources/db/migration/V3__employee_profiles_and_payroll_runs.sql`
- Changes: add `employee_profiles`, `payroll_runs`, new payroll foreign keys, and compatibility columns if needed.
- Verify: migration applies and tables exist with expected constraints.

**Step 12 (20 min) — Create auth/identity JPA models**

- Files: `backend/src/main/java/com/payroll/auth/model/**`, `identity/model/**`, `audit/model/**`
- Changes: add entities and enums for accounts, credentials, audit events, roles, statuses.
- Verify: JPA context loads cleanly.

**Step 13 (20 min) — Create repositories for auth/identity/audit**

- Files: `backend/src/main/java/com/payroll/auth/repository/**`, `identity/repository/**`, `audit/repository/**`
- Changes: add query interfaces for email lookup, profile linkage, and audit writes.
- Verify: repository tests pass with H2 or Testcontainers profile.

**Step 14 (20 min) — Add employee profile model and repository**

- Files: `backend/src/main/java/com/payroll/identity/model/EmployeeProfile.java`, repository classes
- Changes: separate employee profile persistence from legacy `Employee` model.
- Verify: create/read profile repository test passes.

**Step 15 (20 min) — Add payroll run model and repository**

- Files: `backend/src/main/java/com/payroll/payroll/model/PayrollRun.java`, repository classes
- Changes: add payroll-run aggregate and uniqueness rules.
- Verify: duplicate pay-period conflict can be detected in test.

**Step 16 (25 min) — Create migration compatibility read strategy**

- Files: payroll/identity services and repositories
- Changes: define how legacy `employees` data maps into `employee_profiles` during transition.
- Verify: compatibility service returns consistent values for a seeded record.

### PR 3 — Auth endpoints and session flow

**Step 17 (20 min) — Add password hashing service**

- Files: `backend/src/main/java/com/payroll/auth/service/PasswordService.java`
- Changes: configure Argon2 hashing and verification.
- Verify: unit test confirms hash/verify works and hashes differ for same input.

**Step 18 (25 min) — Add account authentication service**

- Files: `backend/src/main/java/com/payroll/auth/service/AuthenticationService.java`
- Changes: validate credentials, role eligibility, account status, and audit writes.
- Verify: service tests cover success, invalid password, disabled account, role mismatch.

**Step 19 (20 min) — Add login request/response DTOs**

- Files: `backend/src/main/java/com/payroll/auth/dto/**`
- Changes: create typed request and response models for admin and employee login.
- Verify: validation tests confirm required fields and email rules.

**Step 20 (25 min) — Implement admin auth controller**

- Files: `backend/src/main/java/com/payroll/auth/controller/AdminAuthController.java`
- Changes: add `POST /api/v1/admin/auth/login` with role restriction and session rotation.
- Verify: MVC test confirms manager passes and employee gets `403 role_mismatch`.

**Step 21 (25 min) — Implement employee auth controller**

- Files: `backend/src/main/java/com/payroll/auth/controller/EmployeeAuthController.java`
- Changes: add `POST /api/v1/employee/auth/login` with employee-only access.
- Verify: MVC test confirms employee passes and manager behavior matches decided policy.

**Step 22 (20 min) — Implement auth/me endpoint**

- Files: `backend/src/main/java/com/payroll/auth/controller/AuthSessionController.java`, auth service helpers
- Changes: add `GET /api/v1/auth/me` that returns minimal user context.
- Verify: authenticated test returns correct role and identity; unauthenticated test returns `401`.

**Step 23 (20 min) — Implement logout endpoint**

- Files: `backend/src/main/java/com/payroll/auth/controller/AuthSessionController.java`
- Changes: add `POST /api/v1/auth/logout` that invalidates session and records audit event.
- Verify: logout test invalidates follow-up authenticated request.

**Step 24 (25 min) — Wire CSRF token flow for browser clients**

- Files: `backend/src/main/java/com/payroll/common/security/**`
- Changes: expose CSRF token in a safe browser-consumable way for Next.js clients.
- Verify: state-changing endpoint rejects missing CSRF and accepts valid token.

**Step 25 (20 min) — Add login rate limiting hook**

- Files: `backend/src/main/java/com/payroll/auth/service/**`, security filter or config
- Changes: introduce per-identifier or per-IP rate limiting abstraction.
- Verify: repeated failed login test hits throttle behavior.

### PR 4 — Admin employee management APIs

**Step 26 (20 min) — Create admin employee DTOs and mappers**

- Files: `backend/src/main/java/com/payroll/admin/dto/**`, `mapper/**`
- Changes: define paginated list DTOs, create/update DTOs, and API mapping.
- Verify: mapper tests cover entity-to-response transformation.

**Step 27 (25 min) — Create admin employee service**

- Files: `backend/src/main/java/com/payroll/admin/service/AdminEmployeeService.java`
- Changes: implement list, create, and update flows against employee profiles.
- Verify: service tests cover campus filter, validation, and conflict handling.

**Step 28 (20 min) — Implement list employees endpoint**

- Files: `backend/src/main/java/com/payroll/admin/controller/AdminEmployeeController.java`
- Changes: add paginated `GET /api/v1/admin/employees`.
- Verify: MVC test confirms manager access and pagination envelope shape.

**Step 29 (20 min) — Implement create employee endpoint**

- Files: same controller + service
- Changes: add `POST /api/v1/admin/employees` with validation and optional account provisioning input.
- Verify: MVC test confirms `422` validation and `409` conflict behavior.

**Step 30 (20 min) — Implement update employee endpoint**

- Files: same controller + service
- Changes: add `PATCH /api/v1/admin/employees/{employeeId}`.
- Verify: MVC test confirms manager success and employee `403` deny path.

**Step 31 (20 min) — Add employee account provisioning flow**

- Files: `backend/src/main/java/com/payroll/identity/service/**`, admin service
- Changes: create linked employee login account and default password / invite flow hook.
- Verify: service test confirms linked account has `EMPLOYEE` role only.

**Step 32 (20 min) — Add admin dashboard summary endpoint**

- Files: `backend/src/main/java/com/payroll/admin/controller/AdminDashboardController.java`, service class
- Changes: add `/api/v1/admin/dashboard/summary` using new DTOs.
- Verify: manager gets summary; employee gets `403`.

### PR 5 — Payroll and employee self-service APIs

**Step 33 (20 min) — Create payroll run DTOs/mappers**

- Files: `backend/src/main/java/com/payroll/payroll/dto/**`, `mapper/**`
- Changes: define create request, summary response, detail response models.
- Verify: mapper tests pass for totals and counts.

**Step 34 (25 min) — Create payroll run service**

- Files: `backend/src/main/java/com/payroll/payroll/service/PayrollRunService.java`
- Changes: implement pay-period conflict checks, transactional run creation, record generation.
- Verify: service tests cover happy path and duplicate-period conflict.

**Step 35 (20 min) — Implement create payroll run endpoint**

- Files: `backend/src/main/java/com/payroll/admin/controller/AdminPayrollRunController.java`
- Changes: add `POST /api/v1/admin/payroll-runs`.
- Verify: MVC test confirms manager access, CSRF requirement, and conflict response.

**Step 36 (20 min) — Implement payroll run detail endpoint**

- Files: same controller + service
- Changes: add `GET /api/v1/admin/payroll-runs/{payrollRunId}`.
- Verify: response includes summary plus records page/detail structure.

**Step 37 (20 min) — Create employee self-service DTOs**

- Files: `backend/src/main/java/com/payroll/employee/dto/**`
- Changes: define employee dashboard, profile, payroll history, and record detail DTOs.
- Verify: DTO serialization tests pass.

**Step 38 (25 min) — Create employee self-service service**

- Files: `backend/src/main/java/com/payroll/employee/service/EmployeeSelfService.java`
- Changes: implement profile lookup and payroll record ownership filtering using session-derived identity.
- Verify: service test confirms one employee cannot read another employee’s data.

**Step 39 (20 min) — Implement employee dashboard endpoint**

- Files: `backend/src/main/java/com/payroll/employee/controller/EmployeeDashboardController.java`
- Changes: add `GET /api/v1/employee/dashboard/summary`.
- Verify: employee success, manager deny path.

**Step 40 (20 min) — Implement employee profile endpoint**

- Files: `backend/src/main/java/com/payroll/employee/controller/EmployeeProfileController.java`
- Changes: add `GET /api/v1/employee/profile`.
- Verify: employee gets own profile only.

**Step 41 (20 min) — Implement employee payroll history endpoints**

- Files: `backend/src/main/java/com/payroll/employee/controller/EmployeePayrollController.java`
- Changes: add list/detail endpoints for employee payroll history.
- Verify: `401`, `403`, and ownership behavior covered by MVC tests.

### PR 6 — Next.js monorepo and design system foundation

**Step 42 (20 min) — Add workspace root for frontend monorepo**

- Files: `/package.json`, `/pnpm-workspace.yaml`, `/.npmrc`, `/.gitignore`
- Changes: introduce workspaces for `apps/*` and `packages/*` and root scripts.
- Verify: `pnpm install` resolves workspace structure.

**Step 43 (25 min) — Scaffold `apps/admin-web`**

- Files: `apps/admin-web/**`
- Changes: create Next.js App Router app with TypeScript and Tailwind.
- Verify: `pnpm --filter admin-web dev` starts locally.

**Step 44 (25 min) — Scaffold `apps/employee-web`**

- Files: `apps/employee-web/**`
- Changes: create second Next.js App Router app.
- Verify: `pnpm --filter employee-web dev` starts locally.

**Step 45 (20 min) — Create shared package skeletons**

- Files: `packages/ui/**`, `packages/api-client/**`, `packages/auth/**`, `packages/types/**`
- Changes: add package manifests, TS configs, and exports.
- Verify: apps can import from shared packages.

**Step 46 (25 min) — Install and configure shadcn/ui foundation**

- Files: `packages/ui/**`, app Tailwind config, globals CSS
- Changes: add design tokens, CSS variables, theme primitives, shared base components.
- Verify: shared button/input/card render in both apps.

**Step 47 (20 min) — Add typography and token system**

- Files: `packages/ui/src/tokens/**`, app layouts, globals CSS
- Changes: add fonts, spacing tokens, color tokens, radius, shadows, and semantic surface variables.
- Verify: design tokens apply consistently in both apps.

**Step 48 (20 min) — Add shared API client package**

- Files: `packages/api-client/**`
- Changes: create typed fetch helpers, error parsing, CSRF handling hooks, and request wrappers.
- Verify: a mocked request test confirms envelope parsing.

**Step 49 (20 min) — Add shared auth helpers package**

- Files: `packages/auth/**`
- Changes: add server-side session bootstrap helpers and portal role guard helpers.
- Verify: unit tests cover “require manager” and “require employee” helpers.

**Step 50 (20 min) — Add frontend test tooling**

- Files: root config, app/package test configs
- Changes: configure Vitest, RTL, Playwright, and lint/typecheck scripts.
- Verify: `pnpm test` runs placeholder suites successfully.

### PR 7 — Admin portal MVP

**Step 51 (20 min) — Build admin root layout and shell**

- Files: `apps/admin-web/app/(protected)/layout.tsx`, feature shell components
- Changes: create sidebar, header, breadcrumb space, and content container.
- Verify: protected layout renders with navigation and responsive drawer behavior.

**Step 52 (20 min) — Build admin login page**

- Files: `apps/admin-web/app/(public)/login/page.tsx`, auth feature components
- Changes: create polished split-card login with inline validation and role-aware error treatment.
- Verify: component test covers validation and error states.

**Step 53 (20 min) — Add admin session guard flow**

- Files: `apps/admin-web/lib/server/**`, protected pages
- Changes: redirect unauthenticated users to admin login and authenticated wrong-role users to access denied.
- Verify: route protection test passes.

**Step 54 (20 min) — Build admin dashboard metric cards**

- Files: admin dashboard feature components
- Changes: create KPI cards, summary layout, loading skeletons.
- Verify: page renders skeleton, empty, and populated states.

**Step 55 (20 min) — Build admin employee list page shell**

- Files: `apps/admin-web/app/(protected)/employees/page.tsx`, employee feature components
- Changes: create page header, toolbar, table container, and empty state.
- Verify: page renders expected shells on mocked data.

**Step 56 (20 min) — Add employee table interactions**

- Files: employee feature client components
- Changes: add search, campus filter, pagination, and row action menu.
- Verify: component test confirms filter and pagination interactions.

**Step 57 (20 min) — Build employee create/edit forms**

- Files: `apps/admin-web/app/(protected)/employees/[employeeId]/page.tsx`, form components
- Changes: add forms with grouped sections, validation, compensation fields, and optional account provisioning UI.
- Verify: form test covers required fields and API error display.

**Step 58 (20 min) — Build payroll run list page shell**

- Files: payroll run feature pages/components
- Changes: create page shell, table/list, and action CTA.
- Verify: page renders empty and populated states with mocked data.

**Step 59 (20 min) — Build payroll run creation flow UI**

- Files: payroll run form components
- Changes: add pay-period form, campus scope selection, submit states, success confirmation.
- Verify: component test confirms form validation and success state.

**Step 60 (20 min) — Build payroll run detail page**

- Files: `apps/admin-web/app/(protected)/payroll-runs/[payrollRunId]/page.tsx`
- Changes: add summary cards, record table, and status callouts.
- Verify: detail page renders with mocked server data.

### PR 8 — Employee portal MVP

**Step 61 (20 min) — Build employee root layout and shell**

- Files: `apps/employee-web/app/(protected)/layout.tsx`, shell components
- Changes: create lighter navigation shell with mobile-first spacing and simple header.
- Verify: responsive shell works in desktop and mobile viewports.

**Step 62 (20 min) — Build employee login page**

- Files: `apps/employee-web/app/(public)/login/page.tsx`
- Changes: create approachable, uncluttered login page with clear payroll messaging.
- Verify: component test covers default/error/loading states.

**Step 63 (20 min) — Add employee session guard flow**

- Files: employee auth helpers and protected pages
- Changes: redirect unauthenticated users to employee login; reject wrong-role session.
- Verify: protected-route test covers redirect and access denied behavior.

**Step 64 (20 min) — Build employee dashboard page**

- Files: employee dashboard feature components
- Changes: create summary cards, recent pay snapshot, friendly guidance blocks.
- Verify: page supports loading, empty, and populated states.

**Step 65 (20 min) — Build employee profile page**

- Files: employee profile feature pages/components
- Changes: add profile cards, campus/position details, account status messaging.
- Verify: page renders server-fetched profile cleanly on mobile and desktop.

**Step 66 (20 min) — Build payroll history list page**

- Files: employee payroll-history feature components
- Changes: create simplified list/table, period filters, empty state, and status badges.
- Verify: filter interaction and empty state tests pass.

**Step 67 (20 min) — Build payroll record detail page**

- Files: `apps/employee-web/app/(protected)/payroll-history/[recordId]/page.tsx`
- Changes: create detail card layout for gross pay, deductions, and net pay with clear visual grouping.
- Verify: page test confirms readable breakdown and error handling.

**Step 68 (20 min) — Add mobile polish and accessibility pass**

- Files: employee shell/components/styles
- Changes: refine tap targets, spacing, semantic landmarks, and focus states.
- Verify: Playwright mobile viewport smoke test passes.

### PR 9 — Hardening, cutover, and docs

**Step 69 (20 min) — Add backend authorization integration tests**

- Files: backend test suites
- Changes: cover 401/403/ownership/security regressions for all critical endpoints.
- Verify: `cd backend && mvn test` passes with auth coverage.

**Step 70 (20 min) — Add frontend auth flow Playwright tests**

- Files: Playwright config and portal e2e specs
- Changes: cover admin login, employee login, unauthenticated redirect, forbidden route.
- Verify: `pnpm playwright test` passes locally against seeded environment.

**Step 71 (20 min) — Add audit logging around sensitive actions**

- Files: audit service integration points across auth/admin/payroll modules
- Changes: emit audit events for login success/failure, employee changes, payroll run creation.
- Verify: integration test confirms audit records are created.

**Step 72 (20 min) — Narrow production CORS and cookie domain config**

- Files: backend security config, environment docs
- Changes: set explicit admin/employee origins and cookie domain rules.
- Verify: staging config allows only known portals.

**Step 73 (20 min) — Freeze legacy frontend routes**

- Files: `frontend/**`, README, routing docs
- Changes: convert legacy landing/role pages into temporary redirect or deprecation notice path during cutover.
- Verify: old route no longer acts as an authorization boundary.

**Step 74 (20 min) — Update developer and ops documentation**

- Files: `README.md`, `docs/**`
- Changes: document local dev for backend + two Next apps + Redis, auth flow, and deployment model.
- Verify: a fresh contributor can follow the steps and run the system.

**Step 75 (25 min) — Run staging cutover rehearsal**

- Files: deployment config, runbook docs
- Changes: test admin portal, employee portal, auth, payroll flows, and rollback sequence in staging.
- Verify: checklist signoff with no blocking auth or data issues.

---

## 8. Test plan

### Backend

#### Unit tests

- `AuthenticationService`
- password hashing service
- role mismatch and account status logic
- employee ownership policy logic
- payroll run conflict and calculation rules
- DTO mapper tests where logic is non-trivial

#### Controller/security tests

- login endpoints
- `auth/me`
- logout
- `401` for unauthenticated protected routes
- `403` for wrong-role access
- CSRF enforcement on state-changing endpoints
- validation error envelope shape

Recommended style:

- `@WebMvcTest` for controller/security slices
- Mockito for service seams

#### Integration tests

- Spring Boot app boot with Flyway migrations
- repository integration tests
- payroll run creation transaction behavior
- audit event writes

Recommended best-practice improvement:

- Use **Testcontainers** for MySQL and Redis integration tests on the security/session path.
- Keep fast unit tests for pure service logic.

### Frontend

#### Unit/component tests

- form validation behavior
- table filtering and pagination interactions
- protected-layout redirect logic where testable
- loading/empty/error state rendering
- accessible labels and button behavior

Tools:

- Vitest
- React Testing Library
- user-event

#### End-to-end tests

Critical flows only:

- admin login success
- employee login success
- employee denied from admin route
- unauthenticated user redirected to correct login
- manager creates payroll run
- employee views own payroll history

Tool:

- Playwright

### Regression coverage priorities

- Cross-portal login rejection
- Ownership enforcement for employee record detail
- session expiry handling
- CSRF rejection for missing token
- payroll-run duplicate prevention

---

## 9. Rollout & rollback plan

### Rollout plan

#### Phase A — Additive backend rollout

- Deploy new schema migrations and V2 backend code while legacy frontend still exists.
- Keep legacy `/api` behavior stable during initial backend preparation.
- Add `/api/v1` endpoints in parallel.

#### Phase B — Admin portal soft launch

- Deploy `admin-web` behind a non-public or limited-access environment first.
- Seed or provision initial manager accounts.
- Validate manager login, employee management, and payroll-run creation in staging then production soft launch.

#### Phase C — Employee portal launch

- Provision employee accounts in batches.
- Deploy `employee-web` and validate self-service flows.
- Monitor `401`, `403`, login errors, and support issues closely.

#### Phase D — Legacy deprecation

- Remove legacy role-selection flow from being authoritative.
- Redirect users to the correct new portal entry point.
- Keep legacy pages only as temporary informational shims if needed.

### Rollback plan

- Frontend rollback:
  - revert admin/employee portal traffic to previous known-good deployment
  - restore temporary legacy entry point if needed
- Backend rollback:
  - keep additive schema changes in place
  - disable or stop routing to new `/api/v1` functionality if severe issues appear
  - preserve old endpoint behavior until confidence is high
- Session rollback:
  - invalidate problematic sessions if auth behavior is incorrect
- Data rollback:
  - payroll-run creation should be transactional; bad runs should be cancellable or soft-invalidated rather than deleted blindly

### Why this rollout is safe

- additive migrations
- backend-first parallel path
- no forced big-bang switch
- legacy routes remain available until V2 is stable

---

## 10. Risks & mitigations

| Risk                                                                | Why it matters                              | Mitigation                                                                             |
| ------------------------------------------------------------------- | ------------------------------------------- | -------------------------------------------------------------------------------------- |
| Auth misconfiguration locks users out                               | Security changes are brittle early          | Build auth in isolation, add controller/security tests before portal rollout           |
| Employee reaches manager features through leftover legacy endpoints | Old surface area is currently open          | Add `/api/v1` namespaces, test deny paths, deprecate old endpoints quickly             |
| Cookie/session behavior breaks across local/staging domains         | Next.js + Spring + subdomains can be tricky | Decide cookie domain strategy early, test on staging domains, document local dev setup |
| React/Next migration scope balloons                                 | Frontend rewrite can slip                   | Deliver portal shells and MVP pages first, polish second                               |
| Design system becomes a time sink                                   | UI quality work can over-expand             | Build only proven shared primitives first; keep feature UI local until reuse is real   |
| New auth tables drift from payroll identity reality                 | Employee/user linkage is critical           | Introduce explicit employee-to-account mapping and test it thoroughly                  |
| Redis becomes a deployment blocker                                  | Sessions depend on it in the target design  | Support a local-dev Redis bootstrap path and validate it early                         |
| Payroll migration introduces reporting inconsistencies              | Financial data trust is critical            | Keep legacy reads parallel during migration and compare totals during rollout          |

---

## 11. Open questions

- Should a manager be allowed to also access the employee portal, or should cross-portal login be blocked both ways?
- What is the employee account bootstrap flow:
  - manager sets initial password
  - temporary password
  - password-reset email flow
- Is Redis acceptable as a required dependency in all environments, including local development?
- Do you want PDF payslip export in the employee portal V2 MVP?
- Should employee profile editing be self-service in V2, or read-only initially?
- Do you want admin reporting beyond dashboard totals, such as charts, CSV export, or trends?
- Are campus values fixed to Casal and Arlegui, or should campus become configurable master data?
- Do you want light mode only for V2 launch, or should dark mode be designed from the beginning?
- Is pnpm acceptable as the new frontend package manager, or do you prefer npm despite the monorepo tradeoff?
- Should we update `docs/payroll-v2-architecture.md` afterward so it explicitly says “Next.js” instead of generic “React apps”?
