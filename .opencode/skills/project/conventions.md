# Project Conventions

## When to load

- You are changing backend code, frontend code, tests, or docs in this repo.
- You need the repo's real commands, structure, or ownership boundaries.

## When NOT to load

- You are answering a pure conceptual question with no repo-specific change.

## Core rules

- Run Maven from `backend/`; that is where `pom.xml` lives.
- Keep backend business rules in `backend/src/main/java/com/payroll/service`.
- Keep HTTP contracts in controllers and update frontend callers under `frontend/js` when routes or payloads change.
- Keep automated tests in `tests/src/test/java`; Maven includes that directory through `build-helper-maven-plugin`.
- Treat `README.md` as the checked-in source for setup, run steps, and role behavior.

## Stack

- Backend: Java 17, Maven, Spring Boot 3.3.4, Spring Web, and Spring Data JPA.
- Data: MySQL runtime configuration in `backend/src/main/resources/application.properties` and schema in `backend/src/main/resources/schema.sql`.
- Tests: JUnit 5, Mockito, and H2 in-memory settings from `backend/src/main/resources/application-test.properties`.
- Frontend: static HTML pages in `frontend/`, shared CSS in `frontend/css/styles.css`, and vanilla JavaScript in `frontend/js`.
- Discovery notes: no `package.json`, JS lockfile, Ruby/Python/Go/Rust/Flutter manifest, or checked-in CI workflow was found.

## Repo Structure

- `backend/`: Spring Boot application and Maven build.
- `backend/src/main/java/com/payroll/controller`: REST endpoints under `/api`.
- `backend/src/main/java/com/payroll/service`: payroll calculations, validation, dashboard, and report logic.
- `backend/src/main/java/com/payroll/repository`: JPA repository interfaces.
- `backend/src/main/java/com/payroll/model`: entity models.
- `backend/src/main/resources`: datasource config, test config, and SQL schema.
- `frontend/`: static pages including `landing.html`, `dashboard.html`, `employees.html`, `add-employee.html`, `edit-employee.html`, and `payroll-report.html`.
- `frontend/js`: shared browser helpers in `common.js` plus page-specific scripts.
- `frontend/css`: shared stylesheet.
- `tests/src/test/java`: backend tests wired into Maven from outside `backend/`.
- `doc/`: documentation assets checked into the repo.

## Commands (Copy/Paste)

Run the backend:

```bash
cd backend
mvn spring-boot:run
```

Run tests:

```bash
cd backend
mvn test
```

No repo-defined frontend serve command was found. `README.md` says to open `frontend/landing.html` in a browser or serve the folder with any static file server.

## Boundaries and Ownership

- `EmployeeController` owns `/api/employees`.
- `PayrollController` owns `/api/payroll/report`, `/api/payroll/save`, and `/api/dashboard/summary`.
- `EmployeeService` owns employee validation and salary recomputation from `hourlyRate * hoursWorked`.
- `PayrollService` owns report rows, totals, dashboard summary, payroll snapshot saves, and net pay calculation.
- `frontend/js/common.js` owns `API_BASE`, role/session helpers, nav rendering, money formatting, and shared API error parsing.
- Page scripts under `frontend/js/*.js` should stay focused on DOM behavior and API calls, not duplicate payroll math already handled in backend services.
- Role access is frontend-only via `sessionStorage`; backend APIs are currently open.

## Testing Strategy

- Use `mvn test` from `backend/`.
- Current automated tests live in `tests/src/test/java/com/payroll` and cover service behavior and report totals.
- Tests run with the H2 in-memory datasource declared in `backend/src/main/resources/application-test.properties`.
- No checked-in frontend automated test runner or CI workflow was found, so backend tests are the observed regression safety net.

## Docs and Public APIs

- `README.md` is the checked-in source for setup, runtime steps, troubleshooting, and role behavior.
- Public HTTP endpoints currently exposed include `GET /api/employees`, `GET /api/employees/{id}`, `POST /api/employees`, `PUT /api/employees/{id}`, `DELETE /api/employees/{id}`, `GET /api/payroll/report`, `POST /api/payroll/save`, and `GET /api/dashboard/summary`.
- When changing routes, payload shapes, backend port, datasource setup, or role behavior, update both the backend source and the matching frontend callers in `frontend/js`, then update `README.md`.

## Security Notes

- `backend/src/main/resources/application.properties` contains datasource configuration. Do not copy credential values into docs, skills, commits, or logs.
- `README.md` already points to `SPRING_DATASOURCE_PASSWORD` as a safer path when passwords contain special characters; prefer environment variables over hardcoding new secrets.
- Controllers currently allow `@CrossOrigin(origins = "*")`.
- README states there is no login or backend auth; frontend role gating controls UI visibility only.

## Load Order

- Load `project/SKILL.md` first.
- Load `project/conventions.md` before non-trivial repo changes.
- If you change controller routes or response shapes, then load global `api` and `documentation`.
- If you change SQL, JPA entities, or datasource setup, then load global `database`.
- If you change role handling, CORS, secrets, or exposed endpoints, then load global `security`.
- If you change behavior, then load global `testing`.
- If you change frontend JavaScript behavior, then load global `javascript`.

## Minimal examples

Observed source paths:

```text
backend/pom.xml
backend/src/main/java/com/payroll/controller/EmployeeController.java
backend/src/main/java/com/payroll/controller/PayrollController.java
backend/src/main/java/com/payroll/service/EmployeeService.java
backend/src/main/java/com/payroll/service/PayrollService.java
backend/src/main/resources/application.properties
backend/src/main/resources/application-test.properties
backend/src/main/resources/schema.sql
frontend/js/common.js
tests/src/test/java/com/payroll/EmployeeServiceTest.java
tests/src/test/java/com/payroll/PayrollServiceTest.java
tests/src/test/java/com/payroll/PayrollReportTest.java
```

Observed local QA flow:

```bash
cd backend
mvn test
```

## Anti-patterns

- Adding new commands or toolchains to this skill before they exist in the repo or `README.md`.
- Editing frontend pages as if they enforce backend security.
- Duplicating payroll calculations across page scripts instead of keeping business rules in backend services.
- Moving tests into `backend/src/test/java` without also updating the repo convention; the current Maven build intentionally pulls tests from `../tests/src/test/java`.

## Checklist

Before you commit:

- Run `cd backend && mvn test`.
- Check whether controller or payload changes require matching `frontend/js` updates.
- Update `README.md` if setup, run steps, routes, or role behavior changed.
- Do not copy datasource secrets into docs or new files.
