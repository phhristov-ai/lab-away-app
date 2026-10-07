package com.labaway.backend.unit.service.order;

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
import com.labaway.backend.exception.OrderNotFoundException;
import com.labaway.backend.service.analytics.GoogleAnalyticsService;
import com.labaway.backend.service.communication.EmailService;
import com.labaway.backend.service.order.OrderService;
import com.labaway.backend.strategy.PaymentProvider;
import com.labaway.backend.strategy.PaymentStrategy;
import com.labaway.backend.strategy.PaymentStrategyFactory;
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

    @InjectMocks
    private OrderService orderService;
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
    private String orderNumber;
    private Order sampleOrder;
    private final OrderNumberGenerator generator = new OrderNumberGenerator();
    @Mock
    private OrderNumberGenerator orderNumberGenerator;
    @Mock
    private GoogleAnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        TestUtils.setField(orderService, "frontendUrl", "http://localhost:3000");
        orderNumber = generator.generate();

    }

    @Test
    void createOrder_shouldReturnResponseWithSessionIdAndOrderId() {
        sampleOrder = buildSampleOrder(PaymentProvider.STRIPE);
        CreateOrderRequestDto dto = buildCreateOrderRequestDto();

        when(productRepository.findBySlug("test-product")).thenReturn(Optional.of(new Product()));
        when(paymentStrategyFactory.getStrategy(PaymentProvider.STRIPE)).thenReturn(paymentStrategy);
        when(paymentStrategy.initiatePayment(any())).thenReturn(new CreatePaymentResponseDto("test-session-id", "client-secret"));
        when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderNumberGenerator.generate()).thenReturn(this.orderNumber);
        CreateOrderResponseDto response = orderService.createOrder(dto);

        assertNotNull(response);
        assertEquals("test-session-id", response.sessionId());

        verify(paymentStrategyFactory).getStrategy(PaymentProvider.STRIPE);
        verify(paymentStrategy).initiatePayment(any());
        verify(orderRepository, times(3)).save(any());
    }

    @Test
    void createExpressOrder_shouldReturnResponseWithSessionIdAndOrderId() {
        sampleOrder = buildSampleOrder(PaymentProvider.STRIPE);
        CreateExpressOrderRequestDto dto = buildCreateExpressOrderRequestDto();

        when(productRepository.findBySlug("test-product"))
                .thenReturn(Optional.of(new Product()));
        when(paymentStrategyFactory.getStrategy(PaymentProvider.PAYPAL))
                .thenReturn(paymentStrategy);
        when(paymentStrategy.initiatePayment(any()))
                .thenReturn(new CreatePaymentResponseDto("paypal-order-id", null));
        when(orderRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(orderNumberGenerator.generate())
                .thenReturn(orderNumber);
        CreateOrderResponseDto response =
                orderService.createExpressOrder(dto);

        assertNotNull(response);
        assertEquals("paypal-order-id", response.sessionId());

        verify(paymentStrategyFactory)
                .getStrategy(PaymentProvider.PAYPAL);
        verify(paymentStrategy)
                .initiatePayment(any());
    }

    private CreateExpressOrderRequestDto buildCreateExpressOrderRequestDto() {
        return new CreateExpressOrderRequestDto(
                PaymentProvider.PAYPAL,
                List.of(
                        new OrderItemDto(
                                "test-product",
                                1,
                                BigDecimal.TEN
                        )
                ),
                Language.EN
        );
    }

    @Test
    void confirmOrder_shouldSetStatusToPaid() {
        sampleOrder = buildSampleOrder(PaymentProvider.STRIPE);
        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(sampleOrder));
        when(paymentStrategyFactory.getStrategy(PaymentProvider.STRIPE)).thenReturn(paymentStrategy);
        when(paymentStrategy.isPaymentCompleted(anyString())).thenReturn(true);
        String clientId = "clientId";
        orderService.confirmOrder(orderNumber, clientId);

        assertEquals(OrderStatus.PAID, sampleOrder.getStatus());
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void confirmExpressOrder_shouldPopulateOrderAndSetStatusToPaid() {
        sampleOrder = buildSampleOrder(PaymentProvider.PAYPAL);
        ConfirmExpressOrderRequestDto dto = buildConfirmExpressOrderRequestDto();

        when(orderRepository.findByOrderNumber(orderNumber))
                .thenReturn(Optional.of(sampleOrder));

        when(paymentStrategyFactory.getStrategy(PaymentProvider.PAYPAL))
                .thenReturn(paymentStrategy);

        when(paymentStrategy.isPaymentCompleted(anyString()))
                .thenReturn(true);

        Address billing = new Address();
        Address shipping = new Address();

        when(orderTransformer.toEntity(dto.billingAddress()))
                .thenReturn(billing);

        when(orderTransformer.toEntity(dto.shippingAddress()))
                .thenReturn(shipping);

        orderService.confirmExpressOrder(dto);

        assertEquals(OrderStatus.PAID, sampleOrder.getStatus());
        assertEquals(dto.customerEmail(), sampleOrder.getCustomerEmail());
        assertEquals(billing, sampleOrder.getBillingAddress());
        assertEquals(shipping, sampleOrder.getShippingAddress());
        assertEquals(dto.paypalCaptureId(), sampleOrder.getPaypalCaptureId());

        verify(orderRepository).save(sampleOrder);
        verify(emailService).sendOrderConfirmationEmail(any());
    }

    private ConfirmExpressOrderRequestDto buildConfirmExpressOrderRequestDto() {
        return new ConfirmExpressOrderRequestDto(
                orderNumber,
                "clientId",
                "john.doe@example.com",
                buildAddressDto(),
                buildAddressDto(),
                "capture-id"
        );
    }

    private AddressDto buildAddressDto() {
        return new AddressDto(
                "John",
                "Doe",
                "DE",
                "Main Street 1",
                "Berlin",
                "10115",
                "+49123456789"
        );
    }

    @Test
    void getOrderById_shouldReturnOrderDto() {
        sampleOrder = buildSampleOrder(PaymentProvider.STRIPE);
        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(sampleOrder));
        OrderDto expectedDto = buildSampleOrderDto();
        when(orderTransformer.toDto(sampleOrder)).thenReturn(expectedDto);

        OrderDto actualDto = orderService.getOrderByOrderNumber(orderNumber);

        assertNotNull(actualDto);
        assertEquals(orderNumber, actualDto.orderNumber());
        assertEquals("PENDING", actualDto.status());
    }

    @Test
    void getOrderById_shouldThrowWhenNotFound() {
        sampleOrder = buildSampleOrder(PaymentProvider.STRIPE);
        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderByOrderNumber(orderNumber));
    }

    @Test
    void getAllOrders_shouldMapOrdersToDtos() {
        sampleOrder = buildSampleOrder(PaymentProvider.STRIPE);
        when(orderRepository.findAll()).thenReturn(List.of(sampleOrder));
        when(orderTransformer.toDto(sampleOrder)).thenReturn(buildSampleOrderDto());

        List<OrderDto> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(orderNumber, result.get(0).orderNumber());
    }

    @Test
    void deleteOrder_shouldDeleteIfExists() {
        sampleOrder = buildSampleOrder(PaymentProvider.STRIPE);
        when(orderRepository.existsByOrderNumber(orderNumber)).thenReturn(true);

        orderService.deleteOrder(orderNumber);

        verify(orderRepository).deleteByOrderNumber(orderNumber);
    }

    @Test
    void deleteOrder_shouldThrowIfNotExists() {
        sampleOrder = buildSampleOrder(PaymentProvider.STRIPE);
        when(orderRepository.existsByOrderNumber(orderNumber)).thenReturn(false);

        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(orderNumber));
    }

    @Test
    void updateOrderStatus_shouldUpdateStatus() {
        sampleOrder = buildSampleOrder(PaymentProvider.STRIPE);
        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(sampleOrder));

        orderService.updateOrderStatus(orderNumber, OrderStatus.PAID);

        assertEquals(OrderStatus.PAID, sampleOrder.getStatus());
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void updateOrderStatus_shouldThrowIfOrderNotFound() {
        sampleOrder = buildSampleOrder(PaymentProvider.STRIPE);
        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.updateOrderStatus(orderNumber, OrderStatus.PAID));
    }

    private CreateOrderRequestDto buildCreateOrderRequestDto() {
        AddressDto addressDto = new AddressDto("John", "Doe", "Country", "Address", "City", "12345", "+359892153902");
        OrderItemDto itemDto = new OrderItemDto("test-product", 1, BigDecimal.TEN);
        return new CreateOrderRequestDto(
                "test@example.com",
                "123456",
                addressDto,
                addressDto,
                PaymentProvider.STRIPE,
                List.of(itemDto),
                Language.EN
        );
    }

    private Order buildSampleOrder(PaymentProvider paymentProvider) {
        Address billing = Address.builder().build();
        Address shipping = Address.builder().build();

        Order.OrderBuilder builder = Order.builder()
                .orderNumber(orderNumber)
                .customerEmail("test@example.com")
                .billingAddress(billing)
                .shippingAddress(shipping)
                .paymentProvider(paymentProvider)
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.TEN)
                .createdAt(Instant.now())
                .updatedAt(Instant.now());

        if (paymentProvider == PaymentProvider.STRIPE) {
            builder.stripeSessionId("test-session-id");
        } else {
            builder.paypalOrderId("test-paypal-order-id");
        }

        Order order = builder.build();

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
        return new OrderDto(
                orderNumber,
                "test@example.com",
                new AddressDto(
                        "John",
                        "Doe",
                        "Country",
                        "Address",
                        "City",
                        "12345",
                        "+359892153902"
                ),
                new AddressDto(
                        "John",
                        "Doe",
                        "Country",
                        "Address",
                        "City",
                        "12345",
                        "+359892153902"
                ),
                BigDecimal.TEN,
                "PENDING",
                LocalDateTime.now(),
                LocalDateTime.now(),
                PaymentProvider.STRIPE,
                List.of(
                        new OrderItemDto(
                                "test-product",
                                1,
                                BigDecimal.TEN
                        )
                )
        );
    }
}
