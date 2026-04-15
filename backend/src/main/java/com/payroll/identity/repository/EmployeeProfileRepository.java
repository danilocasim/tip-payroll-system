package com.payroll.identity.repository;

import com.payroll.identity.model.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, String> {
    Optional<EmployeeProfile> findByLegacyEmployeeId(Integer legacyEmployeeId);

    boolean existsByEmployeeNumberIgnoreCase(String employeeNumber);
}
