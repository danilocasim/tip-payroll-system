package com.payroll.admin.dto;

import java.math.BigDecimal;

public record AdminEmployeeListItemDto(
        Integer employeeId,
        String name,
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
