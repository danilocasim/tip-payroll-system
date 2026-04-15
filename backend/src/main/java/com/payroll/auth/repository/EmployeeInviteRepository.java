package com.payroll.auth.repository;

import com.payroll.auth.model.EmployeeInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeInviteRepository extends JpaRepository<EmployeeInvite, String> {
    Optional<EmployeeInvite> findByTokenHash(String tokenHash);

    Optional<EmployeeInvite> findByUserAccountId(String userAccountId);

    void deleteByUserAccountId(String userAccountId);
}
