package com.payroll.payroll.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreatePayrollRunRequest(
        @NotNull(message = "payPeriodStart is required")
        LocalDate payPeriodStart,
        @NotNull(message = "payPeriodEnd is required")
        LocalDate payPeriodEnd,
        String campusScope
) {
}
