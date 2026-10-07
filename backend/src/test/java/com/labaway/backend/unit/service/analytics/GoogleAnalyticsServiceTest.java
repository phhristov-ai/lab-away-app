package com.labaway.backend.unit.service.analytics;

import com.labaway.backend.configuration.properties.Ga4Properties;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.category.CategoryTranslation;
import com.labaway.backend.entity.order.Order;
import com.labaway.backend.entity.order.OrderItem;
import com.labaway.backend.entity.product.Product;
import com.labaway.backend.enums.Language;
import com.labaway.backend.service.analytics.GoogleAnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleAnalyticsServiceTest {

    @Mock
    private Ga4Properties ga4Properties;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GoogleAnalyticsService googleAnalyticsService;

    private Order order;

    @BeforeEach
    void setUp() {
        when(ga4Properties.measurementId()).thenReturn("G-TEST123");
        when(ga4Properties.apiSecret()).thenReturn("secret123");

        Product product = new Product();
        product.setSlug("test-product");
        Category stdTests = createCategory(
                "std-tests",
                Map.of(
                        Language.EN, "STD Tests",
                        Language.DE, "STD Tests"
                )
        );

        Category vitaminTests = createCategory(
                "vitamin-tests",
                Map.of(
                        Language.EN, "Vitamin & Nutrient Tests",
                        Language.DE, "Vitamin- & Nährstofftests"
                )
        );


        product.setCategories(Set.of(stdTests, vitaminTests));

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setPrice(BigDecimal.valueOf(50));
        item.setQuantity(2);

        order = Order.builder()
                .orderNumber("ORDER123")
                .totalPrice(BigDecimal.valueOf(100))
                .orderItems(List.of(item))
                .language("EN")
                .build();
    }

    private Category createCategory(String slug, Map<Language, String> names) {

        Category category = Category.builder()
                .id(UUID.randomUUID())
                .slug(slug)
                .build();

        names.forEach((language, name) -> {
            CategoryTranslation translation = CategoryTranslation.builder()
                    .language(language)
                    .name(name)
                    .category(category)
                    .build();

            category.getTranslations().add(translation);
        });

        return category;
    }


    @Test
    void shouldSendPurchaseEventSuccessfully() {

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        googleAnalyticsService.sendPurchaseEvent("client-123", order);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        verify(restTemplate, times(1))
                .postForEntity(urlCaptor.capture(), any(), eq(String.class));

        String capturedUrl = urlCaptor.getValue();

        assertThat(capturedUrl)
                .contains("measurement_id=G-TEST123")
                .contains("api_secret=secret123");
    }

    @Test
    void shouldHandleRestTemplateExceptionGracefully() {

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("GA error"));

        googleAnalyticsService.sendPurchaseEvent("client-123", order);

        verify(restTemplate, times(1))
                .postForEntity(anyString(), any(), eq(String.class));

    }
}
