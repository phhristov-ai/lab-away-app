package com.labaway.backend.entity.order;

import com.labaway.backend.enums.OrderStatus;
import com.labaway.backend.strategy.PaymentProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "order_number", nullable = false, unique = true, updatable = false)
    private String orderNumber;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Embedded
    @AttributeOverride(name = "firstName", column = @Column(name = "shipping_first_name", nullable = false))
    @AttributeOverride(name = "lastName", column = @Column(name = "shipping_last_name", nullable = false))
    @AttributeOverride(name = "country", column = @Column(name = "shipping_country", nullable = false))
    @AttributeOverride(name = "streetAddress", column = @Column(name = "shipping_address", nullable = false))
    @AttributeOverride(name = "city", column = @Column(name = "shipping_city", nullable = false))
    @AttributeOverride(name = "postCode", column = @Column(name = "shipping_post_code", nullable = false))
    @AttributeOverride(name = "phone", column = @Column(name = "shipping_phone", nullable = false))
    private Address shippingAddress;

    @Embedded
    @AttributeOverride(name = "firstName", column = @Column(name = "billing_first_name", nullable = false))
    @AttributeOverride(name = "lastName", column = @Column(name = "billing_last_name", nullable = false))
    @AttributeOverride(name = "country", column = @Column(name = "billing_country", nullable = false))
    @AttributeOverride(name = "streetAddress", column = @Column(name = "billing_address", nullable = false))
    @AttributeOverride(name = "city", column = @Column(name = "billing_city", nullable = false))
    @AttributeOverride(name = "postCode", column = @Column(name = "billing_post_code", nullable = false))
    @AttributeOverride(name = "phone", column = @Column(name = "billing_phone", nullable = false))
    private Address billingAddress;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "stripe_session_id")
    private String stripeSessionId;

    @Column(name = "paypal_order_id")
    private String paypalOrderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider paymentProvider;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "language", nullable = false)
    private String language;
}