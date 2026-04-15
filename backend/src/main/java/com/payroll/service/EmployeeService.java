package com.payroll.service;

import com.payroll.model.Employee;
import com.payroll.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee addEmployee(Employee employee) {
        validateEmployee(employee);
        recompute(employee);
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Integer id, Employee updated) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        validateEmployee(updated);

        existing.setName(updated.getName());
        existing.setPosition(updated.getPosition());
        existing.setWorkArea(updated.getWorkArea());
        existing.setCampus(updated.getCampus());
        existing.setHourlyRate(defaultZero(updated.getHourlyRate()));
        existing.setHoursWorked(defaultZero(updated.getHoursWorked()));
        existing.setBonus(defaultZero(updated.getBonus()));
        existing.setDeductions(defaultZero(updated.getDeductions()));
        existing.setPayPeriod(updated.getPayPeriod());
        recompute(existing);

        return employeeRepository.save(existing);
    }

    public void deleteEmployee(Integer id) {
        employeeRepository.deleteById(id);
    }

    public Employee getEmployee(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
    }

    public List<Employee> getEmployees(String campus) {
        if (campus == null || campus.isBlank()) {
            return employeeRepository.findAll().stream().sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName())).toList();
        }
        return employeeRepository.findByCampusIgnoreCaseOrderByNameAsc(campus);
    }

    public BigDecimal computeSalary(BigDecimal hourlyRate, BigDecimal hoursWorked) {
        return defaultZero(hourlyRate).multiply(defaultZero(hoursWorked)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal computeNetPay(BigDecimal salary, BigDecimal bonus, BigDecimal deductions) {
        return defaultZero(salary).add(defaultZero(bonus)).subtract(defaultZero(deductions)).setScale(2, RoundingMode.HALF_UP);
    }

    private void validateEmployee(Employee employee) {
        if (employee.getName() == null || employee.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
    }

    private void recompute(Employee employee) {
        BigDecimal salary = computeSalary(employee.getHourlyRate(), employee.getHoursWorked());
        employee.setHourlyRate(defaultZero(employee.getHourlyRate()));
        employee.setHoursWorked(defaultZero(employee.getHoursWorked()));
        employee.setBonus(defaultZero(employee.getBonus()));
        employee.setDeductions(defaultZero(employee.getDeductions()));
        employee.setSalary(salary);
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
