package com.labaway.backend.dto.order;

import java.math.BigDecimal;

public record OrderItemEmailDto(
        String productName,
        Integer quantity,
        BigDecimal price
) {}
