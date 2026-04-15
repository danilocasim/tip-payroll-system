package com.payroll.common.exception;

public class LockedException extends RuntimeException {
    public LockedException(String message) {
        super(message);
    }
}
