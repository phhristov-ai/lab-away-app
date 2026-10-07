package com.labaway.backend.strategy;

import com.labaway.backend.dto.payment.CreatePaymentRequestDto;
import com.labaway.backend.dto.payment.CreatePaymentResponseDto;

public interface PaymentStrategy {
    PaymentProvider getProvider();
    CreatePaymentResponseDto initiatePayment(CreatePaymentRequestDto request);
    boolean isPaymentCompleted(String sessionId);
}
