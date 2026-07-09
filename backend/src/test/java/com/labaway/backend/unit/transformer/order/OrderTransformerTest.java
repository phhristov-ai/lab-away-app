package com.labaway.backend.unit.transformer.order;

import com.labaway.backend.dto.order.AddressDto;
import com.labaway.backend.dto.order.OrderDto;
import com.labaway.backend.entity.order.Address;
import com.labaway.backend.entity.order.Order;
import com.labaway.backend.entity.order.OrderItem;
import com.labaway.backend.entity.product.Product;
import com.labaway.backend.enums.OrderStatus;

import com.labaway.backend.strategy.PaymentProvider;
import com.labaway.backend.transformer.order.AddressTransformer;
import com.labaway.backend.transformer.order.OrderTransformer;
import com.labaway.backend.util.OrderNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OrderTransformerTest {

    private AddressTransformer addressTransformer;
    private OrderTransformer orderTransformer;
    private final OrderNumberGenerator generator = new OrderNumberGenerator();
    private final Instant now = Instant.now();
    private String orderNumber;

    @BeforeEach
    void setUp() {
        addressTransformer = mock(AddressTransformer.class);
        orderTransformer = new OrderTransformer(addressTransformer);
        orderNumber = generator.generate();
    }

    @Test
    void toDto_shouldMapOrderToOrderDto() {
        Address billingAddress = sampleAddress("BillingCity");
        Address shippingAddress = sampleAddress("ShippingCity");
        AddressDto billingDto = sampleAddressDto("BillingCity");
        AddressDto shippingDto = sampleAddressDto("ShippingCity");

        when(addressTransformer.toDto(billingAddress)).thenReturn(billingDto);
        when(addressTransformer.toDto(shippingAddress)).thenReturn(shippingDto);

        Order order = sampleOrder(billingAddress, shippingAddress);

        OrderDto dto = orderTransformer.toDto(order);

        assertThat(dto).isNotNull();
        assertThat(dto.orderNumber()).isEqualTo(orderNumber);
        assertThat(dto.status()).isEqualTo("PENDING");
        assertThat(dto.totalPrice()).isEqualByComparingTo("99.99");
        assertThat(dto.customerEmail()).isEqualTo("user@example.com");
        assertThat(dto.billingAddress()).isEqualTo(billingDto);
        assertThat(dto.shippingAddress()).isEqualTo(shippingDto);
        assertThat(dto.orderItems()).hasSize(2);
        assertThat(dto.orderItems().get(0).price()).isEqualByComparingTo("49.99");
        assertThat(dto.orderItems().get(1).quantity()).isEqualTo(2);

        verify(addressTransformer).toDto(billingAddress);
        verify(addressTransformer).toDto(shippingAddress);
    }

    @Test
    void toEntity_shouldDelegateToAddressTransformer() {
        AddressDto dto = sampleAddressDto("CityX");
        Address entity = sampleAddress("CityX");

        when(addressTransformer.toEntity(dto)).thenReturn(entity);

        Address result = orderTransformer.toEntity(dto);

        assertThat(result.getCity()).isEqualTo("CityX");
        verify(addressTransformer).toEntity(dto);
    }

    @Test
    void toDto_Address_shouldDelegateToAddressTransformer() {
        Address entity = sampleAddress("CityY");
        AddressDto dto = sampleAddressDto("CityY");

        when(addressTransformer.toDto(entity)).thenReturn(dto);

        AddressDto result = orderTransformer.toDto(entity);

        assertThat(result.city()).isEqualTo("CityY");
        verify(addressTransformer).toDto(entity);
    }

    private Address sampleAddress(String city) {
        return Address.builder().city(city).build();
    }

    private AddressDto sampleAddressDto(String city) {
        return new AddressDto(
                "John",
                "Doe",
                "Country",
                "Address",
                city,
                "12345",
                "+359892153902"
        );
    }

    private OrderItem sampleOrderItem(String price, int quantity) {
        Product product = Product.builder()
                .slug("test-product")
                .price(new BigDecimal(price))
                .stock(10)
                .active(true)
                .build();

        return OrderItem.builder()
                .product(product)
                .price(new BigDecimal(price))
                .quantity(quantity)
                .build();
    }

    private Order sampleOrder(Address billingAddress, Address shippingAddress) {
        return Order.builder()
                .orderNumber(orderNumber)
                .status(OrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .totalPrice(new BigDecimal("99.99"))
                .customerEmail("user@example.com")
                .billingAddress(billingAddress)
                .shippingAddress(shippingAddress)
                .paymentProvider(PaymentProvider.STRIPE)
                .orderItems(List.of(
                        sampleOrderItem("49.99", 1),
                        sampleOrderItem("25.00", 2)
                ))
                .build();
    }
}
