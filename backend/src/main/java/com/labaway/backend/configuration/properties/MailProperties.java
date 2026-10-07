package com.labaway.backend.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mail")
public record MailProperties(
        String host,
        int port,
        String protocol,
        boolean debug,
        boolean auth,
        boolean starttlsEnabled
) {}