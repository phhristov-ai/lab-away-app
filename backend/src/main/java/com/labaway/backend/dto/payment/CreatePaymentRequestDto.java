package com.labaway.backend.dto.payment;

import com.labaway.backend.strategy.PaymentProvider;

public record CreatePaymentRequestDto(
        long amount,
        String currency,
        String customerEmail,
        String successUrl,
        String cancelUrl,
        PaymentProvider paymentProvider
) {}