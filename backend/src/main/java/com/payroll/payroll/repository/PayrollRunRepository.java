package com.payroll.payroll.repository;

import com.payroll.payroll.model.PayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PayrollRunRepository extends JpaRepository<PayrollRun, String> {
    List<PayrollRun> findAllByOrderByCreatedAtDesc();

    List<PayrollRun> findByPayPeriodStartAndPayPeriodEnd(LocalDate payPeriodStart, LocalDate payPeriodEnd);
}
