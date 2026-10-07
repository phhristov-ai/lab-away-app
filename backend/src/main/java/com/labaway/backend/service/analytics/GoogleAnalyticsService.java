package com.labaway.backend.service.analytics;

import com.labaway.backend.configuration.properties.Ga4Properties;
import com.labaway.backend.entity.order.Order;
import com.labaway.backend.entity.order.OrderItem;
import com.labaway.backend.enums.Language;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleAnalyticsService {

    private final Ga4Properties ga4Properties;
    private final RestTemplate restTemplate;
    private static final Logger log =
            LoggerFactory.getLogger(GoogleAnalyticsService.class);

    @Async("taskExecutor")
    public void sendPurchaseEvent(String clientId, Order order) {
        try {
            Map<String, Object> payload = buildPurchasePayload(clientId, order);
            String url = buildGaUrl();

            restTemplate.postForEntity(url, payload, String.class);

        } catch (Exception e) {
            log.error("GA4 purchase tracking failed for order {}",
                    order.getOrderNumber(), e);
        }
    }

    private Map<String, Object> buildPurchasePayload(String clientId, Order order) {
        return Map.of(
                "client_id", clientId,
                "events", List.of(
                        Map.of(
                                "name", "purchase",
                                "params", buildPurchaseParams(order)
                        )
                )
        );
    }

    private Map<String, Object> buildPurchaseParams(Order order) {
        return Map.of(
                "transaction_id", order.getOrderNumber(),
                "affiliation", "Lab-Away Online Store",
                "value", order.getTotalPrice().doubleValue(),
                "currency", "EUR",
                "items", buildItems(order)
        );
    }

    private List<Map<String, Object>> buildItems(Order order) {
        return order.getOrderItems()
                .stream()
                .map(this::mapOrderItemToGaItem)
                .toList();
    }

    private Map<String, Object> mapOrderItemToGaItem(OrderItem item) {
        Map<String, Object> itemMap = new HashMap<>();

        itemMap.put("item_id", item.getProduct().getSlug());
        itemMap.put("item_name",
                item.getProduct().getTranslatedName(Language.EN));
        itemMap.put("price", item.getPrice().doubleValue());
        itemMap.put("quantity", item.getQuantity());
        itemMap.put("item_category", getCategorySlug(item, 0));
        itemMap.put("item_category2", getCategorySlug(item, 1));

        return itemMap;
    }

    private String getCategorySlug(OrderItem item, int index) {
        return item.getProduct()
                .getCategories()
                .stream()
                .skip(index)
                .findFirst()
                .map(c -> c.getSlug())
                .orElse(null);
    }

    private String buildGaUrl() {
        return String.format(
                "https://www.google-analytics.com/mp/collect?measurement_id=%s&api_secret=%s",
                ga4Properties.measurementId(),
                ga4Properties.apiSecret()
        );
    }
}
