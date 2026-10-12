package com.labaway.backend.unit.service.communication;

import com.labaway.backend.configuration.properties.EmailProperties;
import com.labaway.backend.dto.order.AddressEmailDto;
import com.labaway.backend.dto.order.OrderEmailDto;
import com.labaway.backend.dto.order.OrderItemEmailDto;
import com.labaway.backend.service.communication.EmailServiceImpl;
import com.labaway.backend.strategy.PaymentProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

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
    private SesV2Client sesClient;
    @Mock
    private TemplateEngine templateEngine;

    private EmailServiceImpl emailService;
    private EmailProperties emailProperties;

    @BeforeEach
    void setUp() {
        emailProperties = new EmailProperties(
                "sales@lab-away.com",
                List.of(
                        "admin1@example.com",
                        "admin2@example.com"
                )
        );

        emailService = new EmailServiceImpl(
                sesClient,
                templateEngine,
                emailProperties
        );
    }

    @Test
    void sendOrderConfirmationEmail_shouldSendEmailWithHtmlContent() {
        OrderEmailDto orderEmailDto = createTestOrderEmailDto();
        when(templateEngine.process(eq("order-confirmation-en"), any(Context.class))).thenReturn("<html>Email Content</html>");

        emailService.sendOrderConfirmationEmail(orderEmailDto);

        verify(sesClient).sendEmail(any(SendEmailRequest.class));
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
