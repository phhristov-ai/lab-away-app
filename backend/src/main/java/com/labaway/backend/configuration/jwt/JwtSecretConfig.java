package com.labaway.backend.configuration;

import com.labaway.backend.configuration.aws.AwsSecretsManagerHelper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JwtSecretConfig {

    private final AwsSecretsManagerHelper secretsHelper;
    private String jwtSecret;

    public JwtSecretConfig() throws Exception {
        this.secretsHelper = new AwsSecretsManagerHelper();
        Map<String, String> jwtSecrets = secretsHelper.getSecret("JWTSecret");
        this.jwtSecret = jwtSecrets.get("jwtSecret");
    }

    public String getJwtSecret() {
        return jwtSecret;
    }
}
