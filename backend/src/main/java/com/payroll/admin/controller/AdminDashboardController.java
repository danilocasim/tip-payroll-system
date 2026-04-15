package com.payroll.admin.controller;

import com.payroll.admin.dto.AdminDashboardSummaryDto;
import com.payroll.admin.service.AdminDashboardService;
import com.payroll.common.api.ApiResponse;
import com.payroll.common.api.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/summary")
    public ApiResponse<AdminDashboardSummaryDto> summary() {
        return ApiResponses.success(adminDashboardService.getSummary());
    }
}
