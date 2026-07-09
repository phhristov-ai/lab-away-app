package com.labaway.backend.dto.order;

import com.labaway.backend.strategy.PaymentProvider;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        String orderNumber,

        @NotBlank(message = "Customer email is required")
        @Email(message = "Customer email must be a valid email address")
        String customerEmail,

        @NotNull(message = "Billing address is required")
        AddressDto billingAddress,

        @NotNull(message = "Shipping address is required")
        AddressDto shippingAddress,

        @NotNull(message = "Total price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
        BigDecimal totalPrice,

        @NotBlank(message = "Order status is required")
        String status,

        @NotNull
        LocalDateTime createdAt,

        @NotNull
        LocalDateTime updatedAt,

        @NotNull
        PaymentProvider paymentProvider,

        @NotNull
        @Size(min = 1, message = "At least one order item is required")
        List<OrderItemDto> orderItems
) {}