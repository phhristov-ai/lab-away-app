package com.labaway.backend.controller.order;

import com.labaway.backend.dto.order.*;
import com.labaway.backend.enums.OrderStatus;
import com.labaway.backend.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CreateOrderResponseDto> createOrder(@RequestBody CreateOrderRequestDto dto) {
        return new ResponseEntity<>(orderService.createOrder(dto), HttpStatus.CREATED);
    }

    @PostMapping("/express")
    public ResponseEntity<CreateOrderResponseDto> createExpressOrder(
            @RequestBody @Valid CreateExpressOrderRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createExpressOrder(dto));
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmOrder(
            @RequestBody ConfirmOrderRequestDto dto) {
        orderService.confirmOrder(dto.orderNumber(), dto.gaClientId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/express/confirm")
    public ResponseEntity<Void> confirmExpressOrder(
            @RequestBody @Valid ConfirmExpressOrderRequestDto dto) {
        orderService.confirmExpressOrder(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getOrderByOrderNumber(orderNumber));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{orderNumber}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderNumber) {
        orderService.deleteOrder(orderNumber);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderNumber}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable String orderNumber, @RequestParam OrderStatus status) {
        orderService.updateOrderStatus(orderNumber, status);
        return ResponseEntity.ok().build();
    }
}
