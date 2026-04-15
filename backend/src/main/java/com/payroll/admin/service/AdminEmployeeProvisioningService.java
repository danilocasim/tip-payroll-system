package com.payroll.admin.service;

import com.payroll.admin.dto.AdminCreateEmployeeRequest;
import com.payroll.admin.dto.ProvisionedEmployeeDto;
import com.payroll.audit.model.AuditResult;
import com.payroll.audit.service.AuditService;
import com.payroll.auth.service.EmployeeInviteService;
import com.payroll.common.config.CampusCatalog;
import com.payroll.common.exception.ConflictException;
import com.payroll.common.exception.NotFoundException;
import com.payroll.common.security.SessionUser;
import com.payroll.identity.model.AccountStatus;
import com.payroll.identity.model.EmployeeProfile;
import com.payroll.identity.model.EmploymentStatus;
import com.payroll.identity.model.UserAccount;
import com.payroll.identity.model.UserRole;
import com.payroll.identity.repository.EmployeeProfileRepository;
import com.payroll.identity.repository.UserAccountRepository;
import com.payroll.model.Employee;
import com.payroll.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminEmployeeProvisioningService {
    private final EmployeeService employeeService;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final UserAccountRepository userAccountRepository;
    private final EmployeeInviteService employeeInviteService;
    private final AuditService auditService;

    public AdminEmployeeProvisioningService(
            EmployeeService employeeService,
            EmployeeProfileRepository employeeProfileRepository,
            UserAccountRepository userAccountRepository,
            EmployeeInviteService employeeInviteService,
            AuditService auditService
    ) {
        this.employeeService = employeeService;
        this.employeeProfileRepository = employeeProfileRepository;
        this.userAccountRepository = userAccountRepository;
        this.employeeInviteService = employeeInviteService;
        this.auditService = auditService;
    }

    @Transactional
    public ProvisionedEmployeeDto createEmployee(AdminCreateEmployeeRequest request, SessionUser actor) {
        if (employeeProfileRepository.existsByEmployeeNumberIgnoreCase(request.employeeNumber())) {
            throw new ConflictException("employee number already exists");
        }

        if (request.createPortalAccess() && (request.email() == null || request.email().isBlank())) {
            throw new IllegalArgumentException("email is required when portal access is enabled");
        }

        if (request.createPortalAccess() && userAccountRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("email already exists");
        }

        String campus = CampusCatalog.normalize(request.campus());

        Employee employee = new Employee();
        employee.setName(request.firstName().trim() + " " + request.lastName().trim());
        employee.setCampus(campus);
        employee.setPosition(request.position());
        employee.setWorkArea(request.workArea());
        employee.setHourlyRate(request.hourlyRate());
        employee.setHoursWorked(request.hoursWorked());
        employee.setBonus(request.bonus());
        employee.setDeductions(request.deductions());
        employee.setPayPeriod(request.payPeriod());
        Employee savedEmployee = employeeService.addEmployee(employee);

        EmployeeProfile profile = new EmployeeProfile();
        profile.setEmployeeNumber(request.employeeNumber());
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setFullName(savedEmployee.getName());
        profile.setCampus(campus);
        profile.setPosition(request.position());
        profile.setWorkArea(request.workArea());
        profile.setHourlyRate(request.hourlyRate());
        profile.setEmploymentStatus(EmploymentStatus.ACTIVE);
        profile.setLegacyEmployeeId(savedEmployee.getEmployeeId());
        EmployeeProfile savedProfile = employeeProfileRepository.save(profile);

        String email = null;
        String inviteUrl = null;
        java.time.LocalDateTime inviteExpiresAt = null;

        if (request.createPortalAccess()) {
            UserAccount actorAccount = userAccountRepository.findById(actor.id())
                    .orElseThrow(() -> new NotFoundException("manager account not found"));

            UserAccount account = new UserAccount();
            account.setEmail(request.email());
            account.setRole(UserRole.EMPLOYEE);
            account.setStatus(AccountStatus.INVITED);
            account.setEmployeeProfile(savedProfile);
            UserAccount savedAccount = userAccountRepository.save(account);

            EmployeeInviteService.ProvisionedInvite invite = employeeInviteService.issueInvite(savedAccount, actorAccount);
            email = savedAccount.getEmail();
            inviteUrl = invite.inviteUrl();
            inviteExpiresAt = invite.expiresAt();
        }

        auditService.record(
                userAccountRepository.findById(actor.id()).orElse(null),
                "employee.provisioned",
                "employee_profile",
                savedProfile.getId(),
                AuditResult.SUCCESS,
                java.util.Map.of(
                        "employeeId", savedEmployee.getEmployeeId(),
                        "portalAccessCreated", request.createPortalAccess()
                )
        );

        return new ProvisionedEmployeeDto(
                savedEmployee.getEmployeeId(),
                savedProfile.getId(),
                savedProfile.getEmployeeNumber(),
                savedProfile.getFullName(),
                email,
                request.createPortalAccess(),
                inviteUrl,
                inviteExpiresAt
        );
    }
}
