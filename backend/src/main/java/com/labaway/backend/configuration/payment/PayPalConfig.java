package com.labaway.backend.configuration;

import com.labaway.backend.configuration.aws.AwsSecretsManagerHelper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PayPalConfig {

    private final String clientId;
    private final String clientSecret;

    public PayPalConfig() throws Exception {
        AwsSecretsManagerHelper secretsHelper = new AwsSecretsManagerHelper();
        Map<String, String> paypalSecrets = secretsHelper.getSecret("PayPalKeys");
        this.clientId = paypalSecrets.get("paypalClientId");
        this.clientSecret = paypalSecrets.get("paypalSecret");
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
