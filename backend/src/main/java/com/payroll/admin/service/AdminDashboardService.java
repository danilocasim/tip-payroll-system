package com.payroll.admin.service;

import com.payroll.admin.dto.AdminDashboardSummaryDto;
import com.payroll.service.PayrollService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class AdminDashboardService {
    private final PayrollService payrollService;

    public AdminDashboardService(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    public AdminDashboardSummaryDto getSummary() {
        Map<String, Object> summary = payrollService.dashboardSummary();
        return new AdminDashboardSummaryDto(
                ((Number) summary.getOrDefault("totalEmployees", 0)).intValue(),
                (BigDecimal) summary.getOrDefault("totalPayroll", BigDecimal.ZERO),
                (BigDecimal) summary.getOrDefault("avgNetPay", BigDecimal.ZERO)
        );
    }
}
