package com.labaway.backend.dto.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderItemDto(
        @NotBlank(message = "Product slug is required")
        String slug,

        @NotNull
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price
) {}