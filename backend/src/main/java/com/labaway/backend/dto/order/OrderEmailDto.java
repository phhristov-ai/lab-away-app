package com.labaway.backend.dto.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderEmailDto(
        String orderNumber,
        Instant createdAt,
        String customerEmail,
        String language,
        AddressEmailDto billingAddress,
        AddressEmailDto shippingAddress,
        String paymentProvider,
        BigDecimal totalPrice,
        List<OrderItemEmailDto> orderItems
) {}