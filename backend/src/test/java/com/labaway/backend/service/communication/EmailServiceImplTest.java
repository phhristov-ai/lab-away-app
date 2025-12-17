package com.labaway.backend.service.communication;

import com.labaway.backend.configuration.mail.SmtpConfig;
import com.labaway.backend.entity.order.Address;
import com.labaway.backend.entity.order.Order;
import com.labaway.backend.entity.order.OrderItem;
import com.labaway.backend.entity.product.Product;
import com.labaway.backend.entity.product.ProductTranslation;
import com.labaway.backend.enums.Language;
import com.labaway.backend.service.communication.EmailServiceImpl;
import com.labaway.backend.strategy.PaymentProvider;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private SmtpConfig smtpConfig;

    @Mock
    private MimeMessage mimeMessage;

    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() throws Exception {
        when(smtpConfig.getFromEmail()).thenReturn("test@example.com");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        emailService = new EmailServiceImpl(smtpConfig, mailSender, templateEngine);
    }

    @Test
    void sendOrderConfirmationEmail_shouldSendEmailWithHtmlContent() {
        Order order = createTestOrder();
        when(templateEngine.process(eq("order-confirmation-en"), any(Context.class))).thenReturn("<html>Email Content</html>");

        emailService.sendOrderConfirmationEmail(order);

        verify(mailSender).send(any(MimeMessage.class));
        verify(templateEngine).process(eq("order-confirmation-en"), any(Context.class));
    }

    private Address createBillingAddress() {
        return Address.builder()
                .firstName("Richard")
                .lastName("Lorenzo")
                .streetAddress("1169 Quaye Lake Cir")
                .city("Wellington")
                .postCode("33411")
                .country("United States (US)")
                .build();
    }

    private Address createShippingAddress() {
        return Address.builder()
                .firstName("Richard")
                .lastName("Lorenzo")
                .streetAddress("Easy-Delivery 1EC8MO 33 boulevard Tisseron")
                .city("MARSEILLE")
                .postCode("13014")
                .country("France")
                .build();
    }

    private List<OrderItem> createOrderItems(Order order) {
        return List.of(
                OrderItem.builder()
                        .price(BigDecimal.valueOf(18.00))
                        .quantity(1)
                        .product(getProduct())
                        .order(order)
                        .build()
        );
    }

    private Product getProduct() {
        Product product = new Product();
        ProductTranslation translation = new ProductTranslation();
        translation.setProduct(product);
        translation.setLanguage(Language.EN);
        translation.setName("STD Bundle");
        translation.setDescription("Description");
        product.setTranslations(List.of(translation));
        return product;
    }

    private Order createTestOrder() {
        Address billing = createBillingAddress();
        Address shipping = createShippingAddress();

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .customerEmail("pastorrich@therroc.org")
                .billingAddress(billing)
                .shippingAddress(shipping)
                .paymentProvider(PaymentProvider.STRIPE)
                .totalPrice(BigDecimal.valueOf(36.00))
                .createdAt(Instant.parse("2024-12-17T00:00:00Z"))
                .language("EN")
                .build();

        order.setOrderItems(createOrderItems(order));

        return order;
    }

}
