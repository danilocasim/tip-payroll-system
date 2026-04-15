package com.payroll.auth.dto;

import java.time.LocalDateTime;

public record EmployeeInviteViewDto(
        String employeeName,
        String email,
        LocalDateTime expiresAt
) {
}
