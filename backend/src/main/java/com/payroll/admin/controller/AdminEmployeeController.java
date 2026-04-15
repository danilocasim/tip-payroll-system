package com.payroll.admin.controller;

import com.payroll.admin.dto.AdminEmployeeDetailDto;
import com.payroll.admin.dto.AdminEmployeesPageDto;
import com.payroll.admin.dto.AdminCreateEmployeeRequest;
import com.payroll.admin.dto.AdminUpdateEmployeeRequest;
import com.payroll.admin.dto.ProvisionedEmployeeDto;
import com.payroll.admin.service.AdminEmployeeQueryService;
import com.payroll.admin.service.AdminEmployeeProvisioningService;
import com.payroll.common.api.ApiResponse;
import com.payroll.common.api.ApiResponses;
import com.payroll.common.security.SessionUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/employees")
public class AdminEmployeeController {
    private final AdminEmployeeQueryService adminEmployeeQueryService;
    private final AdminEmployeeProvisioningService adminEmployeeProvisioningService;

    public AdminEmployeeController(
            AdminEmployeeQueryService adminEmployeeQueryService,
            AdminEmployeeProvisioningService adminEmployeeProvisioningService
    ) {
        this.adminEmployeeQueryService = adminEmployeeQueryService;
        this.adminEmployeeProvisioningService = adminEmployeeProvisioningService;
    }

    @GetMapping
    public ApiResponse<AdminEmployeesPageDto> all(
            @RequestParam(required = false) String campus,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int perPage
    ) {
        return ApiResponses.success(adminEmployeeQueryService.listEmployees(campus, search, page, perPage));
    }

    @PostMapping
    public ApiResponse<ProvisionedEmployeeDto> create(
            @Valid @RequestBody AdminCreateEmployeeRequest request,
            @AuthenticationPrincipal SessionUser sessionUser
    ) {
        return ApiResponses.success(adminEmployeeProvisioningService.createEmployee(request, sessionUser));
    }

    @GetMapping("/{employeeId}")
    public ApiResponse<AdminEmployeeDetailDto> detail(@PathVariable Integer employeeId) {
        return ApiResponses.success(adminEmployeeQueryService.getEmployee(employeeId));
    }

    @PatchMapping("/{employeeId}")
    public ApiResponse<AdminEmployeeDetailDto> update(
            @PathVariable Integer employeeId,
            @Valid @RequestBody AdminUpdateEmployeeRequest request
    ) {
        return ApiResponses.success(adminEmployeeQueryService.updateEmployee(employeeId, request));
    }
}
