package com.payroll.employee.controller;

import com.payroll.common.api.ApiResponse;
import com.payroll.common.api.ApiResponses;
import com.payroll.common.security.SessionUser;
import com.payroll.employee.dto.EmployeeDashboardSummaryDto;
import com.payroll.employee.service.EmployeePortalService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee/dashboard")
public class EmployeeDashboardController {
    private final EmployeePortalService employeePortalService;

    public EmployeeDashboardController(EmployeePortalService employeePortalService) {
        this.employeePortalService = employeePortalService;
    }

    @GetMapping("/summary")
    public ApiResponse<EmployeeDashboardSummaryDto> summary(@AuthenticationPrincipal SessionUser sessionUser) {
        return ApiResponses.success(employeePortalService.getDashboard(sessionUser));
    }
}
