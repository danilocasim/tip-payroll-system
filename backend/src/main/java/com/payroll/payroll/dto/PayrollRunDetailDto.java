package com.payroll.payroll.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PayrollRunDetailDto(
        String id,
        LocalDate payPeriodStart,
        LocalDate payPeriodEnd,
        String campusScope,
        String status,
        int employeeCount,
        BigDecimal totalSalary,
        BigDecimal totalBonus,
        BigDecimal totalDeductions,
        BigDecimal totalNetPay,
        LocalDateTime createdAt,
        LocalDateTime finalizedAt,
        List<PayrollRunRecordDto> records
) {
}
