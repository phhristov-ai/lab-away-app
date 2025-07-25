package com.labaway.backend.template;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

record TestProduct(String name, double price) {}
record TestOrderItem(TestProduct product, int quantity) {}

class OrderConfirmationTemplateTest {

    @TempDir
    private Path tempDir;

    @Test
    void testRenderOrderConfirmationTemplate() throws IOException {
        TemplateEngine templateEngine = thymeleafTemplateEngine();
        Context context = createContextWithTestData();

        String htmlContent = templateEngine.process("order-confirmation", context);

        Path outputFile = tempDir.resolve("order-confirmation-test.html");
        Files.writeString(outputFile, htmlContent);

        assertThat(htmlContent)
                .contains("123456")
                .contains("John Doe")
                .contains("product-1");

        Files.delete(outputFile);
    }

    private Context createContextWithTestData() {
        Context context = new Context();
        context.setVariable("orderId", "123456");
        context.setVariable("billingName", "John Doe");
        context.setVariable("orderDate", "2025-05-26");
        context.setVariable("orderItems", createTestOrderItems());

        context.setVariable("subtotal", "89.97");
        context.setVariable("shipping", "Free");
        context.setVariable("paymentMethod", "Credit Card");
        context.setVariable("total", "89.97");

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

    private List<TestOrderItem> createTestOrderItems() {
        return List.of(
                new TestOrderItem(new TestProduct("product-1", 19.99), 2),
                new TestOrderItem(new TestProduct("product-2", 49.99), 1)
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
