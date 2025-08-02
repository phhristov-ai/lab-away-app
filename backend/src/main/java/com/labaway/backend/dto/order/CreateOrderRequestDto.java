package com.labaway.backend.dto.order;

import com.labaway.backend.enums.Language;
import com.labaway.backend.strategy.PaymentProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequestDto {

    @Email(message = "Billing email must be a valid email address")
    @NotBlank(message = "Billing email is required")
    private String billingEmail;

    @NotBlank(message = "Billing phone is required")
    private String billingPhone;

    @Valid
    @NotNull(message = "Billing address is required")
    private AddressDto billingAddress;

    @Valid
    @NotNull(message = "Shipping address is required")
    private AddressDto shippingAddress;

    @NotNull(message = "Payment provider is required")
    private PaymentProvider paymentProvider;

    @NotNull(message = "Order items are required")
    @Size(min = 1, message = "At least one item is required")
    private List<OrderItemDto> items;

    @NotNull(message = "Active language must be sent")
    private Language language;
}
