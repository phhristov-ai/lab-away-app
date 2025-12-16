package com.labaway.backend.transformer.controller;

import com.labaway.backend.controller.OrderController;
import com.labaway.backend.dto.order.CreateOrderRequestDto;
import com.labaway.backend.dto.order.CreateOrderResponseDto;
import com.labaway.backend.dto.order.OrderDto;
import com.labaway.backend.enums.OrderStatus;
import com.labaway.backend.service.OrderService;
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
        CreateOrderRequestDto dto = new CreateOrderRequestDto();
        CreateOrderResponseDto responseDto = new CreateOrderResponseDto(generator.generate(), PaymentProvider.STRIPE,  "stripeSessionId", "paymentIntentId", BigDecimal.TEN);

        Mockito.when(orderService.createOrder(dto)).thenReturn(responseDto);

        ResponseEntity<CreateOrderResponseDto> response = orderController.createOrder(dto);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(responseDto, response.getBody());
    }

    @Test
    void testConfirmOrder() {
        String orderNumber = generator.generate();

        Mockito.doNothing().when(orderService).confirmOrder(orderNumber);

        ResponseEntity<Void> response = orderController.confirmOrder(orderNumber);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(orderService, Mockito.times(1)).confirmOrder(orderNumber);
    }

    @Test
    void testGetOrder() {
        String orderNumber = generator.generate();
        OrderDto orderDto = new OrderDto();

        Mockito.when(orderService.getOrderByOrderNumber(orderNumber)).thenReturn(orderDto);

        ResponseEntity<OrderDto> response = orderController.getOrder(orderNumber);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(orderDto, response.getBody());
    }

    @Test
    void testGetAllOrders() {
        List<OrderDto> orderList = List.of(new OrderDto(), new OrderDto());

        Mockito.when(orderService.getAllOrders()).thenReturn(orderList);

        ResponseEntity<List<OrderDto>> response = orderController.getAllOrders();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(orderList, response.getBody());
    }

    @Test
    void testDeleteOrder() {
        String orderNumber = generator.generate();

        Mockito.doNothing().when(orderService).deleteOrder(orderNumber);

        ResponseEntity<Void> response = orderController.deleteOrder(orderNumber);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Mockito.verify(orderService).deleteOrder(orderNumber);
    }

    @Test
    void testUpdateStatus() {
        String orderNumber = generator.generate();
        OrderStatus status = OrderStatus.PAID;

        Mockito.doNothing().when(orderService).updateOrderStatus(orderNumber, status);

        ResponseEntity<Void> response = orderController.updateStatus(orderNumber, status);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(orderService).updateOrderStatus(orderNumber, status);
    }
}
