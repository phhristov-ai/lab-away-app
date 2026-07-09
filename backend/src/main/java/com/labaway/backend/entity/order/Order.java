package com.labaway.backend.entity.order;

import com.labaway.backend.enums.OrderStatus;
import com.labaway.backend.strategy.PaymentProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "order_number", nullable = false, unique = true, updatable = false)
    private String orderNumber;

    @Column(name = "customer_email")
    private String customerEmail;

    @Embedded
    @AttributeOverride(name = "firstName", column = @Column(name = "shipping_first_name"))
    @AttributeOverride(name = "lastName", column = @Column(name = "shipping_last_name"))
    @AttributeOverride(name = "country", column = @Column(name = "shipping_country"))
    @AttributeOverride(name = "streetAddress", column = @Column(name = "shipping_address"))
    @AttributeOverride(name = "city", column = @Column(name = "shipping_city"))
    @AttributeOverride(name = "postCode", column = @Column(name = "shipping_post_code"))
    @AttributeOverride(name = "phone", column = @Column(name = "shipping_phone"))
    private Address shippingAddress;

    @Embedded
    @AttributeOverride(name = "firstName", column = @Column(name = "billing_first_name"))
    @AttributeOverride(name = "lastName", column = @Column(name = "billing_last_name"))
    @AttributeOverride(name = "country", column = @Column(name = "billing_country"))
    @AttributeOverride(name = "streetAddress", column = @Column(name = "billing_address"))
    @AttributeOverride(name = "city", column = @Column(name = "billing_city"))
    @AttributeOverride(name = "postCode", column = @Column(name = "billing_post_code"))
    @AttributeOverride(name = "phone", column = @Column(name = "billing_phone"))
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

    @Column(name = "paypal_capture_id")
    private String paypalCaptureId;

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