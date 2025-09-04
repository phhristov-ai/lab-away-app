package com.labaway.backend.dto.order;

import com.labaway.backend.strategy.PaymentProvider;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {

    private String orderNumber;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be a valid email address")
    private String customerEmail;

    @NotNull(message = "Billing address is required")
    private AddressDto billingAddress;

    @NotNull(message = "Shipping address is required")
    private AddressDto shippingAddress;

    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
    private BigDecimal totalPrice;

    @NotBlank(message = "Order status is required")
    private String status;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider paymentProvider;

    @NotNull
    @Size(min = 1, message = "At least one order item is required")
    private List<OrderItemDto> orderItems;
}
