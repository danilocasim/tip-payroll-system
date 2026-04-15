package com.payroll.employee.controller;

import com.payroll.common.api.ApiResponse;
import com.payroll.common.api.ApiResponses;
import com.payroll.common.security.SessionUser;
import com.payroll.employee.dto.EmployeePayrollRecordDetailDto;
import com.payroll.employee.dto.EmployeePayrollRecordsPageDto;
import com.payroll.employee.service.EmployeePortalService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee/payroll-records")
public class EmployeePayrollController {
    private final EmployeePortalService employeePortalService;

    public EmployeePayrollController(EmployeePortalService employeePortalService) {
        this.employeePortalService = employeePortalService;
    }

    @GetMapping
    public ApiResponse<EmployeePayrollRecordsPageDto> list(
            @AuthenticationPrincipal SessionUser sessionUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int perPage
    ) {
        return ApiResponses.success(employeePortalService.getPayrollRecords(sessionUser, page, perPage));
    }

    @GetMapping("/{recordId}")
    public ApiResponse<EmployeePayrollRecordDetailDto> detail(
            @AuthenticationPrincipal SessionUser sessionUser,
            @PathVariable Integer recordId
    ) {
        return ApiResponses.success(employeePortalService.getPayrollRecord(sessionUser, recordId));
    }
}
