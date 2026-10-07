package com.labaway.backend.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "smtp")
public record SmtpProperties(
        String username,
        String password,
        String fromEmail
) {}