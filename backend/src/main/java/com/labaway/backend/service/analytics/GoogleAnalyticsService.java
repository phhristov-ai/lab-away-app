package com.labaway.backend.service.analytics;

import com.labaway.backend.configuration.analytics.Ga4Config;
import com.labaway.backend.entity.order.Order;
import com.labaway.backend.enums.Language;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoogleAnalyticsService {

    private final Ga4Config ga4Config;
    private final RestTemplate restTemplate;
    private static final Logger log =
            LoggerFactory.getLogger(GoogleAnalyticsService.class);

    @Async("taskExecutor")
    public void sendPurchaseEvent(String clientId, Order order) {

        Map<String, Object> payload = Map.of(
                "client_id", clientId,
                "events", List.of(
                        Map.of(
                                "name", "purchase",
                                "params", Map.of(
                                        "transaction_id", order.getOrderNumber(),
                                        "affiliation", "Lab-Away Online Store",
                                        "value", order.getTotalPrice().doubleValue(),
                                        "currency", "EUR",
                                        "items", order.getOrderItems()
                                                .stream()
                                                .map(item -> Map.of(
                                                        "item_id", item.getProduct().getSlug(),
                                                        "item_name", item.getProduct()
                                                                .getTranslatedName(Language.EN),
                                                        "price", item.getPrice().doubleValue(),
                                                        "quantity", item.getQuantity(),
                                                        "item_category", Objects.requireNonNull(item.getProduct()
                                                                .getCategories()
                                                                .stream()
                                                                .findFirst()
                                                                .orElse(null)),
                                                        "item_category2", Objects.requireNonNull(item.getProduct()
                                                                .getCategories()
                                                                .stream()
                                                                .skip(1)
                                                                .findFirst()
                                                                .orElse(null))
                                                ))
                                                .collect(Collectors.toList())
                                )
                        )
                )
        );

        String url = String.format(
                "https://www.google-analytics.com/mp/collect?measurement_id=%s&api_secret=%s",
                ga4Config.getMeasurementId(),
                ga4Config.getApiSecret()
        );

        try {
            restTemplate.postForEntity(url, payload, String.class);
        } catch (Exception e) {
            log.error("GA4 purchase tracking failed for order {}",
                    order.getOrderNumber(), e);
        }
    }
}
