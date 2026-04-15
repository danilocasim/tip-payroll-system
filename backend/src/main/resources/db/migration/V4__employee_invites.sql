CREATE TABLE IF NOT EXISTS employee_invites (
    id                 CHAR(36) PRIMARY KEY,
    user_account_id    CHAR(36) NOT NULL,
    created_by_user_id CHAR(36),
    token_hash         VARCHAR(128) NOT NULL,
    expires_at         TIMESTAMP NOT NULL,
    consumed_at        TIMESTAMP NULL,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_employee_invites_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_employee_invites_user
        FOREIGN KEY (user_account_id) REFERENCES user_accounts(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_employee_invites_created_by
        FOREIGN KEY (created_by_user_id) REFERENCES user_accounts(id)
            ON DELETE SET NULL
);

CREATE INDEX idx_employee_invites_user ON employee_invites(user_account_id);
CREATE INDEX idx_employee_invites_expires_at ON employee_invites(expires_at);
