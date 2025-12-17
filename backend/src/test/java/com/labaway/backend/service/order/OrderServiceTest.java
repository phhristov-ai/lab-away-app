package com.labaway.backend.service.order;

import com.labaway.backend.dto.order.*;
import com.labaway.backend.dto.payment.CreatePaymentResponseDto;
import com.labaway.backend.entity.order.Address;
import com.labaway.backend.entity.order.Order;
import com.labaway.backend.entity.order.OrderItem;
import com.labaway.backend.entity.product.Product;
import com.labaway.backend.entity.repository.order.OrderItemRepository;
import com.labaway.backend.entity.repository.order.OrderRepository;
import com.labaway.backend.entity.repository.product.ProductRepository;
import com.labaway.backend.enums.Language;
import com.labaway.backend.enums.OrderStatus;
import com.labaway.backend.service.communication.EmailService;
import com.labaway.backend.strategy.PaymentProvider;
import com.labaway.backend.strategy.PaymentStrategy;
import com.labaway.backend.strategy.PaymentStrategyFactory;
import com.labaway.backend.transformer.order.AddressTransformer;
import com.labaway.backend.transformer.order.OrderTransformer;
import com.labaway.backend.util.OrderNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentStrategyFactory paymentStrategyFactory;
    @Mock
    private PaymentStrategy paymentStrategy;
    @Mock
    private EmailService emailService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderTransformer orderTransformer;
    @Mock
    private AddressTransformer addressTransformer;

    @InjectMocks
    private OrderService orderService;

    private String orderNumber;
    private Order sampleOrder;
    private final OrderNumberGenerator generator = new OrderNumberGenerator();
    @Mock
    private OrderNumberGenerator orderNumberGenerator;

    @BeforeEach
    void setUp() {
        TestUtils.setField(orderService, "frontendUrl", "http://localhost:3000");
        orderNumber = generator.generate();
        sampleOrder = buildSampleOrder();
    }

    @Test
    void createOrder_shouldReturnResponseWithSessionIdAndOrderId() {
        CreateOrderRequestDto dto = buildCreateOrderRequestDto();

        when(productRepository.findBySlug("test-product")).thenReturn(Optional.of(new Product()));
        when(paymentStrategyFactory.getStrategy(PaymentProvider.STRIPE)).thenReturn(paymentStrategy);
        when(paymentStrategy.initiatePayment(any())).thenReturn(new CreatePaymentResponseDto("test-session-id", "client-secret"));
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderNumberGenerator.generate()).thenReturn(this.orderNumber);
        CreateOrderResponseDto response = orderService.createOrder(dto);

        assertNotNull(response);
        assertEquals("test-session-id", response.getSessionId());

        verify(paymentStrategyFactory).getStrategy(PaymentProvider.STRIPE);
        verify(paymentStrategy).initiatePayment(any());
        verify(orderRepository, times(3)).save(any());
    }

    @Test
    void confirmOrder_shouldSetStatusToPaid() {
        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(sampleOrder));
        when(paymentStrategyFactory.getStrategy(PaymentProvider.STRIPE)).thenReturn(paymentStrategy);
        when(paymentStrategy.isPaymentCompleted(anyString())).thenReturn(true);
        orderService.confirmOrder(orderNumber);

        assertEquals(OrderStatus.PAID, sampleOrder.getStatus());
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void getOrderById_shouldReturnOrderDto() {
        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(sampleOrder));
        OrderDto expectedDto = buildSampleOrderDto();
        when(orderTransformer.toDto(sampleOrder)).thenReturn(expectedDto);

        OrderDto actualDto = orderService.getOrderByOrderNumber(orderNumber);

        assertNotNull(actualDto);
        assertEquals(orderNumber, actualDto.getOrderNumber());
        assertEquals("PENDING", actualDto.getStatus());
    }

    @Test
    void getOrderById_shouldThrowWhenNotFound() {
        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> orderService.getOrderByOrderNumber(orderNumber));
    }

    @Test
    void getAllOrders_shouldMapOrdersToDtos() {
        when(orderRepository.findAll()).thenReturn(List.of(sampleOrder));
        when(orderTransformer.toDto(sampleOrder)).thenReturn(buildSampleOrderDto());

        List<OrderDto> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(orderNumber, result.get(0).getOrderNumber());
    }

    @Test
    void deleteOrder_shouldDeleteIfExists() {
        when(orderRepository.existsByOrderNumber(orderNumber)).thenReturn(true);

        orderService.deleteOrder(orderNumber);

        verify(orderRepository).deleteByOrderNumber(orderNumber);
    }

    @Test
    void deleteOrder_shouldThrowIfNotExists() {
        when(orderRepository.existsByOrderNumber(orderNumber)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> orderService.deleteOrder(orderNumber));
    }

    @Test
    void updateOrderStatus_shouldUpdateStatus() {
        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(sampleOrder));

        orderService.updateOrderStatus(orderNumber, OrderStatus.PAID);

        assertEquals(OrderStatus.PAID, sampleOrder.getStatus());
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void updateOrderStatus_shouldThrowIfOrderNotFound() {
        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.updateOrderStatus(orderNumber, OrderStatus.PAID));
    }

    private CreateOrderRequestDto buildCreateOrderRequestDto() {
        AddressDto addressDto = new AddressDto("John", "Doe", "Country", "Address", "City", "12345", "+359892153902");
        OrderItemDto itemDto = new OrderItemDto("test-product", 1, BigDecimal.TEN);
        return CreateOrderRequestDto.builder()
                .customerEmail("test@example.com")
                .billingPhone("123456")
                .billingAddress(addressDto)
                .shippingAddress(addressDto)
                .items(List.of(itemDto))
                .paymentProvider(PaymentProvider.STRIPE)
                .language(Language.EN)
                .build();
    }

    private Order buildSampleOrder() {
        Address billing = Address.builder().build();
        Address shipping = Address.builder().build();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customerEmail("test@example.com")
                .billingAddress(billing)
                .shippingAddress(shipping)
                .stripeSessionId("test-session-id")
                .paymentProvider(PaymentProvider.STRIPE)
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.TEN)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        OrderItem item = OrderItem.builder()
                .id(UUID.randomUUID())
                .price(BigDecimal.TEN)
                .quantity(1)
                .order(order)
                .build();

        order.setOrderItems(List.of(item));
        return order;
    }

    private OrderDto buildSampleOrderDto() {
        return OrderDto.builder()
                .orderNumber(orderNumber)
                .status("PENDING")
                .customerEmail("test@example.com")
                .totalPrice(BigDecimal.TEN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .billingAddress(new AddressDto("John", "Doe", "Country", "Address", "City", "12345", "+359892153902"))
                .shippingAddress(new AddressDto("John", "Doe", "Country", "Address", "City", "12345", "+359892153902"))
                .orderItems(List.of(OrderItemDto.builder().quantity(1).price(BigDecimal.TEN).build()))
                .paymentProvider(PaymentProvider.STRIPE)
                .build();
    }
}
