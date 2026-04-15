package com.payroll.employee.dto;

import java.math.BigDecimal;

public record EmployeeDashboardSummaryDto(
        String employeeNumber,
        String fullName,
        String latestPayPeriod,
        BigDecimal latestNetPay,
        int payrollRecordCount
) {
}
