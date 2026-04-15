package com.payroll.employee.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EmployeePayrollRecordSummaryDto(
        Integer recordId,
        BigDecimal salary,
        BigDecimal bonus,
        BigDecimal deductions,
        BigDecimal netPay,
        String payPeriod,
        LocalDateTime generatedAt
) {
}
