package com.labaway.backend.dto.payment;

import com.labaway.backend.strategy.PaymentProvider;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePaymentRequestDto {
    private long amount;
    private String currency;
    private String customerEmail;
    private String successUrl;
    private String cancelUrl;
    private PaymentProvider paymentProvider;
}
