package com.labaway.backend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;

public class AwsSecretsManagerHelper {

    private final SecretsManagerClient client;

    public AwsSecretsManagerHelper() {
        this.client = SecretsManagerClient.builder()
                .region(Region.of(System.getenv("AWS_REGION")))
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
