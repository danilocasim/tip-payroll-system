package com.payroll;

import com.payroll.audit.repository.AuditEventRepository;
import com.payroll.auth.model.EmployeeInvite;
import com.payroll.auth.repository.EmployeeInviteRepository;
import com.payroll.auth.service.PasswordService;
import com.payroll.common.security.SessionUser;
import com.payroll.identity.model.AccountStatus;
import com.payroll.identity.model.EmployeeProfile;
import com.payroll.identity.model.EmploymentStatus;
import com.payroll.identity.model.PasswordCredential;
import com.payroll.identity.model.UserAccount;
import com.payroll.identity.model.UserRole;
import com.payroll.identity.repository.EmployeeProfileRepository;
import com.payroll.identity.repository.PasswordCredentialRepository;
import com.payroll.identity.repository.UserAccountRepository;
import com.payroll.model.Employee;
import com.payroll.model.PayrollRecord;
import com.payroll.payroll.repository.PayrollRunRepository;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.PayrollRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordCredentialRepository passwordCredentialRepository;

    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PayrollRecordRepository payrollRecordRepository;

    @Autowired
    private AuditEventRepository auditEventRepository;

    @Autowired
    private EmployeeInviteRepository employeeInviteRepository;

    @Autowired
    private PayrollRunRepository payrollRunRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        payrollRecordRepository.deleteAll();
        payrollRunRepository.deleteAll();
        employeeInviteRepository.deleteAll();
        auditEventRepository.deleteAll();
        passwordCredentialRepository.deleteAll();
        userAccountRepository.deleteAll();
        employeeProfileRepository.deleteAll();
        employeeRepository.deleteAll();

        Employee employee = new Employee();
        employee.setName("Ana Reyes");
        employee.setCampus("Arlegui");
        employee.setPosition("Cashier");
        employee.setWorkArea("Counter");
        employee.setHourlyRate(new BigDecimal("150.00"));
        employee.setHoursWorked(new BigDecimal("160.00"));
        employee.setSalary(new BigDecimal("24000.00"));
        employee.setBonus(new BigDecimal("1000.00"));
        employee.setDeductions(new BigDecimal("500.00"));
        employee.setPayPeriod("2026-04");
        Employee savedEmployee = employeeRepository.save(employee);

        createUser("manager@tip.edu", UserRole.MANAGER, "managerpass123", null, null);

        EmployeeProfile profile = new EmployeeProfile();
        profile.setEmployeeNumber("EMP-1001");
        profile.setFirstName("Ana");
        profile.setLastName("Reyes");
        profile.setFullName("Ana Reyes");
        profile.setCampus("Arlegui");
        profile.setPosition("Cashier");
        profile.setWorkArea("Counter");
        profile.setHourlyRate(new BigDecimal("150.00"));
        profile.setEmploymentStatus(EmploymentStatus.ACTIVE);
        profile.setLegacyEmployeeId(savedEmployee.getEmployeeId());
        EmployeeProfile savedProfile = employeeProfileRepository.save(profile);

        createUser("employee@tip.edu", UserRole.EMPLOYEE, "employeepass123", savedProfile, AccountStatus.ACTIVE);

        PayrollRecord payrollRecord = new PayrollRecord();
        payrollRecord.setEmployee(savedEmployee);
        payrollRecord.setSalary(new BigDecimal("24000.00"));
        payrollRecord.setBonus(new BigDecimal("1000.00"));
        payrollRecord.setDeductions(new BigDecimal("500.00"));
        payrollRecord.setNetPay(new BigDecimal("24500.00"));
        payrollRecord.setPayPeriod("2026-04");
        payrollRecordRepository.save(payrollRecord);
    }

    @Test
    void adminLoginReturnsManagerIdentity() throws Exception {
        mockMvc.perform(post("/api/v1/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"email\": \"manager@tip.edu\",
                                  \"password\": \"managerpass123\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data.role").value("MANAGER"))
                .andExpect(jsonPath("$.data.email").value("manager@tip.edu"));

        SessionUser principal = new SessionUser("manager-id", "manager@tip.edu", UserRole.MANAGER, null);

        mockMvc.perform(get("/api/v1/admin/dashboard/summary")
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext(principal))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data.totalEmployees").value(1));
    }

    @Test
    void employeeCannotUseAdminLogin() throws Exception {
        mockMvc.perform(post("/api/v1/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"email\": \"employee@tip.edu\",
                                  \"password\": \"employeepass123\"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.ok").value(false))
                .andExpect(jsonPath("$.error.code").value("invalid_credentials"));
    }

    @Test
    void unauthenticatedAdminRouteReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/admin/employees"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.ok").value(false))
                .andExpect(jsonPath("$.error.code").value("unauthenticated"));
    }

    @Test
    void employeeSessionCanLoadOwnProfileButNotAdminRoute() throws Exception {
        mockMvc.perform(post("/api/v1/employee/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"email\": \"employee@tip.edu\",
                                  \"password\": \"employeepass123\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("EMPLOYEE"));

        EmployeeProfile profile = employeeProfileRepository.findByLegacyEmployeeId(
                employeeRepository.findAll().get(0).getEmployeeId()
        ).orElseThrow();
        SessionUser principal = new SessionUser("employee-id", "employee@tip.edu", UserRole.EMPLOYEE, profile.getId());

        mockMvc.perform(get("/api/v1/employee/profile")
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext(principal))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data.employeeNumber").value("EMP-1001"))
                .andExpect(jsonPath("$.data.fullName").value("Ana Reyes"));

        mockMvc.perform(get("/api/v1/admin/employees")
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext(principal))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.ok").value(false))
                .andExpect(jsonPath("$.error.code").value("forbidden"));
    }

    @Test
    void employeeCanLoadOwnPayrollRecordDetail() throws Exception {
        EmployeeProfile profile = employeeProfileRepository.findByLegacyEmployeeId(
                employeeRepository.findAll().get(0).getEmployeeId()
        ).orElseThrow();
        SessionUser principal = new SessionUser("employee-id", "employee@tip.edu", UserRole.EMPLOYEE, profile.getId());
        Integer recordId = payrollRecordRepository.findByEmployeeEmployeeIdOrderByGeneratedAtDesc(profile.getLegacyEmployeeId()).get(0).getRecordId();

        mockMvc.perform(get("/api/v1/employee/payroll-records/{recordId}", recordId)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext(principal))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data.recordId").value(recordId))
                .andExpect(jsonPath("$.data.employeeName").value("Ana Reyes"))
                .andExpect(jsonPath("$.data.netPay").value(24500.00));
    }

    @Test
    void managerCanProvisionEmployeeAndEmployeeCanActivateInvite() throws Exception {
        UserAccount manager = userAccountRepository.findByEmailIgnoreCase("manager@tip.edu").orElseThrow();
        SessionUser principal = new SessionUser(manager.getId(), manager.getEmail(), UserRole.MANAGER, null);

        MvcResult createResult = mockMvc.perform(post("/api/v1/admin/employees")
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext(principal)))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "employeeNumber": "EMP-2002",
                                  "firstName": "Mika",
                                  "lastName": "Santos",
                                  "campus": "Casal",
                                  "position": "Barista",
                                  "workArea": "Cafe",
                                  "hourlyRate": 120.00,
                                  "hoursWorked": 160.00,
                                  "bonus": 500.00,
                                  "deductions": 250.00,
                                  "payPeriod": "2026-05",
                                  "createPortalAccess": true,
                                  "email": "mika.santos@tip.edu"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data.portalAccessCreated").value(true))
                .andExpect(jsonPath("$.data.email").value("mika.santos@tip.edu"))
                .andReturn();

        JsonNode createPayload = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String inviteUrl = createPayload.get("data").get("inviteUrl").asText();
        String token = inviteUrl.substring(inviteUrl.lastIndexOf('/') + 1);

        UserAccount employeeAccount = userAccountRepository.findByEmailIgnoreCase("mika.santos@tip.edu").orElseThrow();
        EmployeeInvite invite = employeeInviteRepository.findByUserAccountId(employeeAccount.getId()).orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(AccountStatus.INVITED, employeeAccount.getStatus());
        org.junit.jupiter.api.Assertions.assertFalse(invite.isConsumed());

        mockMvc.perform(get("/api/v1/employee/auth/invite").param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data.email").value("mika.santos@tip.edu"));

        mockMvc.perform(post("/api/v1/employee/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "token": "%s",
                                  "password": "welcome123",
                                  "confirmPassword": "welcome123"
                                }
                                """.formatted(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data.activated").value(true));

        UserAccount activatedAccount = userAccountRepository.findByEmailIgnoreCase("mika.santos@tip.edu").orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(AccountStatus.ACTIVE, activatedAccount.getStatus());
        org.junit.jupiter.api.Assertions.assertTrue(passwordCredentialRepository.findByUserAccountId(activatedAccount.getId()).isPresent());

        mockMvc.perform(post("/api/v1/employee/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "mika.santos@tip.edu",
                                  "password": "welcome123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data.role").value("EMPLOYEE"));
    }

    @Test
    void managerCanCreateAndFinalizePayrollRun() throws Exception {
        UserAccount manager = userAccountRepository.findByEmailIgnoreCase("manager@tip.edu").orElseThrow();
        SessionUser principal = new SessionUser(manager.getId(), manager.getEmail(), UserRole.MANAGER, null);

        MvcResult createResult = mockMvc.perform(post("/api/v1/admin/payroll-runs")
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext(principal)))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "payPeriodStart": "2026-04-01",
                                  "payPeriodEnd": "2026-04-15",
                                  "campusScope": "Arlegui"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.employeeCount").value(1))
                .andReturn();

        JsonNode createPayload = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String payrollRunId = createPayload.get("data").get("id").asText();

        mockMvc.perform(get("/api/v1/admin/payroll-runs")
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext(principal))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data[0].id").value(payrollRunId));

        mockMvc.perform(get("/api/v1/admin/payroll-runs/{payrollRunId}", payrollRunId)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext(principal))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data.records[0].employeeName").value("Ana Reyes"));

        mockMvc.perform(post("/api/v1/admin/payroll-runs/{payrollRunId}/finalize", payrollRunId)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext(principal)))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data.status").value("FINALIZED"));
    }

    @Test
    void managerCanUpdateExistingEmployeePayrollBasis() throws Exception {
        UserAccount manager = userAccountRepository.findByEmailIgnoreCase("manager@tip.edu").orElseThrow();
        SessionUser principal = new SessionUser(manager.getId(), manager.getEmail(), UserRole.MANAGER, null);
        Integer employeeId = employeeRepository.findAll().get(0).getEmployeeId();

        mockMvc.perform(patch("/api/v1/admin/employees/{employeeId}", employeeId)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext(principal)))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Ana",
                                  "lastName": "Reyes",
                                  "campus": "Casal",
                                  "position": "Shift Lead",
                                  "workArea": "Kitchen",
                                  "hourlyRate": 175.00,
                                  "hoursWorked": 120.00,
                                  "bonus": 250.00,
                                  "deductions": 100.00,
                                  "payPeriod": "2026-04-16"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data.campus").value("Casal"))
                .andExpect(jsonPath("$.data.position").value("Shift Lead"))
                .andExpect(jsonPath("$.data.payPeriod").value("2026-04-16"))
                .andExpect(jsonPath("$.data.salary").value(21000.00))
                .andExpect(jsonPath("$.data.netPay").value(21150.00));
    }

    private void createUser(String email, UserRole role, String password, EmployeeProfile profile, AccountStatus status) {
        UserAccount account = new UserAccount();
        account.setEmail(email);
        account.setRole(role);
        account.setStatus(status == null ? AccountStatus.ACTIVE : status);
        account.setEmployeeProfile(profile);
        UserAccount savedAccount = userAccountRepository.save(account);

        PasswordCredential credential = new PasswordCredential();
        credential.setUserAccount(savedAccount);
        credential.setPasswordAlgorithm("argon2");
        credential.setPasswordHash(passwordService.hash(password));
        passwordCredentialRepository.save(credential);
    }

    private SecurityContext securityContext(SessionUser principal) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.authorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
