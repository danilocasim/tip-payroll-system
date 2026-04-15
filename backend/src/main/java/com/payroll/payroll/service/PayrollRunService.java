package com.payroll.payroll.service;

import com.payroll.audit.model.AuditResult;
import com.payroll.audit.service.AuditService;
import com.payroll.common.config.CampusCatalog;
import com.payroll.common.exception.ConflictException;
import com.payroll.common.exception.NotFoundException;
import com.payroll.common.security.SessionUser;
import com.payroll.identity.model.EmployeeProfile;
import com.payroll.identity.model.UserAccount;
import com.payroll.identity.repository.EmployeeProfileRepository;
import com.payroll.identity.repository.UserAccountRepository;
import com.payroll.model.Employee;
import com.payroll.model.PayrollRecord;
import com.payroll.payroll.dto.CreatePayrollRunRequest;
import com.payroll.payroll.dto.PayrollRunDetailDto;
import com.payroll.payroll.dto.PayrollRunListItemDto;
import com.payroll.payroll.dto.PayrollRunRecordDto;
import com.payroll.payroll.model.PayrollRun;
import com.payroll.payroll.model.PayrollRunStatus;
import com.payroll.payroll.repository.PayrollRunRepository;
import com.payroll.repository.PayrollRecordRepository;
import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PayrollRunService {
    private final PayrollRunRepository payrollRunRepository;
    private final PayrollRecordRepository payrollRecordRepository;
    private final EmployeeService employeeService;
    private final PayrollService payrollService;
    private final UserAccountRepository userAccountRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final AuditService auditService;

    public PayrollRunService(
            PayrollRunRepository payrollRunRepository,
            PayrollRecordRepository payrollRecordRepository,
            EmployeeService employeeService,
            PayrollService payrollService,
            UserAccountRepository userAccountRepository,
            EmployeeProfileRepository employeeProfileRepository,
            AuditService auditService
    ) {
        this.payrollRunRepository = payrollRunRepository;
        this.payrollRecordRepository = payrollRecordRepository;
        this.employeeService = employeeService;
        this.payrollService = payrollService;
        this.userAccountRepository = userAccountRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<PayrollRunListItemDto> list() {
        return payrollRunRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toListItem)
                .toList();
    }

    @Transactional
    public PayrollRunDetailDto create(CreatePayrollRunRequest request, SessionUser actor) {
        if (request.payPeriodEnd().isBefore(request.payPeriodStart())) {
            throw new IllegalArgumentException("payPeriodEnd must be on or after payPeriodStart");
        }

        String normalizedCampus = normalizeCampusScope(request.campusScope());
        ensureNoDuplicateRun(request, normalizedCampus);

        UserAccount createdBy = userAccountRepository.findById(actor.id())
                .orElseThrow(() -> new NotFoundException("manager account not found"));

        PayrollRun run = new PayrollRun();
        run.setPayPeriodStart(request.payPeriodStart());
        run.setPayPeriodEnd(request.payPeriodEnd());
        run.setCampusScope(normalizedCampus);
        run.setStatus(PayrollRunStatus.DRAFT);
        run.setCreatedByUser(createdBy);
        PayrollRun savedRun = payrollRunRepository.save(run);

        String payPeriodLabel = formatPayPeriod(request.payPeriodStart(), request.payPeriodEnd());

        for (Employee employee : employeeService.getEmployees(normalizedCampus)) {
            if (payrollRecordRepository.existsByPayrollRunIdAndEmployeeEmployeeId(savedRun.getId(), employee.getEmployeeId())) {
                continue;
            }

            PayrollRecord record = new PayrollRecord();
            record.setEmployee(employee);
            record.setPayrollRunId(savedRun.getId());
            record.setEmployeeProfileId(resolveEmployeeProfileId(employee.getEmployeeId()));
            record.setSalary(z(employee.getSalary()));
            record.setBonus(z(employee.getBonus()));
            record.setDeductions(z(employee.getDeductions()));
            record.setNetPay(payrollService.calculateNetPay(employee.getSalary(), employee.getBonus(), employee.getDeductions()));
            record.setPayPeriod(payPeriodLabel);
            record.setPayPeriodStart(request.payPeriodStart());
            record.setPayPeriodEnd(request.payPeriodEnd());
            payrollRecordRepository.save(record);
        }

        auditService.record(createdBy, "payroll.run.created", "payroll_run", savedRun.getId(), AuditResult.SUCCESS,
                Map.of("campusScope", normalizedCampus == null ? "ALL" : normalizedCampus));

        return detail(savedRun.getId());
    }

    @Transactional(readOnly = true)
    public PayrollRunDetailDto detail(String payrollRunId) {
        PayrollRun run = payrollRunRepository.findById(payrollRunId)
                .orElseThrow(() -> new NotFoundException("payroll run not found"));
        List<PayrollRunRecordDto> records = payrollRecordRepository.findByPayrollRunIdOrderByGeneratedAtDesc(payrollRunId).stream()
                .map(this::toRecord)
                .toList();

        BigDecimal totalSalary = BigDecimal.ZERO;
        BigDecimal totalBonus = BigDecimal.ZERO;
        BigDecimal totalDeductions = BigDecimal.ZERO;
        BigDecimal totalNetPay = BigDecimal.ZERO;
        for (PayrollRunRecordDto record : records) {
            totalSalary = totalSalary.add(z(record.salary()));
            totalBonus = totalBonus.add(z(record.bonus()));
            totalDeductions = totalDeductions.add(z(record.deductions()));
            totalNetPay = totalNetPay.add(z(record.netPay()));
        }

        return new PayrollRunDetailDto(
                run.getId(),
                run.getPayPeriodStart(),
                run.getPayPeriodEnd(),
                run.getCampusScope(),
                run.getStatus().name(),
                records.size(),
                totalSalary.setScale(2, RoundingMode.HALF_UP),
                totalBonus.setScale(2, RoundingMode.HALF_UP),
                totalDeductions.setScale(2, RoundingMode.HALF_UP),
                totalNetPay.setScale(2, RoundingMode.HALF_UP),
                run.getCreatedAt(),
                run.getFinalizedAt(),
                records
        );
    }

    @Transactional
    public PayrollRunDetailDto finalizeRun(String payrollRunId, SessionUser actor) {
        PayrollRun run = payrollRunRepository.findById(payrollRunId)
                .orElseThrow(() -> new NotFoundException("payroll run not found"));

        if (run.getStatus() == PayrollRunStatus.FINALIZED) {
            throw new ConflictException("payroll run is already finalized");
        }

        run.setStatus(PayrollRunStatus.FINALIZED);
        run.setFinalizedAt(LocalDateTime.now());
        payrollRunRepository.save(run);

        UserAccount actorAccount = userAccountRepository.findById(actor.id()).orElse(null);
        auditService.record(actorAccount, "payroll.run.finalized", "payroll_run", payrollRunId, AuditResult.SUCCESS, Map.of());
        return detail(payrollRunId);
    }

    private void ensureNoDuplicateRun(CreatePayrollRunRequest request, String normalizedCampus) {
        boolean duplicateExists = payrollRunRepository.findByPayPeriodStartAndPayPeriodEnd(request.payPeriodStart(), request.payPeriodEnd()).stream()
                .anyMatch(existing -> normalizeCampusScope(existing.getCampusScope()) == null
                        ? normalizedCampus == null
                        : normalizeCampusScope(existing.getCampusScope()).equals(normalizedCampus));
        if (duplicateExists) {
            throw new ConflictException("payroll run already exists for this pay period and campus scope");
        }
    }

    private String resolveEmployeeProfileId(Integer employeeId) {
        return employeeProfileRepository.findByLegacyEmployeeId(employeeId)
                .map(EmployeeProfile::getId)
                .orElse(null);
    }

    private PayrollRunListItemDto toListItem(PayrollRun run) {
        PayrollRunDetailDto detail = detail(run.getId());
        return new PayrollRunListItemDto(
                run.getId(),
                run.getPayPeriodStart(),
                run.getPayPeriodEnd(),
                run.getCampusScope(),
                run.getStatus().name(),
                detail.employeeCount(),
                detail.totalNetPay(),
                run.getCreatedAt(),
                run.getFinalizedAt()
        );
    }

    private PayrollRunRecordDto toRecord(PayrollRecord record) {
        Employee employee = record.getEmployee();
        return new PayrollRunRecordDto(
                record.getRecordId(),
                employee == null ? null : employee.getEmployeeId(),
                employee == null ? "Unknown employee" : employee.getName(),
                employee == null ? null : employee.getCampus(),
                employee == null ? null : employee.getPosition(),
                record.getSalary(),
                record.getBonus(),
                record.getDeductions(),
                record.getNetPay(),
                record.getPayPeriod(),
                record.getGeneratedAt()
        );
    }

    private String normalizeCampusScope(String campusScope) {
        if (campusScope == null || campusScope.isBlank()) {
            return null;
        }
        return CampusCatalog.normalize(campusScope);
    }

    private String formatPayPeriod(LocalDate payPeriodStart, LocalDate payPeriodEnd) {
        return payPeriodStart.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE)
                + "-"
                + payPeriodEnd.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
    }

    private BigDecimal z(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
