package com.labaway.backend.service.communication;

import com.labaway.backend.entity.order.Order;

public interface EmailService {
    void sendOrderConfirmationEmail(Order order);
}