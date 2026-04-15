package com.payroll.employee.dto;

import java.util.List;

public record EmployeePayrollRecordsPageDto(
        List<EmployeePayrollRecordSummaryDto> items,
        int page,
        int perPage,
        long total
) {
}
