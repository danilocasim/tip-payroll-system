package com.payroll.auth.controller;

import com.payroll.auth.dto.AuthUserDto;
import com.payroll.auth.dto.LoginRequest;
import com.payroll.auth.service.AuthenticationService;
import com.payroll.auth.service.SessionAuthenticationService;
import com.payroll.common.api.ApiResponse;
import com.payroll.common.api.ApiResponses;
import com.payroll.common.security.SessionUser;
import com.payroll.identity.model.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee/auth")
public class EmployeeAuthController {
    private final AuthenticationService authenticationService;
    private final SessionAuthenticationService sessionAuthenticationService;

    public EmployeeAuthController(AuthenticationService authenticationService, SessionAuthenticationService sessionAuthenticationService) {
        this.authenticationService = authenticationService;
        this.sessionAuthenticationService = sessionAuthenticationService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthUserDto> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        SessionUser user = authenticationService.authenticate("employee", request.email(), request.password(), UserRole.EMPLOYEE);
        sessionAuthenticationService.signIn(user, httpServletRequest, httpServletResponse);
        return ApiResponses.success(new AuthUserDto(user.id(), user.email(), user.role().name(), user.employeeProfileId()));
    }
}
