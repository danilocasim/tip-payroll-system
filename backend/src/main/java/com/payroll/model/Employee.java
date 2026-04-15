package com.payroll.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(nullable = false, length = 100)
    private String name;

    private String position;

    @Column(name = "work_area")
    private String workArea;

    private String campus;

    @Column(name = "hourly_rate")
    private BigDecimal hourlyRate = BigDecimal.ZERO;

    @Column(name = "hours_worked")
    private BigDecimal hoursWorked = BigDecimal.ZERO;

    private BigDecimal salary = BigDecimal.ZERO;

    private BigDecimal bonus = BigDecimal.ZERO;

    private BigDecimal deductions = BigDecimal.ZERO;

    @Column(name = "pay_period")
    private String payPeriod;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getWorkArea() { return workArea; }
    public void setWorkArea(String workArea) { this.workArea = workArea; }
    public String getCampus() { return campus; }
    public void setCampus(String campus) { this.campus = campus; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
    public BigDecimal getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(BigDecimal hoursWorked) { this.hoursWorked = hoursWorked; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public BigDecimal getBonus() { return bonus; }
    public void setBonus(BigDecimal bonus) { this.bonus = bonus; }
    public BigDecimal getDeductions() { return deductions; }
    public void setDeductions(BigDecimal deductions) { this.deductions = deductions; }
    public String getPayPeriod() { return payPeriod; }
    public void setPayPeriod(String payPeriod) { this.payPeriod = payPeriod; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
