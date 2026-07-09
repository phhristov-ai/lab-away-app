package com.labaway.backend.dto.order;

public record ConfirmExpressOrderRequestDto(
        String orderNumber,
        String gaClientId,
        String customerEmail,
        AddressDto billingAddress,
        AddressDto shippingAddress,
        String paypalCaptureId
) {}