package com.payroll.identity.service;

import com.payroll.audit.model.AuditResult;
import com.payroll.audit.service.AuditService;
import com.payroll.auth.service.PasswordService;
import com.payroll.common.config.BootstrapAdminProperties;
import com.payroll.identity.model.AccountStatus;
import com.payroll.identity.model.PasswordCredential;
import com.payroll.identity.model.UserAccount;
import com.payroll.identity.model.UserRole;
import com.payroll.identity.repository.PasswordCredentialRepository;
import com.payroll.identity.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class BootstrapAdminInitializer implements CommandLineRunner {
    private final BootstrapAdminProperties properties;
    private final UserAccountRepository userAccountRepository;
    private final PasswordCredentialRepository passwordCredentialRepository;
    private final PasswordService passwordService;
    private final AuditService auditService;

    public BootstrapAdminInitializer(
            BootstrapAdminProperties properties,
            UserAccountRepository userAccountRepository,
            PasswordCredentialRepository passwordCredentialRepository,
            PasswordService passwordService,
            AuditService auditService
    ) {
        this.properties = properties;
        this.userAccountRepository = userAccountRepository;
        this.passwordCredentialRepository = passwordCredentialRepository;
        this.passwordService = passwordService;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (properties.getEmail() == null || properties.getEmail().isBlank() || properties.getPassword() == null || properties.getPassword().isBlank()) {
            return;
        }
        if (userAccountRepository.existsByEmailIgnoreCase(properties.getEmail())) {
            return;
        }

        UserAccount account = new UserAccount();
        account.setEmail(properties.getEmail());
        account.setRole(UserRole.MANAGER);
        account.setStatus(AccountStatus.ACTIVE);
        userAccountRepository.save(account);

        PasswordCredential credential = new PasswordCredential();
        credential.setUserAccount(account);
        credential.setPasswordAlgorithm("argon2");
        credential.setPasswordHash(passwordService.hash(properties.getPassword()));
        passwordCredentialRepository.save(credential);

        auditService.record(account, "bootstrap.admin.created", "user_account", account.getId(), AuditResult.SUCCESS, Map.of("name", properties.getName()));
    }
}
