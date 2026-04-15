package com.payroll.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActivateInviteRequest(
        @NotBlank(message = "token is required")
        String token,
        @NotBlank(message = "password is required")
        @Size(min = 8, message = "password must be at least 8 characters")
        String password,
        @NotBlank(message = "confirmPassword is required")
        String confirmPassword
) {
}
