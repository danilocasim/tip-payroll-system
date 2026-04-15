package com.payroll.auth.dto;

public record AuthUserDto(
        String id,
        String email,
        String role,
        String employeeProfileId
) {
}
