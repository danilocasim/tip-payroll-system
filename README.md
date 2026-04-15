# TIP Cafeteria Payroll Management System

Full-stack payroll website for Technological Institute of the Philippines cafeteria staff (Casal and Arlegui campuses), with frontend role selection (`manager` / `employee`) using `sessionStorage` only.

## 1) Local MySQL setup

Default local development settings in `backend/src/main/resources/application.properties` point to:

- Host: `127.0.0.1:3306`
- Database: `payroll_system`
- Username: `payroll_app`
- Password: `localpayroll`

If you want different credentials, override them with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`.

Load the schema once using `backend/src/main/resources/schema.sql` if your local database is empty.

## 2) Run Spring Boot backend

```bash
cd payroll-system/backend
mvn spring-boot:run
```

Backend starts on `http://localhost:8080`.

## 3) Run tests

```bash
cd payroll-system/backend
mvn test
```

Tests use H2 in-memory DB (`application-test.properties`, MySQL compatibility mode).

## 4) Open frontend

1. **Start the backend first** (`mvn spring-boot:run`). Saving employees calls `http://localhost:8080/api`; if the server is not running, the form will show an error under the Save button.
2. Open `payroll-system/frontend/landing.html` in your browser (or serve the folder with any static file server).

### Troubleshooting “Save does nothing” / add employee fails

- Confirm the terminal running Spring Boot shows **Started PayrollApplication** and no database connection errors.
- In the browser, open **Developer Tools → Console** and look for red errors when you click Save.
- If your local MySQL server is not running, start it before launching Spring Boot.
- If you use different local credentials, export `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` before running `mvn spring-boot:run`.

## 5) Role system behavior

- No login, no auth, no passwords.
- User chooses role on `landing.html`.
- Selection is stored with:
  - `sessionStorage.setItem('role', 'manager')`
  - `sessionStorage.setItem('role', 'employee')`
- Every non-landing page calls `checkRole()` and redirects to `landing.html` if role is missing.
- Manager-only UI is hidden for employees by `.manager-only` elements.
- "Switch Role" clears session and returns to landing page.

## Notes

- Salary is auto-computed: `hourlyRate * hoursWorked`
- Net Pay is computed: `salary + bonus - deductions`
- Backend APIs are open (frontend only enforces role-based visibility)
# tip-payroll-system
