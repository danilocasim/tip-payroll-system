package com.payroll;

import com.payroll.service.EmployeeService;
import com.payroll.service.PayrollService;
import com.payroll.repository.PayrollRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

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
    void testSalaryComputation() {
        BigDecimal salary = new EmployeeService(null).computeSalary(new BigDecimal("150.00"), new BigDecimal("160"));
        assertEquals(new BigDecimal("24000.00"), salary);
    }

    @Test
    void testNetPayCalculation() {
        BigDecimal netPay = payrollService.calculateNetPay(new BigDecimal("24000.00"), new BigDecimal("2000.00"), new BigDecimal("1500.00"));
        assertEquals(new BigDecimal("24500.00"), netPay);
    }

    @Test
    void testNetPayWithZeroBonus() {
        BigDecimal netPay = payrollService.calculateNetPay(new BigDecimal("24000.00"), BigDecimal.ZERO, new BigDecimal("1500.00"));
        assertEquals(new BigDecimal("22500.00"), netPay);
    }

    @Test
    void testNetPayDeductionsExceedSalary() {
        BigDecimal netPay = payrollService.calculateNetPay(new BigDecimal("1000.00"), BigDecimal.ZERO, new BigDecimal("1500.00"));
        assertEquals(new BigDecimal("-500.00"), netPay);
    }
}
