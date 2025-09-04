package com.labaway.backend.service;

import com.labaway.backend.configuration.SmtpConfig;
import com.labaway.backend.entity.order.Order;
import com.labaway.backend.enums.Language;
import com.labaway.backend.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    public EmailServiceImpl(SmtpConfig smtpConfig, JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.fromEmail = smtpConfig.getFromEmail();
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendOrderConfirmationEmail(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    "UTF-8"
            );

            helper.setTo(order.getCustomerEmail());
            helper.setFrom(fromEmail);
            helper.setSubject("Your order has been confirmed");

            Context context = buildOrderConfirmationContext(order);
            String html = templateEngine.process("order-confirmation-" + order.getLanguage().toLowerCase(), context);
            System.out.println("Generated HTML:\n" + html);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send email", e);
        }
    }

    private Context buildOrderConfirmationContext(Order order) {
        Locale locale = Locale.forLanguageTag(order.getLanguage());
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        currencyFormat.setCurrency(Currency.getInstance("EUR"));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", locale)
                .withZone(ZoneId.systemDefault());

        Context context = new Context(locale);
        context.setVariable("orderId", order.getOrderNumber());
        context.setVariable("orderDate", dateFormatter.format(order.getCreatedAt()));

        context.setVariable("billingName", order.getBillingAddress().getFirstName() + " " + order.getBillingAddress().getLastName());
        context.setVariable("billingAddress", order.getBillingAddress().getStreetAddress());
        context.setVariable("billingCity", order.getBillingAddress().getCity());
        context.setVariable("billingPostCode", order.getBillingAddress().getPostCode());
        context.setVariable("billingCountry", order.getBillingAddress().getCountry());
        context.setVariable("billingPhone", order.getBillingAddress());
        context.setVariable("customerEmail", order.getCustomerEmail());

        context.setVariable("shippingName", order.getShippingAddress().getFirstName() + " " + order.getShippingAddress().getLastName());
        context.setVariable("shippingAddress", order.getShippingAddress().getStreetAddress());
        context.setVariable("shippingCity", order.getShippingAddress().getCity());
        context.setVariable("shippingPostCode", order.getShippingAddress().getPostCode());
        context.setVariable("shippingPhone", order.getShippingAddress().getPhone());
        context.setVariable("shippingCountry", order.getShippingAddress().getCountry());

        context.setVariable("paymentMethod", order.getPaymentProvider().toString());
        context.setVariable("total", currencyFormat.format(order.getTotalPrice()));

        BigDecimal subtotal = order.getOrderItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        context.setVariable("subtotal", currencyFormat.format(subtotal));

        context.setVariable("shipping", currencyFormat.format(BigDecimal.valueOf(18)) + " (incl. VAT)");
        context.setVariable("orderItems", getFormattedOrderItems(order, locale));

        return context;
    }

    private List<Map<String, String>> getFormattedOrderItems(Order order, Locale locale) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        currencyFormat.setCurrency(Currency.getInstance("EUR"));

        Language language = Arrays.stream(Language.values())
                .filter(l -> l.name().equalsIgnoreCase(order.getLanguage()))
                .findFirst()
                .orElse(Language.EN);

        return order.getOrderItems().stream()
                .map(item -> Map.of(
                        "productName", item.getProduct().getTranslatedName(language),
                        "quantity", item.getQuantity().toString(),
                        "price", currencyFormat.format(item.getPrice())
                ))
                .toList();
    }

}