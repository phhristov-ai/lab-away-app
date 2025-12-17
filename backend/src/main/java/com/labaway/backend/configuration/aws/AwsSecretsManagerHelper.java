package com.labaway.backend.configuration.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.labaway.backend.configuration.secrets.SecretsManagerHelper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;

@Component
@Profile("!local")
public class AwsSecretsManagerHelper implements SecretsManagerHelper {

    private final SecretsManagerClient client;

    public AwsSecretsManagerHelper() {
        this.client = SecretsManagerClient.builder()
                .region(Region.of("eu-north-1"))
                .build();
    }

    public Map<String, String> getSecret(String secretName) throws Exception {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();
        GetSecretValueResponse response = client.getSecretValue(request);

        String secretString = response.secretString();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(secretString, Map.class);
    }
}
