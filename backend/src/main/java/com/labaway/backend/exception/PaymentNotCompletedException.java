package com.labaway.backend.exception;

public class PaymentNotCompletedException extends RuntimeException {

    public PaymentNotCompletedException(String message) {
        super(message);
    }
}