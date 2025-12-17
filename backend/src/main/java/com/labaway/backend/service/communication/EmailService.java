package com.labaway.backend.service;

import com.labaway.backend.entity.order.Order;

public interface EmailService {
    void sendOrderConfirmationEmail(Order order);
}