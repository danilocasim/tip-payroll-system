package com.payroll.employee.dto;

import java.math.BigDecimal;

public record EmployeeProfileDto(
        String id,
        String employeeNumber,
        String fullName,
        String campus,
        String position,
        String workArea,
        BigDecimal hourlyRate,
        String employmentStatus
) {
}
