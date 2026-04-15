package com.payroll.auth.controller;

import com.payroll.auth.dto.ActivateInviteRequest;
import com.payroll.auth.dto.EmployeeInviteViewDto;
import com.payroll.auth.service.EmployeeInviteService;
import com.payroll.common.api.ApiResponse;
import com.payroll.common.api.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/employee/auth")
public class EmployeeInviteController {
    private final EmployeeInviteService employeeInviteService;

    public EmployeeInviteController(EmployeeInviteService employeeInviteService) {
        this.employeeInviteService = employeeInviteService;
    }

    @GetMapping("/invite")
    public ApiResponse<EmployeeInviteViewDto> invite(@RequestParam String token) {
        return ApiResponses.success(employeeInviteService.getInviteView(token));
    }

    @PostMapping("/activate")
    public ApiResponse<Map<String, Boolean>> activate(@Valid @RequestBody ActivateInviteRequest request) {
        employeeInviteService.activateInvite(request.token(), request.password(), request.confirmPassword());
        return ApiResponses.success(Map.of("activated", true));
    }
}
