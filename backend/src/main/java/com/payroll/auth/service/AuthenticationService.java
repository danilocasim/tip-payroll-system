package com.payroll.auth.service;

import com.payroll.audit.model.AuditResult;
import com.payroll.audit.service.AuditService;
import com.payroll.common.exception.ForbiddenException;
import com.payroll.common.exception.InvalidCredentialsException;
import com.payroll.common.exception.LockedException;
import com.payroll.common.security.SessionUser;
import com.payroll.identity.model.AccountStatus;
import com.payroll.identity.model.PasswordCredential;
import com.payroll.identity.model.UserAccount;
import com.payroll.identity.model.UserRole;
import com.payroll.identity.repository.PasswordCredentialRepository;
import com.payroll.identity.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuthenticationService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordCredentialRepository passwordCredentialRepository;
    private final PasswordService passwordService;
    private final AuditService auditService;

    public AuthenticationService(
            UserAccountRepository userAccountRepository,
            PasswordCredentialRepository passwordCredentialRepository,
            PasswordService passwordService,
            AuditService auditService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordCredentialRepository = passwordCredentialRepository;
        this.passwordService = passwordService;
        this.auditService = auditService;
    }

    @Transactional
    public SessionUser authenticate(String portal, String email, String password, UserRole requiredRole) {
        String normalizedEmail = email.trim().toLowerCase();
        UserAccount account = userAccountRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> invalidCredentials(normalizedEmail, portal));

        PasswordCredential credential = passwordCredentialRepository.findByUserAccountId(account.getId())
                .orElseThrow(() -> invalidCredentials(normalizedEmail, portal));

        if (!passwordService.matches(password, credential.getPasswordHash())) {
            throw invalidCredentials(normalizedEmail, portal);
        }

        if (account.getStatus() == AccountStatus.LOCKED) {
            auditService.record(account, "auth.login", "portal", portal, AuditResult.FAILURE, Map.of("reason", "account_locked"));
            throw new LockedException("account is locked");
        }

        if (account.getStatus() == AccountStatus.DISABLED) {
            auditService.record(account, "auth.login", "portal", portal, AuditResult.FAILURE, Map.of("reason", "account_disabled"));
            throw new ForbiddenException("account is disabled");
        }

        if (account.getRole() != requiredRole) {
            auditService.record(account, "auth.login", "portal", portal, AuditResult.FAILURE, Map.of("reason", "role_mismatch"));
            throw new InvalidCredentialsException("invalid credentials");
        }

        account.setLastLoginAt(LocalDateTime.now());
        userAccountRepository.save(account);
        auditService.record(account, "auth.login", "portal", portal, AuditResult.SUCCESS, Map.of("role", account.getRole().name()));

        return new SessionUser(
                account.getId(),
                account.getEmail(),
                account.getRole(),
                account.getEmployeeProfile() == null ? null : account.getEmployeeProfile().getId()
        );
    }

    private InvalidCredentialsException invalidCredentials(String email, String portal) {
        auditService.record(null, "auth.login", "portal", portal, AuditResult.FAILURE, Map.of("reason", "invalid_credentials", "email", email));
        return new InvalidCredentialsException("invalid credentials");
    }
}
