package com.payroll.common.api;

import java.util.Map;

public final class ApiResponses {
    private ApiResponses() {
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(false, null, new ApiError(code, message, Map.of()));
    }

    public static ApiResponse<Void> error(String code, String message, Map<String, String> fields) {
        return new ApiResponse<>(false, null, new ApiError(code, message, fields));
    }
}
