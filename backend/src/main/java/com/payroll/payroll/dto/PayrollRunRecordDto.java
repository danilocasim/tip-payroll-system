package com.payroll.payroll.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PayrollRunRecordDto(
        Integer recordId,
        Integer employeeId,
        String employeeName,
        String campus,
        String position,
        BigDecimal salary,
        BigDecimal bonus,
        BigDecimal deductions,
        BigDecimal netPay,
        String payPeriod,
        LocalDateTime generatedAt
) {
}
