package com.payroll.repository;

import com.payroll.model.PayrollRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollRecordRepository extends JpaRepository<PayrollRecord, Integer> {
}
