package com.labaway.backend.configuration.payment;

import com.labaway.backend.configuration.secrets.SecretsManagerHelper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PayPalConfig {

    private final SecretsManagerHelper secretsHelper;

    private String clientId;
    private String clientSecret;

    @Autowired
    public PayPalConfig(SecretsManagerHelper secretsHelper) {
        this.secretsHelper = secretsHelper;
    }

    @PostConstruct
    public void init() throws Exception {

        Map<String, String> paypalSecrets =
                secretsHelper.getSecret("PayPalKeys");

        this.clientId =
                paypalSecrets.get("paypalClientId");

        this.clientSecret =
                paypalSecrets.get("paypalSecret");
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}