package com.labaway.backend.dto.order;

import com.labaway.backend.enums.Language;
import com.labaway.backend.strategy.PaymentProvider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateExpressOrderRequestDto(
        @NotNull
        PaymentProvider paymentProvider,

        @NotNull
        @Size(min = 1)
        List<OrderItemDto> items,

        @NotNull
        Language language
) {}