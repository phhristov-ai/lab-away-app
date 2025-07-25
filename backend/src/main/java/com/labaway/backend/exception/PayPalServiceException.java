package com.labaway.backend.exception;

public class PayPalServiceException extends RuntimeException {
    public PayPalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
