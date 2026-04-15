package com.payroll.auth.dto;

import java.util.List;

public record AuthMeData(
        AuthUserDto user,
        List<String> portalAccess
) {
}
