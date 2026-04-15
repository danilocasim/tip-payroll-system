package com.payroll.admin.dto;

import java.time.LocalDateTime;

public record ProvisionedEmployeeDto(
        Integer employeeId,
        String employeeProfileId,
        String employeeNumber,
        String fullName,
        String email,
        boolean portalAccessCreated,
        String inviteUrl,
        LocalDateTime inviteExpiresAt
) {
}
