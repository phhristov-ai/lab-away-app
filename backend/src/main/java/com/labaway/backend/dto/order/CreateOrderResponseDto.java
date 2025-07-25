package com.labaway.backend.dto.order;

import com.labaway.backend.strategy.PaymentProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class CreateOrderResponseDto {
    private String orderNumber;
    private PaymentProvider provider;
    private String sessionId;
    private String clientSecret;
    private BigDecimal total;
}
