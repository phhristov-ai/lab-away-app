package com.labaway.backend.dto.order;

import com.labaway.backend.enums.Language;
import com.labaway.backend.strategy.PaymentProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequestDto(

        @Email(message = "Customer email must be a valid email address")
        @NotBlank(message = "Customer email is required")
        String customerEmail,

        @NotBlank(message = "Billing phone is required")
        String billingPhone,

        @Valid
        @NotNull(message = "Billing address is required")
        AddressDto billingAddress,

        @Valid
        @NotNull(message = "Shipping address is required")
        AddressDto shippingAddress,

        @NotNull(message = "Payment provider is required")
        PaymentProvider paymentProvider,

        @NotNull(message = "Order items are required")
        @Size(min = 1, message = "At least one item is required")
        List<OrderItemDto> items,

        @NotNull(message = "Active language must be sent")
        Language language

) {}
