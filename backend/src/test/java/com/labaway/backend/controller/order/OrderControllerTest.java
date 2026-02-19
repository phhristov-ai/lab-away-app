package com.labaway.backend.controller.order;

import com.labaway.backend.dto.order.ConfirmOrderRequestDto;
import com.labaway.backend.dto.order.CreateOrderRequestDto;
import com.labaway.backend.dto.order.CreateOrderResponseDto;
import com.labaway.backend.dto.order.OrderDto;
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

        when(orderService.createOrder(dto)).thenReturn(responseDto);

        ResponseEntity<CreateOrderResponseDto> response = orderController.createOrder(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void testConfirmOrder() {
        String orderNumber = generator.generate();
        String clientId = "clientId";
        ConfirmOrderRequestDto confirmOrderRequestDto = new ConfirmOrderRequestDto();
        confirmOrderRequestDto.setOrderNumber(orderNumber);
        confirmOrderRequestDto.setGaClientId(clientId);

        doNothing().when(orderService).confirmOrder(orderNumber, clientId);

        ResponseEntity<Void> response = orderController.confirmOrder(confirmOrderRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).confirmOrder(orderNumber, clientId);
    }

    @Test
    void testGetOrder() {
        String orderNumber = generator.generate();
        OrderDto orderDto = new OrderDto();

        when(orderService.getOrderByOrderNumber(orderNumber)).thenReturn(orderDto);

        ResponseEntity<OrderDto> response = orderController.getOrder(orderNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDto, response.getBody());
    }

    @Test
    void testGetAllOrders() {
        List<OrderDto> orderList = List.of(new OrderDto(), new OrderDto());

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