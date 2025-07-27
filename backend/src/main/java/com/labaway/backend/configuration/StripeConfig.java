package com.labaway.backend.configuration;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StripeConfig {

    private final AwsSecretsManagerHelper secretsHelper;
    private String secretKey;

    public StripeConfig() throws Exception {
        this.secretsHelper = new AwsSecretsManagerHelper();
        Map<String, String> stripeSecrets = secretsHelper.getSecret("StripeKeys");
        this.secretKey = stripeSecrets.get("stripeSecretKey");
    }

    public String getSecretKey() {
        return secretKey;
    }
}
