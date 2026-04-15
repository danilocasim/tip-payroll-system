package com.payroll.auth.controller;

import com.payroll.auth.dto.AuthMeData;
import com.payroll.auth.dto.AuthUserDto;
import com.payroll.auth.service.SessionAuthenticationService;
import com.payroll.common.api.ApiResponse;
import com.payroll.common.api.ApiResponses;
import com.payroll.common.security.SessionUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthSessionController {
    private final SessionAuthenticationService sessionAuthenticationService;

    public AuthSessionController(SessionAuthenticationService sessionAuthenticationService) {
        this.sessionAuthenticationService = sessionAuthenticationService;
    }

    @GetMapping("/me")
    public ApiResponse<AuthMeData> me(@AuthenticationPrincipal SessionUser user) {
        return ApiResponses.success(new AuthMeData(
                new AuthUserDto(user.id(), user.email(), user.role().name(), user.employeeProfileId()),
                List.of(user.role().name().toLowerCase())
        ));
    }

    @GetMapping("/csrf")
    public ApiResponse<Map<String, String>> csrf(HttpServletRequest request, CsrfToken csrfToken) {
        String token = csrfToken.getToken();
        if (request.getCookies() != null) {
            token = Arrays.stream(request.getCookies())
                    .filter(cookie -> "XSRF-TOKEN".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(token);
        }

        return ApiResponses.success(Map.of(
                "token", token,
                "headerName", csrfToken.getHeaderName()
        ));
    }

    @PostMapping("/logout")
    public ApiResponse<Map<String, Boolean>> logout(HttpServletRequest request) {
        sessionAuthenticationService.signOut(request);
        return ApiResponses.success(Map.of("loggedOut", true));
    }
}
