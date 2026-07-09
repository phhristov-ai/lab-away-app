package com.labaway.backend.dto.order;

import com.labaway.backend.strategy.PaymentProvider;

import java.math.BigDecimal;

public record CreateOrderResponseDto(
        String orderNumber,
        PaymentProvider provider,
        String sessionId,
        String clientSecret,
        BigDecimal total
) {}