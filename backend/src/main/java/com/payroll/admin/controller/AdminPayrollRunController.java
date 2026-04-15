package com.payroll.admin.controller;

import com.payroll.common.api.ApiResponse;
import com.payroll.common.api.ApiResponses;
import com.payroll.common.security.SessionUser;
import com.payroll.payroll.dto.CreatePayrollRunRequest;
import com.payroll.payroll.dto.PayrollRunDetailDto;
import com.payroll.payroll.dto.PayrollRunListItemDto;
import com.payroll.payroll.service.PayrollRunService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/payroll-runs")
public class AdminPayrollRunController {
    private final PayrollRunService payrollRunService;

    public AdminPayrollRunController(PayrollRunService payrollRunService) {
        this.payrollRunService = payrollRunService;
    }

    @GetMapping
    public ApiResponse<List<PayrollRunListItemDto>> list() {
        return ApiResponses.success(payrollRunService.list());
    }

    @PostMapping
    public ApiResponse<PayrollRunDetailDto> create(
            @Valid @RequestBody CreatePayrollRunRequest request,
            @AuthenticationPrincipal SessionUser sessionUser
    ) {
        return ApiResponses.success(payrollRunService.create(request, sessionUser));
    }

    @GetMapping("/{payrollRunId}")
    public ApiResponse<PayrollRunDetailDto> detail(@PathVariable String payrollRunId) {
        return ApiResponses.success(payrollRunService.detail(payrollRunId));
    }

    @PostMapping("/{payrollRunId}/finalize")
    public ApiResponse<PayrollRunDetailDto> finalizeRun(
            @PathVariable String payrollRunId,
            @AuthenticationPrincipal SessionUser sessionUser
    ) {
        return ApiResponses.success(payrollRunService.finalizeRun(payrollRunId, sessionUser));
    }
}
