package com.payroll.employee.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EmployeePayrollRecordDetailDto(
        Integer recordId,
        String employeeName,
        BigDecimal salary,
        BigDecimal bonus,
        BigDecimal deductions,
        BigDecimal netPay,
        String payPeriod,
        LocalDateTime generatedAt
) {
}
