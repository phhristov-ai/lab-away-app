package com.labaway.backend.dto.order;

public record ConfirmOrderRequestDto(
        String orderNumber,
        String gaClientId
) {}