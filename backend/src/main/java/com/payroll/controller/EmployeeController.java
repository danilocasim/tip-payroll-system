package com.payroll.controller;

import com.payroll.model.Employee;
import com.payroll.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> all(@RequestParam(required = false) String campus) {
        return employeeService.getEmployees(campus);
    }

    @GetMapping("/{id}")
    public Employee one(@PathVariable Integer id) {
        return employeeService.getEmployee(id);
    }

    @PostMapping
    public Employee create(@RequestBody Employee employee) {
        return employeeService.addEmployee(employee);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Integer id, @RequestBody Employee employee) {
        return employeeService.updateEmployee(id, employee);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);
    }
}
