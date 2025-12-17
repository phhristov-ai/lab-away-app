package com.labaway.backend.entity.repository.order;

import com.labaway.backend.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByOrderNumber(String orderNumber);
    boolean existsByOrderNumber(String orderNumber);

    void deleteByOrderNumber(String orderNumber);
}
