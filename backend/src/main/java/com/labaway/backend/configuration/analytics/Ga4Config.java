package com.labaway.backend.configuration.analytics;

import com.labaway.backend.configuration.aws.AwsSecretsManagerHelper;
import com.labaway.backend.configuration.secrets.SecretsManagerHelper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class Ga4Config {
    private final SecretsManagerHelper secretsHelper;
    private String measurementId;
    private String apiSecret;

    @PostConstruct
    public void init() throws Exception {
        Map<String, String> gaSecrets =
                secretsHelper.getSecret("Ga4Keys");

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
