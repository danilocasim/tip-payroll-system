package com.payroll.employee.service;

import com.payroll.common.exception.ForbiddenException;
import com.payroll.common.exception.NotFoundException;
import com.payroll.common.security.SessionUser;
import com.payroll.employee.dto.EmployeeDashboardSummaryDto;
import com.payroll.employee.dto.EmployeePayrollRecordDetailDto;
import com.payroll.employee.dto.EmployeePayrollRecordSummaryDto;
import com.payroll.employee.dto.EmployeePayrollRecordsPageDto;
import com.payroll.employee.dto.EmployeeProfileDto;
import com.payroll.identity.model.EmployeeProfile;
import com.payroll.identity.repository.EmployeeProfileRepository;
import com.payroll.model.Employee;
import com.payroll.model.PayrollRecord;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.PayrollRecordRepository;
import com.payroll.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmployeePortalService {
    private final EmployeeProfileRepository employeeProfileRepository;
    private final EmployeeRepository employeeRepository;
    private final PayrollRecordRepository payrollRecordRepository;
    private final EmployeeService employeeService;

    public EmployeePortalService(
            EmployeeProfileRepository employeeProfileRepository,
            EmployeeRepository employeeRepository,
            PayrollRecordRepository payrollRecordRepository,
            EmployeeService employeeService
    ) {
        this.employeeProfileRepository = employeeProfileRepository;
        this.employeeRepository = employeeRepository;
        this.payrollRecordRepository = payrollRecordRepository;
        this.employeeService = employeeService;
    }

    public EmployeeProfileDto getProfile(SessionUser sessionUser) {
        EmployeeProfile profile = requireProfile(sessionUser);
        return new EmployeeProfileDto(
                profile.getId(),
                profile.getEmployeeNumber(),
                profile.getFullName(),
                profile.getCampus(),
                profile.getPosition(),
                profile.getWorkArea(),
                profile.getHourlyRate(),
                profile.getEmploymentStatus().name()
        );
    }

    public EmployeeDashboardSummaryDto getDashboard(SessionUser sessionUser) {
        EmployeeProfile profile = requireProfile(sessionUser);
        if (profile.getLegacyEmployeeId() == null) {
            return new EmployeeDashboardSummaryDto(profile.getEmployeeNumber(), profile.getFullName(), null, BigDecimal.ZERO, 0);
        }

        Employee employee = employeeRepository.findById(profile.getLegacyEmployeeId()).orElse(null);
        if (employee == null) {
            return new EmployeeDashboardSummaryDto(profile.getEmployeeNumber(), profile.getFullName(), null, BigDecimal.ZERO, 0);
        }

        BigDecimal latestNetPay = employeeService.computeNetPay(employee.getSalary(), employee.getBonus(), employee.getDeductions());
        int payrollRecordCount = payrollRecordRepository.findByEmployeeEmployeeIdOrderByGeneratedAtDesc(employee.getEmployeeId()).size();

        return new EmployeeDashboardSummaryDto(
                profile.getEmployeeNumber(),
                profile.getFullName(),
                employee.getPayPeriod(),
                latestNetPay,
                payrollRecordCount
        );
    }

    public EmployeePayrollRecordsPageDto getPayrollRecords(SessionUser sessionUser, int page, int perPage) {
        EmployeeProfile profile = requireProfile(sessionUser);
        int resolvedPage = Math.max(page, 1);
        int resolvedPerPage = Math.min(Math.max(perPage, 1), 100);

        if (profile.getLegacyEmployeeId() == null) {
            return new EmployeePayrollRecordsPageDto(List.of(), resolvedPage, resolvedPerPage, 0);
        }

        List<PayrollRecord> records = payrollRecordRepository.findByEmployeeEmployeeIdOrderByGeneratedAtDesc(profile.getLegacyEmployeeId());
        int fromIndex = Math.min((resolvedPage - 1) * resolvedPerPage, records.size());
        int toIndex = Math.min(fromIndex + resolvedPerPage, records.size());
        List<EmployeePayrollRecordSummaryDto> items = records.subList(fromIndex, toIndex).stream()
                .map(this::toSummary)
                .toList();

        return new EmployeePayrollRecordsPageDto(items, resolvedPage, resolvedPerPage, records.size());
    }

    public EmployeePayrollRecordDetailDto getPayrollRecord(SessionUser sessionUser, Integer recordId) {
        EmployeeProfile profile = requireProfile(sessionUser);
        if (profile.getLegacyEmployeeId() == null) {
            throw new NotFoundException("payroll record not found");
        }

        PayrollRecord record = payrollRecordRepository.findByRecordIdAndEmployeeEmployeeId(recordId, profile.getLegacyEmployeeId())
                .orElseThrow(() -> new NotFoundException("payroll record not found"));

        return new EmployeePayrollRecordDetailDto(
                record.getRecordId(),
                record.getEmployee() == null ? profile.getFullName() : record.getEmployee().getName(),
                record.getSalary(),
                record.getBonus(),
                record.getDeductions(),
                record.getNetPay(),
                record.getPayPeriod(),
                record.getGeneratedAt()
        );
    }

    private EmployeeProfile requireProfile(SessionUser sessionUser) {
        if (sessionUser.employeeProfileId() == null || sessionUser.employeeProfileId().isBlank()) {
            throw new ForbiddenException("employee account is not linked to a profile");
        }
        return employeeProfileRepository.findById(sessionUser.employeeProfileId())
                .orElseThrow(() -> new NotFoundException("employee profile not found"));
    }

    private EmployeePayrollRecordSummaryDto toSummary(PayrollRecord record) {
        return new EmployeePayrollRecordSummaryDto(
                record.getRecordId(),
                record.getSalary(),
                record.getBonus(),
                record.getDeductions(),
                record.getNetPay(),
                record.getPayPeriod(),
                record.getGeneratedAt()
        );
    }
}
