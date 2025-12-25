package com.labaway.backend.service.communication;

import com.labaway.backend.configuration.mail.SmtpConfig;
import com.labaway.backend.dto.order.OrderEmailDto;
import com.labaway.backend.dto.order.OrderItemEmailDto;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EmailServiceImpl implements EmailService {
    private String fromEmail;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger log =
            LoggerFactory.getLogger(EmailServiceImpl.class);

    private static final Map<String, String> subjects = Map.of(
            "en", "Your order has been confirmed",
            "de", "Ihre Bestellung wurde bestätigt"
    );

    public EmailServiceImpl(SmtpConfig smtpConfig, JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.fromEmail = smtpConfig.getFromEmail();
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    @Async("taskExecutor")
    public void sendOrderConfirmationEmail(OrderEmailDto orderEmailDto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    "UTF-8"
            );

            helper.setTo(orderEmailDto.customerEmail());
            helper.setFrom(fromEmail);

            String lang = orderEmailDto.language().toLowerCase();
            String subject = subjects.getOrDefault(lang, subjects.get("en"));
            helper.setSubject(subject);

            Context context = buildOrderConfirmationContext(orderEmailDto);
            String html = templateEngine.process("order-confirmation-" + orderEmailDto.language().toLowerCase(), context);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
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