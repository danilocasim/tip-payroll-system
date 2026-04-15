package com.payroll;

import com.payroll.model.Employee;
import com.payroll.repository.EmployeeRepository;
import com.payroll.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setEmployeeId(1);
        employee.setName("Juan Dela Cruz");
        employee.setCampus("Casal");
        employee.setHourlyRate(new BigDecimal("150.00"));
        employee.setHoursWorked(new BigDecimal("160"));
        employee.setBonus(new BigDecimal("2000.00"));
        employee.setDeductions(new BigDecimal("1500.00"));
    }

    @Test
    void testAddEmployee_success() {
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));
        Employee saved = employeeService.addEmployee(employee);
        assertEquals(new BigDecimal("24000.00"), saved.getSalary());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void testAddEmployee_missingName_throwsException() {
        employee.setName(" ");
        assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(employee));
    }

    @Test
    void testUpdateEmployee_success() {
        Employee updated = new Employee();
        updated.setName("Maria Santos");
        updated.setCampus("Arlegui");
        updated.setHourlyRate(new BigDecimal("120.00"));
        updated.setHoursWorked(new BigDecimal("170"));
        updated.setBonus(new BigDecimal("500.00"));
        updated.setDeductions(new BigDecimal("250.00"));

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArgument(0));

        Employee result = employeeService.updateEmployee(1, updated);
        assertEquals("Maria Santos", result.getName());
        assertEquals(new BigDecimal("20400.00"), result.getSalary());
    }

    @Test
    void testDeleteEmployee_success() {
        employeeService.deleteEmployee(1);
        verify(employeeRepository).deleteById(1);
    }

    @Test
    void testGetAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        List<Employee> result = employeeService.getEmployees(null);
        assertEquals(1, result.size());
    }

    @Test
    void testGetEmployeesByCampus_casal() {
        when(employeeRepository.findByCampusIgnoreCaseOrderByNameAsc("Casal")).thenReturn(List.of(employee));
        List<Employee> result = employeeService.getEmployees("Casal");
        assertEquals("Casal", result.get(0).getCampus());
    }

    @Test
    void testGetEmployeesByCampus_arlegui() {
        Employee arlegui = new Employee();
        arlegui.setName("Ana Reyes");
        arlegui.setCampus("Arlegui");
        when(employeeRepository.findByCampusIgnoreCaseOrderByNameAsc("Arlegui")).thenReturn(List.of(arlegui));
        List<Employee> result = employeeService.getEmployees("Arlegui");
        assertEquals("Arlegui", result.get(0).getCampus());
    }
}
