package com.labaway.backend.configuration;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {

    private final JwtSecretConfig jwtSecretConfig;

    public JwtConfig(JwtSecretConfig jwtSecretConfig) {
        this.jwtSecretConfig = jwtSecretConfig;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        String secret = jwtSecretConfig.getJwtSecret();
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
