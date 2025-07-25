package com.labaway.backend.dto.subscription;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionRequest {
    private String email;
}
