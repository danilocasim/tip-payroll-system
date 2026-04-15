package com.payroll.identity.repository;

import com.payroll.identity.model.PasswordCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordCredentialRepository extends JpaRepository<PasswordCredential, String> {
    Optional<PasswordCredential> findByUserAccountId(String userAccountId);
}
