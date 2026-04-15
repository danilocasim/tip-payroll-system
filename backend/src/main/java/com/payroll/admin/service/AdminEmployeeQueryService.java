package com.payroll.admin.service;

import com.payroll.admin.dto.AdminEmployeeDetailDto;
import com.payroll.admin.dto.AdminUpdateEmployeeRequest;
import com.payroll.admin.dto.AdminEmployeeListItemDto;
import com.payroll.admin.dto.AdminEmployeesPageDto;
import com.payroll.common.config.CampusCatalog;
import com.payroll.model.Employee;
import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
public class AdminEmployeeQueryService {
    private final EmployeeService employeeService;
    private final PayrollService payrollService;

    public AdminEmployeeQueryService(EmployeeService employeeService, PayrollService payrollService) {
        this.employeeService = employeeService;
        this.payrollService = payrollService;
    }

    public AdminEmployeesPageDto listEmployees(String campus, String search, int page, int perPage) {
        int resolvedPage = Math.max(page, 1);
        int resolvedPerPage = Math.min(Math.max(perPage, 1), 100);
        List<Employee> filtered = employeeService.getEmployees(campus).stream()
                .filter(employee -> matchesSearch(employee, search))
                .toList();

        int fromIndex = Math.min((resolvedPage - 1) * resolvedPerPage, filtered.size());
        int toIndex = Math.min(fromIndex + resolvedPerPage, filtered.size());

        List<AdminEmployeeListItemDto> items = filtered.subList(fromIndex, toIndex).stream()
                .map(this::toItem)
                .toList();

        return new AdminEmployeesPageDto(items, resolvedPage, resolvedPerPage, filtered.size());
    }

    public AdminEmployeeDetailDto getEmployee(Integer employeeId) {
        Employee employee = employeeService.getEmployee(employeeId);
        return toDetail(employee);
    }

    public AdminEmployeeDetailDto updateEmployee(Integer employeeId, AdminUpdateEmployeeRequest request) {
        Employee employee = new Employee();
        employee.setName(request.firstName().trim() + " " + request.lastName().trim());
        employee.setCampus(CampusCatalog.normalize(request.campus()));
        employee.setPosition(request.position());
        employee.setWorkArea(request.workArea());
        employee.setHourlyRate(request.hourlyRate());
        employee.setHoursWorked(request.hoursWorked());
        employee.setBonus(request.bonus());
        employee.setDeductions(request.deductions());
        employee.setPayPeriod(request.payPeriod());

        return toDetail(employeeService.updateEmployee(employeeId, employee));
    }

    private boolean matchesSearch(Employee employee, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        String needle = search.toLowerCase(Locale.ROOT).trim();
        return contains(employee.getName(), needle)
                || contains(employee.getCampus(), needle)
                || contains(employee.getPosition(), needle)
                || contains(employee.getWorkArea(), needle);
    }

    private boolean contains(String value, String needle) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(needle);
    }

    private AdminEmployeeListItemDto toItem(Employee employee) {
        BigDecimal netPay = payrollService.calculateNetPay(employee.getSalary(), employee.getBonus(), employee.getDeductions());
        return new AdminEmployeeListItemDto(
                employee.getEmployeeId(),
                employee.getName(),
                employee.getCampus(),
                employee.getPosition(),
                employee.getWorkArea(),
                employee.getHourlyRate(),
                employee.getHoursWorked(),
                employee.getSalary(),
                employee.getBonus(),
                employee.getDeductions(),
                netPay,
                employee.getPayPeriod()
        );
    }

    private AdminEmployeeDetailDto toDetail(Employee employee) {
        BigDecimal netPay = payrollService.calculateNetPay(employee.getSalary(), employee.getBonus(), employee.getDeductions());
        String[] nameParts = splitName(employee.getName());
        return new AdminEmployeeDetailDto(
                employee.getEmployeeId(),
                employee.getName(),
                nameParts[0],
                nameParts[1],
                employee.getCampus(),
                employee.getPosition(),
                employee.getWorkArea(),
                employee.getHourlyRate(),
                employee.getHoursWorked(),
                employee.getSalary(),
                employee.getBonus(),
                employee.getDeductions(),
                netPay,
                employee.getPayPeriod()
        );
    }

    private String[] splitName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new String[]{"", ""};
        }
        String trimmed = fullName.trim();
        int index = trimmed.indexOf(' ');
        if (index < 0) {
            return new String[]{trimmed, ""};
        }
        return new String[]{trimmed.substring(0, index), trimmed.substring(index + 1).trim()};
    }
}
