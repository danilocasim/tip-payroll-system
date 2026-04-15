CREATE TABLE IF NOT EXISTS payroll_runs (
    id                 CHAR(36) PRIMARY KEY,
    pay_period_start   DATE NOT NULL,
    pay_period_end     DATE NOT NULL,
    campus_scope       VARCHAR(50),
    status             VARCHAR(20) NOT NULL,
    created_by_user_id CHAR(36) NOT NULL,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finalized_at       TIMESTAMP NULL,
    CONSTRAINT fk_payroll_runs_created_by_user
        FOREIGN KEY (created_by_user_id) REFERENCES user_accounts(id)
            ON DELETE RESTRICT
);

ALTER TABLE payroll_records
    ADD COLUMN payroll_run_id CHAR(36) NULL;

ALTER TABLE payroll_records
    ADD COLUMN employee_profile_id CHAR(36) NULL;

ALTER TABLE payroll_records
    ADD COLUMN pay_period_start DATE NULL;

ALTER TABLE payroll_records
    ADD COLUMN pay_period_end DATE NULL;

CREATE INDEX idx_payroll_runs_pay_period_end_status ON payroll_runs(pay_period_end, status);
CREATE INDEX idx_payroll_records_employee_profile_period_end ON payroll_records(employee_profile_id, pay_period_end);
