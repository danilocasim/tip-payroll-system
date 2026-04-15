CREATE TABLE IF NOT EXISTS employee_profiles (
    id                CHAR(36) PRIMARY KEY,
    employee_number   VARCHAR(50) NOT NULL,
    first_name        VARCHAR(100),
    last_name         VARCHAR(100),
    full_name         VARCHAR(200) NOT NULL,
    campus            VARCHAR(50),
    position          VARCHAR(100),
    work_area         VARCHAR(50),
    hourly_rate       DECIMAL(10,2) DEFAULT 0,
    employment_status VARCHAR(20) NOT NULL,
    legacy_employee_id INT,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_employee_profiles_employee_number UNIQUE (employee_number),
    CONSTRAINT uk_employee_profiles_legacy_employee UNIQUE (legacy_employee_id),
    CONSTRAINT fk_employee_profiles_legacy_employee
        FOREIGN KEY (legacy_employee_id) REFERENCES employees(employee_id)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS user_accounts (
    id                  CHAR(36) PRIMARY KEY,
    email               VARCHAR(255) NOT NULL,
    role                VARCHAR(20) NOT NULL,
    status              VARCHAR(20) NOT NULL,
    employee_profile_id CHAR(36),
    last_login_at       TIMESTAMP NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_accounts_email UNIQUE (email),
    CONSTRAINT uk_user_accounts_employee_profile UNIQUE (employee_profile_id),
    CONSTRAINT fk_user_accounts_employee_profile
        FOREIGN KEY (employee_profile_id) REFERENCES employee_profiles(id)
            ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS password_credentials (
    id                   CHAR(36) PRIMARY KEY,
    user_account_id      CHAR(36) NOT NULL,
    password_hash        VARCHAR(255) NOT NULL,
    password_algorithm   VARCHAR(50) NOT NULL,
    password_updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_password_credentials_user UNIQUE (user_account_id),
    CONSTRAINT fk_password_credentials_user
        FOREIGN KEY (user_account_id) REFERENCES user_accounts(id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS audit_events (
    id             CHAR(36) PRIMARY KEY,
    actor_user_id  CHAR(36),
    event_type     VARCHAR(100) NOT NULL,
    resource_type  VARCHAR(100),
    resource_id    VARCHAR(100),
    result         VARCHAR(20) NOT NULL,
    metadata_json  TEXT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_events_actor_user
        FOREIGN KEY (actor_user_id) REFERENCES user_accounts(id)
            ON DELETE SET NULL
);

CREATE INDEX idx_user_accounts_role_status ON user_accounts(role, status);
CREATE INDEX idx_employee_profiles_campus_status ON employee_profiles(campus, employment_status);
CREATE INDEX idx_audit_events_type_created_at ON audit_events(event_type, created_at);
CREATE INDEX idx_audit_events_actor_created_at ON audit_events(actor_user_id, created_at);
