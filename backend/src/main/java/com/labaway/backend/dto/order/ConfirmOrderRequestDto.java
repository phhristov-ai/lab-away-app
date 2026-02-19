package com.labaway.backend.dto.order;

import lombok.Data;

@Data
public class ConfirmOrderRequestDto {
    private String orderNumber;
    private String gaClientId;
}
