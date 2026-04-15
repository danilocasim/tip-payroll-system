package com.payroll.common.exception;

import com.payroll.common.api.ApiResponse;
import com.payroll.common.api.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fields.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage() == null ? "invalid" : fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableEntity().body(ApiResponses.error("validation_failed", "invalid input", fields));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.unprocessableEntity().body(ApiResponses.error("validation_failed", "invalid input"));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponses.error("not_found", ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponses.error("conflict", ex.getMessage()));
    }

    @ExceptionHandler(RoleMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleRoleMismatch(RoleMismatchException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponses.error("role_mismatch", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponses.error("invalid_credentials", ex.getMessage()));
    }

    @ExceptionHandler(InvalidInviteException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidInvite(InvalidInviteException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponses.error("invalid_invite", ex.getMessage()));
    }

    @ExceptionHandler(InviteExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleInviteExpired(InviteExpiredException ex) {
        return ResponseEntity.status(HttpStatus.GONE).body(ApiResponses.error("invite_expired", ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponses.error("forbidden", ex.getMessage()));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<Void>> handleLocked(LockedException ex) {
        return ResponseEntity.status(HttpStatus.LOCKED).body(ApiResponses.error("account_locked", ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponses.error("unauthenticated", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ApiResponses.error("bad_request", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponses.error("server_error", "unexpected server error"));
    }
}
