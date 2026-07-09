package com.labaway.backend.dto.payment;

public record CreatePaymentResponseDto(
        String paymentIntentId,
        String clientSecret
) {}