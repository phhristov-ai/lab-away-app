package com.labaway.backend.configuration.payment;

import com.labaway.backend.configuration.secrets.SecretsManagerHelper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StripeConfig {

    private final SecretsManagerHelper secretsHelper;

    private String secretKey;

    @Autowired
    public StripeConfig(SecretsManagerHelper secretsHelper) {
        this.secretsHelper = secretsHelper;
    }

    @PostConstruct
    public void init() throws Exception {

        Map<String, String> stripeSecrets =
                secretsHelper.getSecret("StripeKeys");

        this.secretKey =
                stripeSecrets.get("stripeSecretKey");
    }

    public String getSecretKey() {
        return secretKey;
    }
}