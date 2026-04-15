package com.payroll.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record AdminCreateEmployeeRequest(
        @NotBlank(message = "employeeNumber is required")
        String employeeNumber,
        @NotBlank(message = "firstName is required")
        String firstName,
        @NotBlank(message = "lastName is required")
        String lastName,
        @NotBlank(message = "campus is required")
        String campus,
        String position,
        String workArea,
        BigDecimal hourlyRate,
        BigDecimal hoursWorked,
        BigDecimal bonus,
        BigDecimal deductions,
        String payPeriod,
        boolean createPortalAccess,
        @Email(message = "email must be valid")
        String email
) {
}
