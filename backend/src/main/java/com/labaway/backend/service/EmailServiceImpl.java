package com.labaway.backend.service;

import com.labaway.backend.entity.order.Order;
import com.labaway.backend.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.from}")
    private String fromEmail;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendOrderConfirmationEmail(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(order.getBillingEmail());
            helper.setFrom(fromEmail);
            helper.setSubject("Your order has been confirmed");

            Context context = new Context();
            context.setVariable("orderId", order.getId().toString().substring(0, 8));
            context.setVariable("orderDate", DateTimeFormatter.ofPattern("MMMM dd, yyyy").withZone(ZoneId.systemDefault()).format(order.getCreatedAt()));

            context.setVariable("billingName", order.getBillingAddress().getFirstName() + " " + order.getBillingAddress().getLastName());
            context.setVariable("billingAddress", order.getBillingAddress().getStreetAddress());
            context.setVariable("billingCity", order.getBillingAddress().getCity());
            context.setVariable("billingPostCode", order.getBillingAddress().getPostCode());
            context.setVariable("billingCountry", order.getBillingAddress().getCountry());
            context.setVariable("billingPhone", order.getBillingPhone());
            context.setVariable("billingEmail", order.getBillingEmail());

            context.setVariable("shippingName", order.getShippingAddress().getFirstName() + " " + order.getShippingAddress().getLastName());
            context.setVariable("shippingAddress", order.getShippingAddress().getStreetAddress());
            context.setVariable("shippingCity", order.getShippingAddress().getCity());
            context.setVariable("shippingPostCode", order.getShippingAddress().getPostCode());
            context.setVariable("shippingCountry", order.getShippingAddress().getCountry());

            context.setVariable("paymentMethod", order.getPaymentProvider().toString());
            context.setVariable("total", order.getTotalPrice().toString() + " €");

            BigDecimal subtotal = order.getOrderItems().stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            context.setVariable("subtotal", subtotal.toString() + " €");

            context.setVariable("shipping", "18,00 € (incl. VAT)");
            context.setVariable("orderItems", order.getOrderItems());

            String html = templateEngine.process("order-confirmation", context);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send email", e);        }
    }
}
