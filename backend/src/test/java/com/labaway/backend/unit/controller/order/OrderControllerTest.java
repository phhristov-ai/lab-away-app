package com.labaway.backend.unit.controller.order;

import com.labaway.backend.controller.order.OrderController;
import com.labaway.backend.dto.order.*;
import com.labaway.backend.enums.Language;
import com.labaway.backend.enums.OrderStatus;
import com.labaway.backend.service.order.OrderService;
import com.labaway.backend.strategy.PaymentProvider;
import com.labaway.backend.util.OrderNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;
    @InjectMocks
    private OrderController orderController;
    private final OrderNumberGenerator generator = new OrderNumberGenerator();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        CreateOrderRequestDto dto = createOrderRequestDto();
        CreateOrderResponseDto responseDto = new CreateOrderResponseDto(generator.generate(), PaymentProvider.STRIPE,  "stripeSessionId", "paymentIntentId", BigDecimal.TEN);

        when(orderService.createOrder(dto)).thenReturn(responseDto);

        ResponseEntity<CreateOrderResponseDto> response = orderController.createOrder(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(orderService).createOrder(dto);
    }

    private CreateOrderRequestDto createOrderRequestDto() {
        return new CreateOrderRequestDto(
                "john.doe@example.com",
                "+359888123456",
                createAddressDto(),
                createAddressDto(),
                PaymentProvider.STRIPE,
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

    private AddressDto createAddressDto() {
        return new AddressDto(
                "John",
                "Doe",
                "Bulgaria",
                "Main Street 1",
                "Sofia",
                "1000",
                "+359888123456"
        );
    }

    @Test
    void testCreateExpressOrder() {
        CreateExpressOrderRequestDto dto = new CreateExpressOrderRequestDto(
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
        CreateOrderResponseDto responseDto =
                new CreateOrderResponseDto(
                        generator.generate(),
                        PaymentProvider.PAYPAL,
                        "paypalOrderId",
                        null,
                        BigDecimal.TEN);

        when(orderService.createExpressOrder(dto))
                .thenReturn(responseDto);

        ResponseEntity<CreateOrderResponseDto> response =
                orderController.createExpressOrder(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());

        verify(orderService).createExpressOrder(dto);
    }

    @Test
    void testConfirmOrder() {
        String orderNumber = generator.generate();
        String clientId = "clientId";
        ConfirmOrderRequestDto confirmOrderRequestDto = new ConfirmOrderRequestDto(orderNumber, clientId);

        doNothing().when(orderService).confirmOrder(orderNumber, clientId);

        ResponseEntity<Void> response = orderController.confirmOrder(confirmOrderRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).confirmOrder(orderNumber, clientId);
    }

    @Test
    void testConfirmExpressOrder() {
        ConfirmExpressOrderRequestDto dto = createConfirmExpressOrderRequestDto();

        doNothing().when(orderService)
                .confirmExpressOrder(dto);

        ResponseEntity<Void> response =
                orderController.confirmExpressOrder(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(orderService).confirmExpressOrder(dto);
    }

    private ConfirmExpressOrderRequestDto createConfirmExpressOrderRequestDto() {
        return new ConfirmExpressOrderRequestDto(
                "ORDER-123",
                "ga-client-id",
                "john.doe@example.com",
                createAddressDto(),
                createAddressDto(),
                "paypal-capture-id"
        );
    }

    @Test
    void testGetOrder() {
        String orderNumber = generator.generate();
        OrderDto orderDto = createOrderDto(orderNumber);

        when(orderService.getOrderByOrderNumber(orderNumber)).thenReturn(orderDto);

        ResponseEntity<OrderDto> response = orderController.getOrder(orderNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDto, response.getBody());
    }

    private OrderDto createOrderDto(String orderNumber) {
        return new OrderDto(
                orderNumber,
                "john.doe@example.com",
                createAddressDto(),
                createAddressDto(),
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

    @Test
    void testGetAllOrders() {
        List<OrderDto> orderList = List.of(
                createOrderDto("ORDER-001"),
                createOrderDto("ORDER-002")
        );

        when(orderService.getAllOrders()).thenReturn(orderList);

        ResponseEntity<List<OrderDto>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderList, response.getBody());
    }

    @Test
    void testDeleteOrder() {
        String orderNumber = generator.generate();

        doNothing().when(orderService).deleteOrder(orderNumber);

        ResponseEntity<Void> response = orderController.deleteOrder(orderNumber);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService).deleteOrder(orderNumber);
    }

    @Test
    void testUpdateStatus() {
        String orderNumber = generator.generate();
        OrderStatus status = OrderStatus.PAID;

        doNothing().when(orderService).updateOrderStatus(orderNumber, status);

        ResponseEntity<Void> response = orderController.updateStatus(orderNumber, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService).updateOrderStatus(orderNumber, status);
    }
}