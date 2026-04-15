package com.payroll.repository;

import com.payroll.model.PayrollRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollRecordRepository extends JpaRepository<PayrollRecord, Integer> {
    List<PayrollRecord> findByEmployeeEmployeeIdOrderByGeneratedAtDesc(Integer employeeId);

    List<PayrollRecord> findByPayrollRunIdOrderByGeneratedAtDesc(String payrollRunId);

    Optional<PayrollRecord> findByRecordIdAndEmployeeEmployeeId(Integer recordId, Integer employeeId);

    boolean existsByEmployeeEmployeeId(Integer employeeId);

    boolean existsByEmployeeEmployeeIdAndPayPeriod(Integer employeeId, String payPeriod);

    boolean existsByPayrollRunIdAndEmployeeEmployeeId(String payrollRunId, Integer employeeId);
}
