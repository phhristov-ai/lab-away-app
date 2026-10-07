package com.labaway.backend.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "db")
public record DatabaseProperties(
        String username,
        String password,
        String engine,
        String host,
        int port,
        String name,
        String instanceIdentifier
) {}