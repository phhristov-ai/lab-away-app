package com.labaway.backend.transformer.order;

import com.labaway.backend.dto.order.*;
import com.labaway.backend.entity.order.Address;
import com.labaway.backend.entity.order.Order;
import com.labaway.backend.entity.order.OrderItem;
import com.labaway.backend.enums.Language;
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

    public OrderEmailDto mapToEmailDto(Order order) {
        Language language = Language.valueOf(order.getLanguage().toUpperCase());

        return new OrderEmailDto(
                order.getOrderNumber(),
                order.getCreatedAt(),
                order.getCustomerEmail(),
                order.getLanguage(),
                mapAddressToEmailDto(order.getBillingAddress()),
                mapAddressToEmailDto(order.getShippingAddress()),
                order.getPaymentProvider().name(),
                order.getTotalPrice(),
                order.getOrderItems().stream()
                        .map(item -> mapOrderItemToEmailDto(item, language))
                        .toList()
        );
    }

    private AddressEmailDto mapAddressToEmailDto(Address address) {
        return new AddressEmailDto(
                address.getFirstName(),
                address.getLastName(),
                address.getStreetAddress(),
                address.getCity(),
                address.getPostCode(),
                address.getCountry(),
                address.getPhone()
        );
    }

    private OrderItemEmailDto mapOrderItemToEmailDto(
            OrderItem item,
            Language language
    ) {
        return new OrderItemEmailDto(
                item.getProduct().getTranslatedName(language),
                item.getQuantity(),
                item.getPrice()
        );
    }

}
