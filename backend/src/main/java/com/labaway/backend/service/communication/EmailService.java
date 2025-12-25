package com.labaway.backend.service.communication;

import com.labaway.backend.dto.order.OrderEmailDto;

public interface EmailService {
    void sendOrderConfirmationEmail(OrderEmailDto orderEmailDto);
}