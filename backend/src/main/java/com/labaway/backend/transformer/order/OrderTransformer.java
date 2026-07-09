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

        return new OrderDto(
                order.getOrderNumber(),
                order.getCustomerEmail(),
                addressTransformer.toDto(order.getBillingAddress()),
                addressTransformer.toDto(order.getShippingAddress()),
                order.getTotalPrice(),
                order.getStatus().toString(),
                LocalDateTime.ofInstant(order.getCreatedAt(), ZoneId.systemDefault()),
                LocalDateTime.ofInstant(order.getUpdatedAt(), ZoneId.systemDefault()),
                order.getPaymentProvider(),
                itemDtos
        );
    }

    private OrderItemDto toDto(OrderItem item) {
        return new OrderItemDto(
                item.getProduct().getSlug(),
                item.getQuantity(),
                item.getPrice()
        );
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
