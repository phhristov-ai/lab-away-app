package com.labaway.backend.unit.template;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class OrderConfirmationTemplateTest {

    @TempDir
    private Path tempDir;

    @Test
    void testRenderOrderConfirmationTemplate() throws IOException {
        TemplateEngine templateEngine = thymeleafTemplateEngine();
        Context context = createContextWithTestData();

        String htmlContent = templateEngine.process("order-confirmation-en", context);

        Path outputFile = tempDir.resolve("order-confirmation-test.html");
        Files.writeString(outputFile, htmlContent);

        assertThat(htmlContent)
                .contains("123456")
                .contains("John Doe")
                .contains("product-1");

        Files.delete(outputFile);
    }

    private Context createContextWithTestData() {
        Context context = new Context(Locale.ENGLISH);

        context.setVariable("orderId", "123456");
        context.setVariable("billingName", "John Doe");
        context.setVariable("orderDate", "2025-05-26");

        context.setVariable("orderItems", createFormattedOrderItems());

        context.setVariable("subtotal", "89.97 €");
        context.setVariable("shipping", "Free");
        context.setVariable("paymentMethod", "Credit Card");
        context.setVariable("total", "89.97 €");

        context.setVariable("billingAddress", "123 Billing St.");
        context.setVariable("billingCity", "Billingville");
        context.setVariable("billingPostCode", "12345");
        context.setVariable("billingCountry", "Billingland");
        context.setVariable("billingPhone", "123-456-7890");
        context.setVariable("billingEmail", "john.doe@example.com");

        context.setVariable("shippingName", "John Doe");
        context.setVariable("shippingAddress", "456 Shipping Ave.");
        context.setVariable("shippingCity", "Ship City");
        context.setVariable("shippingPostCode", "67890");
        context.setVariable("shippingCountry", "Shippingland");

        return context;
    }

    private List<Map<String, String>> createFormattedOrderItems() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.ENGLISH);

        return List.of(
                Map.of(
                        "productName", "product-1",
                        "quantity", "2",
                        "price", currencyFormat.format(19.99)
                ),
                Map.of(
                        "productName", "product-2",
                        "quantity", "1",
                        "price", currencyFormat.format(49.99)
                )
        );
    }

    private TemplateEngine thymeleafTemplateEngine() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }
}