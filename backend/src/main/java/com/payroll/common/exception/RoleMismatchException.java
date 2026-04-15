package com.payroll.common.exception;

public class RoleMismatchException extends RuntimeException {
    public RoleMismatchException(String message) {
        super(message);
    }
}
