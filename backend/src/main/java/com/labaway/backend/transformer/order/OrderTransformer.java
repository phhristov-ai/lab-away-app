package com.labaway.backend.transformer.order;

import com.labaway.backend.dto.order.AddressDto;
import com.labaway.backend.dto.order.OrderDto;
import com.labaway.backend.dto.order.OrderItemDto;
import com.labaway.backend.entity.order.Address;
import com.labaway.backend.entity.order.Order;
import com.labaway.backend.entity.order.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderTransformer {

    private final AddressTransformer addressTransformer;

    public OrderDto toDto(Order order) {
        List<OrderItemDto> itemDtos = order.getOrderItems().stream()
                .map(this::toDto)
                .toList();

        return OrderDto.builder()
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus().toString())
                .createdAt(LocalDateTime.ofInstant(order.getCreatedAt(), ZoneId.systemDefault()))
                .updatedAt(LocalDateTime.ofInstant(order.getUpdatedAt(), ZoneId.systemDefault()))
                .totalPrice(order.getTotalPrice())
                .customerEmail(order.getCustomerEmail())
                .billingAddress(addressTransformer.toDto(order.getBillingAddress()))
                .shippingAddress(addressTransformer.toDto(order.getShippingAddress()))
                .paymentProvider(order.getPaymentProvider())
                .orderItems(itemDtos)
                .build();
    }

    private OrderItemDto toDto(OrderItem item) {
        return OrderItemDto.builder()
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }

    public Address toEntity(AddressDto dto) {
        return addressTransformer.toEntity(dto);
    }

    public AddressDto toDto(Address entity) {
        return addressTransformer.toDto(entity);
    }
}
