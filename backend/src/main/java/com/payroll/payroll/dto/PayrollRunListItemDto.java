package com.payroll.payroll.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PayrollRunListItemDto(
        String id,
        LocalDate payPeriodStart,
        LocalDate payPeriodEnd,
        String campusScope,
        String status,
        int employeeCount,
        BigDecimal totalNetPay,
        LocalDateTime createdAt,
        LocalDateTime finalizedAt
) {
}
