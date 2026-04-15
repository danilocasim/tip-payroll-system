package com.payroll.admin.dto;

import java.math.BigDecimal;

public record AdminDashboardSummaryDto(
        int totalEmployees,
        BigDecimal totalPayroll,
        BigDecimal avgNetPay
) {
}
