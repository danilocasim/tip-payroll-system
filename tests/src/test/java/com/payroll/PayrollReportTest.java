package com.payroll;

import com.payroll.model.Employee;
import com.payroll.repository.PayrollRecordRepository;
import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollReportTest {

    @Mock
    private EmployeeService employeeService;
    @Mock
    private PayrollRecordRepository payrollRecordRepository;

    private PayrollService payrollService;

    @BeforeEach
    void setUp() {
        payrollService = new PayrollService(employeeService, payrollRecordRepository);
    }

    @Test
    void testGenerateReport_allEmployees() {
        when(employeeService.getEmployees(null)).thenReturn(List.of(emp("A", "Casal"), emp("B", "Arlegui")));
        List<Map<String, Object>> rows = payrollService.getPayrollReport(null);
        assertEquals(2, rows.size());
    }

    @Test
    void testGenerateReport_filterByCasal() {
        when(employeeService.getEmployees("Casal")).thenReturn(List.of(emp("A", "Casal")));
        List<Map<String, Object>> rows = payrollService.getPayrollReport("Casal");
        assertEquals("Casal", rows.get(0).get("campus"));
    }

    @Test
    void testGenerateReport_filterByArlegui() {
        when(employeeService.getEmployees("Arlegui")).thenReturn(List.of(emp("B", "Arlegui")));
        List<Map<String, Object>> rows = payrollService.getPayrollReport("Arlegui");
        assertEquals("Arlegui", rows.get(0).get("campus"));
    }

    @Test
    void testReportTotalsAreCorrect() {
        when(employeeService.getEmployees(null)).thenReturn(List.of(emp("A", "Casal"), emp("B", "Arlegui")));
        Map<String, BigDecimal> totals = payrollService.reportTotals(null);
        assertEquals(new BigDecimal("2000.00"), totals.get("totalSalary"));
        assertEquals(new BigDecimal("200.00"), totals.get("totalBonus"));
        assertEquals(new BigDecimal("100.00"), totals.get("totalDeductions"));
        assertEquals(new BigDecimal("2100.00"), totals.get("totalNetPay"));
    }

    @Test
    void testSavePayrollSnapshot() {
        when(employeeService.getEmployees(null)).thenReturn(List.of(emp("A", "Casal"), emp("B", "Arlegui")));
        int saved = payrollService.saveSnapshot(null);
        assertEquals(2, saved);
        verify(payrollRecordRepository, times(2)).save(any());
    }

    @Test
    void testSavePayrollSnapshot_skipsExistingPayPeriod() {
        Employee existing = emp("A", "Casal");
        existing.setEmployeeId(1);
        existing.setPayPeriod("2026-04");

        when(employeeService.getEmployees(null)).thenReturn(List.of(existing));
        when(payrollRecordRepository.existsByEmployeeEmployeeIdAndPayPeriod(1, "2026-04")).thenReturn(true);

        int saved = payrollService.saveSnapshot(null);

        assertEquals(0, saved);
        verify(payrollRecordRepository, never()).save(any());
    }

    private Employee emp(String name, String campus) {
        Employee e = new Employee();
        e.setName(name);
        e.setCampus(campus);
        e.setSalary(new BigDecimal("1000.00"));
        e.setBonus(new BigDecimal("100.00"));
        e.setDeductions(new BigDecimal("50.00"));
        e.setHoursWorked(new BigDecimal("160"));
        return e;
    }
}
