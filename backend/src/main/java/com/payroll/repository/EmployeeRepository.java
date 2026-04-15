package com.payroll.repository;

import com.payroll.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findByCampusIgnoreCaseOrderByNameAsc(String campus);
}
