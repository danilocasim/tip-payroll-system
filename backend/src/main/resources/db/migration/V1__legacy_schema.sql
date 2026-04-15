CREATE TABLE IF NOT EXISTS employees (
    employee_id  INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    position     VARCHAR(100),
    work_area    VARCHAR(50),
    campus       VARCHAR(50),
    hourly_rate  DECIMAL(10,2) DEFAULT 0,
    hours_worked DECIMAL(10,2) DEFAULT 0,
    salary       DECIMAL(10,2) DEFAULT 0,
    bonus        DECIMAL(10,2) DEFAULT 0,
    deductions   DECIMAL(10,2) DEFAULT 0,
    pay_period   VARCHAR(20),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payroll_records (
    record_id    INT AUTO_INCREMENT PRIMARY KEY,
    employee_id  INT,
    salary       DECIMAL(10,2),
    bonus        DECIMAL(10,2),
    deductions   DECIMAL(10,2),
    net_pay      DECIMAL(10,2),
    pay_period   VARCHAR(20),
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payroll_records_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
            ON DELETE CASCADE
);
