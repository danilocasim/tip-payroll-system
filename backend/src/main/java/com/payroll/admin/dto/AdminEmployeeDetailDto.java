package com.payroll.admin.dto;

import java.math.BigDecimal;

public record AdminEmployeeDetailDto(
        Integer employeeId,
        String name,
        String firstName,
        String lastName,
        String campus,
        String position,
        String workArea,
        BigDecimal hourlyRate,
        BigDecimal hoursWorked,
        BigDecimal salary,
        BigDecimal bonus,
        BigDecimal deductions,
        BigDecimal netPay,
        String payPeriod
) {
}
