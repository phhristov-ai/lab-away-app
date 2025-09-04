package com.labaway.backend.service;

import com.labaway.backend.dto.order.*;
import com.labaway.backend.dto.payment.CreatePaymentRequestDto;
import com.labaway.backend.dto.payment.CreatePaymentResponseDto;
import com.labaway.backend.entity.order.Order;

import com.labaway.backend.entity.order.OrderItem;
import com.labaway.backend.entity.product.Product;
import com.labaway.backend.entity.repository.OrderItemRepository;
import com.labaway.backend.entity.repository.ProductRepository;
import com.labaway.backend.enums.OrderStatus;
import com.labaway.backend.entity.repository.OrderRepository;
import com.labaway.backend.strategy.PaymentProvider;
import com.labaway.backend.strategy.PaymentStrategy;
import com.labaway.backend.strategy.PaymentStrategyFactory;
import com.labaway.backend.transformer.OrderTransformer;
import com.labaway.backend.util.OrderNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final PaymentStrategyFactory paymentStrategyFactory;
    private final EmailService emailService;
    private final OrderTransformer orderTransformer;

    private final OrderNumberGenerator orderNumberGenerator;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public CreateOrderResponseDto createOrder(CreateOrderRequestDto dto) {
        BigDecimal total = calculateTotal(dto.getItems());

        Order order = createInitialOrder(dto, total);

        List<OrderItem> orderItems = createOrderItems(dto.getItems(), order);
        orderItemRepository.saveAll(orderItems);
        order.getOrderItems().clear();
        order.getOrderItems().addAll(orderItems);
        orderRepository.save(order);

        CreatePaymentResponseDto paymentResponse = initiatePayment(dto, total, order);
        attachPaymentSessionId(dto.getPaymentProvider(), order, paymentResponse);
        orderRepository.save(order);

        return buildCreateOrderResponse(order, paymentResponse);
    }

    private Order createInitialOrder(CreateOrderRequestDto dto, BigDecimal total) {
        Order order = Order.builder()
                .customerEmail(dto.getCustomerEmail())
                .billingAddress(orderTransformer.toEntity(dto.getBillingAddress()))
                .shippingAddress(orderTransformer.toEntity(dto.getShippingAddress()))
                .totalPrice(total)
                .status(OrderStatus.PENDING)
                .paymentProvider(dto.getPaymentProvider())
                .orderNumber(orderNumberGenerator.generate())
                .language(dto.getLanguage().name())
                .build();
        return orderRepository.save(order);
    }

    private List<OrderItem> createOrderItems(List<OrderItemDto> itemDtos, Order order) {
        return itemDtos.stream().map(itemDto -> {
            Product product = productRepository.findBySlug(itemDto.getSlug())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with slug: " + itemDto.getSlug()));
            return OrderItem.builder()
                    .quantity(itemDto.getQuantity())
                    .price(itemDto.getPrice())
                    .product(product)
                    .order(order)
                    .build();
        }).toList();
    }

    private CreatePaymentResponseDto initiatePayment(CreateOrderRequestDto dto, BigDecimal total, Order order) {
        PaymentStrategy strategy = paymentStrategyFactory.getStrategy(dto.getPaymentProvider());
        Currency currency = Currency.getInstance("EUR");
        int fractionDigits = currency.getDefaultFractionDigits();
        long amount = total.movePointRight(fractionDigits).longValue();

        CreatePaymentRequestDto paymentRequest = CreatePaymentRequestDto.builder()
                .amount(amount)
                .currency("eur")
                .customerEmail(order.getCustomerEmail())
                .successUrl(frontendUrl + "/success?orderId=" + order.getId())
                .cancelUrl(frontendUrl + "/cancel")
                .build();

        return strategy.initiatePayment(paymentRequest);
    }

    private void attachPaymentSessionId(PaymentProvider provider, Order order, CreatePaymentResponseDto response) {
        String paymentIntentId = response.getPaymentIntentId();
        if (provider == PaymentProvider.STRIPE) {
            order.setStripeSessionId(paymentIntentId);
        } else if (provider == PaymentProvider.PAYPAL) {
            order.setPaypalOrderId(paymentIntentId);
        }
    }

    private CreateOrderResponseDto buildCreateOrderResponse(Order order, CreatePaymentResponseDto paymentResponse) {
        return CreateOrderResponseDto.builder()
                .orderNumber(order.getOrderNumber())
                .provider(order.getPaymentProvider())
                .sessionId(paymentResponse.getPaymentIntentId())
                .clientSecret(paymentResponse.getClientSecret())
                .total(order.getTotalPrice())
                .build();
    }

    private BigDecimal calculateTotal(List<OrderItemDto> items) {
        return items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void confirmOrder(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        PaymentStrategy strategy = paymentStrategyFactory.getStrategy(order.getPaymentProvider());
        String sessionId = getPaymentSessionId(order);

        if (!strategy.isPaymentCompleted(sessionId)) {
            throw new IllegalStateException("Payment has not been completed yet.");
        }
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        emailService.sendOrderConfirmationEmail(order);
    }

    private String getPaymentSessionId(Order order) {
        return switch (order.getPaymentProvider()) {
            case STRIPE -> order.getStripeSessionId();
            case PAYPAL -> order.getPaypalOrderId();
        };
    }

    public OrderDto getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderNumber));
        return orderTransformer.toDto(order);
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderTransformer::toDto)
                .toList();
    }

    public void deleteOrder(String orderNumber) {
        if (!orderRepository.existsByOrderNumber(orderNumber)) {
            throw new IllegalArgumentException("Order not found: " + orderNumber);
        }
        orderRepository.deleteByOrderNumber(orderNumber);
    }

    public void updateOrderStatus(String orderNumber, OrderStatus status) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }
}