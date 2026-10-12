package com.labaway.backend.service.communication;

import com.labaway.backend.configuration.properties.EmailProperties;
import com.labaway.backend.dto.order.OrderEmailDto;
import com.labaway.backend.dto.order.OrderItemEmailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailServiceImpl.class);

    private static final Map<String, String> subjects = Map.of(
            "en", "Your order has been confirmed",
            "de", "Ihre Bestellung wurde bestätigt"
    );

    private final SesV2Client sesClient;
    private final TemplateEngine templateEngine;
    private final EmailProperties emailProperties;

    public EmailServiceImpl(
            SesV2Client sesClient,
            TemplateEngine templateEngine,
            EmailProperties emailProperties) {
        this.sesClient = sesClient;
        this.templateEngine = templateEngine;
        this.emailProperties = emailProperties;
    }

    @Override
    @Async("taskExecutor")
    public void sendOrderConfirmationEmail(OrderEmailDto orderEmailDto) {
        try {
            String lang = orderEmailDto.language()
                    .toLowerCase(Locale.ROOT);

            String subject = subjects.getOrDefault(
                    lang,
                    subjects.get("en")
            );

            Context context = buildOrderConfirmationContext(orderEmailDto);

            String html = templateEngine.process(
                    "order-confirmation-" + lang,
                    context
            );

            List<String> admins = emailProperties.admins() == null
                    ? List.of()
                    : emailProperties.admins();

            Destination destination = Destination.builder()
                    .toAddresses(orderEmailDto.customerEmail())
                    .bccAddresses(admins)
                    .build();

            Message message = Message.builder()
                    .subject(Content.builder()
                            .data(subject)
                            .charset("UTF-8")
                            .build())
                    .body(Body.builder()
                            .html(Content.builder()
                                    .data(html)
                                    .charset("UTF-8")
                                    .build())
                            .build())
                    .build();

            SendEmailRequest request = SendEmailRequest.builder()
                    .fromEmailAddress(emailProperties.from())
                    .destination(destination)
                    .content(EmailContent.builder()
                            .simple(message)
                            .build())
                    .build();

            sesClient.sendEmail(request);

            log.info(
                    "Order confirmation email submitted to SES for order {}",
                    orderEmailDto.orderNumber()
            );
        } catch (RuntimeException e) {
            log.error(
                    "Failed to send order confirmation email for order {}",
                    orderEmailDto.orderNumber(),
                    e
            );
        }
    }

    private Context buildOrderConfirmationContext(OrderEmailDto orderEmailDto) {
        Locale locale = Locale.forLanguageTag(orderEmailDto.language());
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        currencyFormat.setCurrency(Currency.getInstance("EUR"));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", locale)
                .withZone(ZoneId.systemDefault());

        Context context = new Context(locale);
        context.setVariable("orderId", orderEmailDto.orderNumber());
        context.setVariable("orderDate", dateFormatter.format(orderEmailDto.createdAt()));

        context.setVariable("billingName", orderEmailDto.billingAddress().firstName() + " " + orderEmailDto.billingAddress().lastName());
        context.setVariable("billingAddress", orderEmailDto.billingAddress().streetAddress());
        context.setVariable("billingCity", orderEmailDto.billingAddress().city());
        context.setVariable("billingPostCode", orderEmailDto.billingAddress().postCode());
        context.setVariable("billingCountry", orderEmailDto.billingAddress().country());
        context.setVariable("billingPhone", orderEmailDto.billingAddress().phone());
        context.setVariable("customerEmail", orderEmailDto.customerEmail());

        context.setVariable("shippingName", orderEmailDto.shippingAddress().firstName() + " " + orderEmailDto.shippingAddress().lastName());
        context.setVariable("shippingAddress", orderEmailDto.shippingAddress().streetAddress());
        context.setVariable("shippingCity", orderEmailDto.shippingAddress().city());
        context.setVariable("shippingPostCode", orderEmailDto.shippingAddress().postCode());
        context.setVariable("shippingPhone", orderEmailDto.shippingAddress().phone());
        context.setVariable("shippingCountry", orderEmailDto.shippingAddress().country());

        context.setVariable("paymentMethod", orderEmailDto.paymentProvider());
        context.setVariable("total", currencyFormat.format(orderEmailDto.totalPrice()));

        BigDecimal subtotal = orderEmailDto.orderItems().stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        context.setVariable("subtotal", currencyFormat.format(subtotal));
        context.setVariable("orderItems", getFormattedOrderItems(orderEmailDto.orderItems(), locale));

        return context;
    }

    private List<Map<String, String>> getFormattedOrderItems(
            List<OrderItemEmailDto> items,
            Locale locale
    ) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        currencyFormat.setCurrency(Currency.getInstance("EUR"));

        return items.stream()
                .map(item -> Map.of(
                        "productName", item.productName(),
                        "quantity", item.quantity().toString(),
                        "price", currencyFormat.format(item.price())
                ))
                .toList();
    }
}