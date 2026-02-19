package com.labaway.backend.configuration.analytics;

import com.labaway.backend.configuration.aws.AwsSecretsManagerHelper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Ga4Config {

    private final String measurementId;
    private final String apiSecret;

    public Ga4Config() throws Exception {
        AwsSecretsManagerHelper secretsHelper = new AwsSecretsManagerHelper();
        Map<String, String> gaSecrets = secretsHelper.getSecret("Ga4Keys");

        this.measurementId = gaSecrets.get("measurementId");
        this.apiSecret = gaSecrets.get("apiSecret");
    }

    public String getMeasurementId() {
        return measurementId;
    }

    public String getApiSecret() {
        return apiSecret;
    }
}
