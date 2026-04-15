package com.payroll.service;

import com.payroll.model.Employee;
import com.payroll.model.PayrollRecord;
import com.payroll.repository.PayrollRecordRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayrollService {
    private final EmployeeService employeeService;
    private final PayrollRecordRepository payrollRecordRepository;

    public PayrollService(EmployeeService employeeService, PayrollRecordRepository payrollRecordRepository) {
        this.employeeService = employeeService;
        this.payrollRecordRepository = payrollRecordRepository;
    }

    public List<Map<String, Object>> getPayrollReport(String campus) {
        return employeeService.getEmployees(campus).stream().map(this::toRow).toList();
    }

    public Map<String, BigDecimal> reportTotals(String campus) {
        BigDecimal totalSalary = BigDecimal.ZERO;
        BigDecimal totalBonus = BigDecimal.ZERO;
        BigDecimal totalDeductions = BigDecimal.ZERO;
        BigDecimal totalNetPay = BigDecimal.ZERO;

        for (Employee e : employeeService.getEmployees(campus)) {
            BigDecimal netPay = calculateNetPay(e.getSalary(), e.getBonus(), e.getDeductions());
            totalSalary = totalSalary.add(z(e.getSalary()));
            totalBonus = totalBonus.add(z(e.getBonus()));
            totalDeductions = totalDeductions.add(z(e.getDeductions()));
            totalNetPay = totalNetPay.add(netPay);
        }

        Map<String, BigDecimal> totals = new HashMap<>();
        totals.put("totalSalary", totalSalary.setScale(2, RoundingMode.HALF_UP));
        totals.put("totalBonus", totalBonus.setScale(2, RoundingMode.HALF_UP));
        totals.put("totalDeductions", totalDeductions.setScale(2, RoundingMode.HALF_UP));
        totals.put("totalNetPay", totalNetPay.setScale(2, RoundingMode.HALF_UP));
        return totals;
    }

    public Map<String, Object> dashboardSummary() {
        List<Employee> employees = employeeService.getEmployees(null);
        BigDecimal totalPayroll = BigDecimal.ZERO;

        for (Employee e : employees) {
            totalPayroll = totalPayroll.add(calculateNetPay(e.getSalary(), e.getBonus(), e.getDeductions()));
        }

        BigDecimal avgNetPay = employees.isEmpty()
                ? BigDecimal.ZERO
                : totalPayroll.divide(BigDecimal.valueOf(employees.size()), 2, RoundingMode.HALF_UP);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalEmployees", employees.size());
        summary.put("totalPayroll", totalPayroll.setScale(2, RoundingMode.HALF_UP));
        summary.put("avgNetPay", avgNetPay);
        return summary;
    }

    public int saveSnapshot(String campus) {
        List<Employee> employees = employeeService.getEmployees(campus);
        int saved = 0;
        for (Employee employee : employees) {
            if (employee.getPayPeriod() != null && !employee.getPayPeriod().isBlank()
                    && payrollRecordRepository.existsByEmployeeEmployeeIdAndPayPeriod(employee.getEmployeeId(), employee.getPayPeriod())) {
                continue;
            }
            PayrollRecord record = new PayrollRecord();
            record.setEmployee(employee);
            record.setSalary(z(employee.getSalary()));
            record.setBonus(z(employee.getBonus()));
            record.setDeductions(z(employee.getDeductions()));
            record.setNetPay(calculateNetPay(employee.getSalary(), employee.getBonus(), employee.getDeductions()));
            record.setPayPeriod(employee.getPayPeriod());
            payrollRecordRepository.save(record);
            saved++;
        }
        return saved;
    }

    public BigDecimal calculateNetPay(BigDecimal salary, BigDecimal bonus, BigDecimal deductions) {
        return z(salary).add(z(bonus)).subtract(z(deductions)).setScale(2, RoundingMode.HALF_UP);
    }

    private Map<String, Object> toRow(Employee e) {
        Map<String, Object> row = new HashMap<>();
        row.put("employeeId", e.getEmployeeId());
        row.put("name", e.getName());
        row.put("campus", e.getCampus());
        row.put("position", e.getPosition());
        row.put("workArea", e.getWorkArea());
        row.put("hoursWorked", z(e.getHoursWorked()).setScale(2, RoundingMode.HALF_UP));
        row.put("salary", z(e.getSalary()).setScale(2, RoundingMode.HALF_UP));
        row.put("bonus", z(e.getBonus()).setScale(2, RoundingMode.HALF_UP));
        row.put("deductions", z(e.getDeductions()).setScale(2, RoundingMode.HALF_UP));
        row.put("netPay", calculateNetPay(e.getSalary(), e.getBonus(), e.getDeductions()));
        row.put("payPeriod", e.getPayPeriod());
        return row;
    }

    private BigDecimal z(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
