package com.payroll.auth.service;

import com.payroll.audit.model.AuditResult;
import com.payroll.audit.service.AuditService;
import com.payroll.auth.dto.EmployeeInviteViewDto;
import com.payroll.auth.model.EmployeeInvite;
import com.payroll.auth.repository.EmployeeInviteRepository;
import com.payroll.common.exception.InvalidInviteException;
import com.payroll.common.exception.InviteExpiredException;
import com.payroll.identity.model.AccountStatus;
import com.payroll.identity.model.PasswordCredential;
import com.payroll.identity.model.UserAccount;
import com.payroll.identity.repository.PasswordCredentialRepository;
import com.payroll.identity.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;

@Service
public class EmployeeInviteService {
    private static final int INVITE_TOKEN_BYTES = 24;
    private static final long INVITE_TTL_HOURS = 72;

    private final EmployeeInviteRepository employeeInviteRepository;
    private final PasswordCredentialRepository passwordCredentialRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordService passwordService;
    private final AuditService auditService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String employeePortalBaseUrl;

    public EmployeeInviteService(
            EmployeeInviteRepository employeeInviteRepository,
            PasswordCredentialRepository passwordCredentialRepository,
            UserAccountRepository userAccountRepository,
            PasswordService passwordService,
            AuditService auditService,
            @Value("${app.employee-portal.base-url:http://localhost:3001}") String employeePortalBaseUrl
    ) {
        this.employeeInviteRepository = employeeInviteRepository;
        this.passwordCredentialRepository = passwordCredentialRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordService = passwordService;
        this.auditService = auditService;
        this.employeePortalBaseUrl = employeePortalBaseUrl;
    }

    @Transactional
    public ProvisionedInvite issueInvite(UserAccount employeeAccount, UserAccount actor) {
        employeeInviteRepository.deleteByUserAccountId(employeeAccount.getId());

        byte[] tokenBytes = new byte[INVITE_TOKEN_BYTES];
        secureRandom.nextBytes(tokenBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        EmployeeInvite invite = new EmployeeInvite();
        invite.setUserAccount(employeeAccount);
        invite.setCreatedByUser(actor);
        invite.setTokenHash(hashToken(rawToken));
        invite.setExpiresAt(LocalDateTime.now().plusHours(INVITE_TTL_HOURS));
        employeeInviteRepository.save(invite);

        auditService.record(actor, "employee.invite.issued", "user_account", employeeAccount.getId(), AuditResult.SUCCESS,
                Map.of("employeeEmail", employeeAccount.getEmail(), "expiresAt", invite.getExpiresAt().toString()));

        return new ProvisionedInvite(rawToken, employeePortalBaseUrl + "/activate/" + rawToken, invite.getExpiresAt());
    }

    @Transactional(readOnly = true)
    public EmployeeInviteViewDto getInviteView(String rawToken) {
        EmployeeInvite invite = requireUsableInvite(rawToken);
        UserAccount account = invite.getUserAccount();
        String employeeName = account.getEmployeeProfile() == null ? account.getEmail() : account.getEmployeeProfile().getFullName();
        return new EmployeeInviteViewDto(employeeName, account.getEmail(), invite.getExpiresAt());
    }

    @Transactional
    public void activateInvite(String rawToken, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("password confirmation does not match");
        }

        EmployeeInvite invite = requireUsableInvite(rawToken);
        UserAccount account = invite.getUserAccount();

        PasswordCredential credential = passwordCredentialRepository.findByUserAccountId(account.getId()).orElseGet(() -> {
            PasswordCredential created = new PasswordCredential();
            created.setUserAccount(account);
            created.setPasswordAlgorithm("argon2");
            return created;
        });

        credential.setPasswordHash(passwordService.hash(password));
        passwordCredentialRepository.save(credential);

        account.setStatus(AccountStatus.ACTIVE);
        userAccountRepository.save(account);

        invite.setConsumedAt(LocalDateTime.now());
        employeeInviteRepository.save(invite);

        auditService.record(account, "employee.invite.activated", "user_account", account.getId(), AuditResult.SUCCESS,
                Map.of("inviteId", invite.getId()));
    }

    private EmployeeInvite requireUsableInvite(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new InvalidInviteException("invite link is invalid");
        }

        EmployeeInvite invite = employeeInviteRepository.findByTokenHash(hashToken(rawToken))
                .orElseThrow(() -> new InvalidInviteException("invite link is invalid"));

        if (invite.isConsumed()) {
            throw new InvalidInviteException("invite link has already been used");
        }

        if (invite.isExpired(LocalDateTime.now())) {
            throw new InviteExpiredException("invite link has expired");
        }

        return invite;
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }

    public record ProvisionedInvite(String token, String inviteUrl, LocalDateTime expiresAt) {
    }
}
