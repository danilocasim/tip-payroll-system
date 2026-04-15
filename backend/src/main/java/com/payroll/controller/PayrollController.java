package com.payroll.controller;

import com.payroll.service.PayrollService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PayrollController {
    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @GetMapping("/payroll/report")
    public Map<String, Object> report(@RequestParam(required = false) String campus) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("rows", payrollService.getPayrollReport(campus));
        payload.put("totals", payrollService.reportTotals(campus));
        return payload;
    }

    @PostMapping("/payroll/save")
    public Map<String, Object> save(@RequestParam(required = false) String campus) {
        int saved = payrollService.saveSnapshot(campus);
        return Map.of("savedRecords", saved);
    }

    @GetMapping("/dashboard/summary")
    public Map<String, Object> summary() {
        return payrollService.dashboardSummary();
    }
}
