package com.labaway.backend.service.order;

import com.labaway.backend.dto.order.*;
import com.labaway.backend.dto.payment.CreatePaymentRequestDto;
import com.labaway.backend.dto.payment.CreatePaymentResponseDto;
import com.labaway.backend.entity.order.Order;

import com.labaway.backend.entity.order.OrderItem;
import com.labaway.backend.entity.product.Product;
import com.labaway.backend.entity.repository.order.OrderItemRepository;
import com.labaway.backend.entity.repository.product.ProductRepository;
import com.labaway.backend.enums.OrderStatus;
import com.labaway.backend.entity.repository.order.OrderRepository;
import com.labaway.backend.service.communication.EmailService;
import com.labaway.backend.strategy.PaymentProvider;
import com.labaway.backend.strategy.PaymentStrategy;
import com.labaway.backend.strategy.PaymentStrategyFactory;
import com.labaway.backend.transformer.order.OrderTransformer;
import com.labaway.backend.util.OrderNumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
//    private final GoogleAnalyticsService analyticsService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public CreateOrderResponseDto createOrder(CreateOrderRequestDto dto) {
        BigDecimal total = calculateTotal(dto.items());

        Order order = createInitialOrder(dto, total);

        createOrderItems(order, dto.items());

        return initiatePaymentAndBuildResponse(order);
    }

    public CreateOrderResponseDto createExpressOrder(CreateExpressOrderRequestDto dto) {
        BigDecimal total = calculateTotal(dto.items());

        Order order = createInitialExpressOrder(dto, total);

        createOrderItems(order, dto.items());

        return initiatePaymentAndBuildResponse(order);
    }

    private CreateOrderResponseDto initiatePaymentAndBuildResponse(
            Order order) {

        CreatePaymentResponseDto paymentResponse = initiatePayment(order);

        attachPaymentSessionId(
                order.getPaymentProvider(),
                order,
                paymentResponse);

        orderRepository.save(order);

        return buildCreateOrderResponse(order, paymentResponse);
    }

    private Order createInitialOrder(CreateOrderRequestDto dto, BigDecimal total) {
        Order order = Order.builder()
                .customerEmail(dto.customerEmail())
                .billingAddress(orderTransformer.toEntity(dto.billingAddress()))
                .shippingAddress(orderTransformer.toEntity(dto.shippingAddress()))
                .totalPrice(total)
                .status(OrderStatus.PENDING)
                .paymentProvider(dto.paymentProvider())
                .orderNumber(orderNumberGenerator.generate())
                .language(dto.language().name())
                .build();

        return orderRepository.save(order);
    }

    private Order createInitialExpressOrder(CreateExpressOrderRequestDto dto,
                                            BigDecimal total) {
        Order order = Order.builder()
                .paymentProvider(dto.paymentProvider())
                .orderNumber(orderNumberGenerator.generate())
                .totalPrice(total)
                .status(OrderStatus.PENDING)
                .language(dto.language().name())
                .build();

        return orderRepository.save(order);
    }

    private void createOrderItems(Order order, List<OrderItemDto> itemDtos) {

        List<OrderItem> orderItems = itemDtos.stream()
                .map(itemDto -> {
                    Product product = productRepository.findBySlug(itemDto.slug())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Product not found with slug: " + itemDto.slug()));

                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(itemDto.quantity())
                            .price(itemDto.price())
                            .build();
                })
                .toList();

        orderItemRepository.saveAll(orderItems);

        order.getOrderItems().clear();
        order.getOrderItems().addAll(orderItems);

        orderRepository.save(order);
    }

    private CreatePaymentResponseDto initiatePayment(Order order) {

        PaymentStrategy strategy =
                paymentStrategyFactory.getStrategy(order.getPaymentProvider());

        Currency currency = Currency.getInstance("EUR");
        int fractionDigits = currency.getDefaultFractionDigits();

        long amount = order.getTotalPrice()
                .movePointRight(fractionDigits)
                .longValue();

        CreatePaymentRequestDto paymentRequest = new CreatePaymentRequestDto(
                amount,
                "eur",
                order.getCustomerEmail(),
                frontendUrl + "/success?orderId=" + order.getId(),
                frontendUrl + "/cancel",
                null
        );

        return strategy.initiatePayment(paymentRequest);
    }

    private void attachPaymentSessionId(PaymentProvider provider, Order order, CreatePaymentResponseDto response) {
        String paymentIntentId = response.paymentIntentId();
        if (provider == PaymentProvider.STRIPE) {
            order.setStripeSessionId(paymentIntentId);
        } else if (provider == PaymentProvider.PAYPAL) {
            order.setPaypalOrderId(paymentIntentId);
        }
    }

    private CreateOrderResponseDto buildCreateOrderResponse(Order order, CreatePaymentResponseDto paymentResponse) {
        return new CreateOrderResponseDto(
                order.getOrderNumber(),
                order.getPaymentProvider(),
                paymentResponse.paymentIntentId(),
                paymentResponse.clientSecret(),
                order.getTotalPrice()
        );
    }

    private BigDecimal calculateTotal(List<OrderItemDto> items) {
        return items.stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void confirmOrder(String orderNumber, String gaClientId) {
        Order order = findOrder(orderNumber);
        completeOrder(order, gaClientId);
    }

    @Transactional
    public void confirmExpressOrder(ConfirmExpressOrderRequestDto dto) {

        Order order = findOrder(dto.orderNumber());

        order.setCustomerEmail(dto.customerEmail());
        order.setBillingAddress(
                orderTransformer.toEntity(dto.billingAddress()));
        order.setShippingAddress(
                orderTransformer.toEntity(dto.shippingAddress()));
        order.setPaypalCaptureId(dto.paypalCaptureId());

        completeOrder(order, dto.gaClientId());
    }

    private Order findOrder(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber) .orElseThrow(() ->
                new EntityNotFoundException("Order not found"));
    }

    private void completeOrder(Order order, String gaClientId) {

        PaymentStrategy strategy =
                paymentStrategyFactory.getStrategy(order.getPaymentProvider());

        String paymentId = getPaymentSessionId(order);

        if (!strategy.isPaymentCompleted(paymentId)) {
            throw new IllegalStateException("Payment has not been completed.");
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        emailService.sendOrderConfirmationEmail(
                orderTransformer.mapToEmailDto(order));

        //    analyticsService.sendPurchaseEvent(clientId, order);
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