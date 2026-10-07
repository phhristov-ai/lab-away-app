package com.labaway.backend.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ga4")
public record Ga4Properties(
        String measurementId,
        String apiSecret
) {
}