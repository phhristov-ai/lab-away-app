package com.labaway.backend.unit.service.communication;

import com.labaway.backend.configuration.mail.SmtpConfig;
import com.labaway.backend.dto.order.AddressEmailDto;
import com.labaway.backend.dto.order.OrderEmailDto;
import com.labaway.backend.dto.order.OrderItemEmailDto;
import com.labaway.backend.service.communication.EmailServiceImpl;
import com.labaway.backend.strategy.PaymentProvider;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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

        ReflectionTestUtils.setField(
                emailService,
                "adminEmails",
                new String[]{"test-admin@example.com"}
        );
    }

    @Test
    void sendOrderConfirmationEmail_shouldSendEmailWithHtmlContent() {
        OrderEmailDto orderEmailDto = createTestOrderEmailDto();
        when(templateEngine.process(eq("order-confirmation-en"), any(Context.class))).thenReturn("<html>Email Content</html>");

        emailService.sendOrderConfirmationEmail(orderEmailDto);

        verify(mailSender).send(any(MimeMessage.class));
        verify(templateEngine).process(eq("order-confirmation-en"), any(Context.class));
    }

    private OrderEmailDto createTestOrderEmailDto() {
        return new OrderEmailDto(
                "ORDER-123",
                Instant.parse("2024-12-17T00:00:00Z"),
                "pastorrich@therroc.org",
                "EN",
                createBillingAddressEmailDto(),
                createShippingAddressEmailDto(),
                PaymentProvider.STRIPE.name(),
                BigDecimal.valueOf(36.00),
                createOrderItemEmailDtos()
        );
    }

    private AddressEmailDto createBillingAddressEmailDto() {
        return new AddressEmailDto(
                "Richard",
                "Lorenzo",
                "1169 Quaye Lake Cir",
                "Wellington",
                "33411",
                "United States (US)",
                null
        );
    }

    private AddressEmailDto createShippingAddressEmailDto() {
        return new AddressEmailDto(
                "Richard",
                "Lorenzo",
                "Easy-Delivery 1EC8MO 33 boulevard Tisseron",
                "MARSEILLE",
                "13014",
                "France",
                null
        );
    }

    private List<OrderItemEmailDto> createOrderItemEmailDtos() {
        return List.of(
                new OrderItemEmailDto(
                        "STD Bundle",
                        1,
                        BigDecimal.valueOf(18.00)
                )
        );
    }
}
