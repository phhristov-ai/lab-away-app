package com.labaway.backend.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
        int status,
        String message,
        LocalDateTime timestamp,
        Map<String, String> validationErrors
) {

    public static ApiError of(int status, String message) {
        return new ApiError(
                status,
                message,
                LocalDateTime.now(),
                null
        );
    }

    public static ApiError validation(Map<String, String> errors) {
        return new ApiError(
                400,
                "Validation failed",
                LocalDateTime.now(),
                errors
        );
    }
}