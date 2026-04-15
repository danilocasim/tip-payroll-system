package com.payroll.admin.dto;

import java.util.List;

public record AdminEmployeesPageDto(
        List<AdminEmployeeListItemDto> items,
        int page,
        int perPage,
        long total
) {
}
