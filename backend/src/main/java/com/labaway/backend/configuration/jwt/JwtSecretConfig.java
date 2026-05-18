package com.labaway.backend.configuration.jwt;

import com.labaway.backend.configuration.secrets.SecretsManagerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JwtSecretConfig {

    private final SecretsManagerHelper secretsHelper;
    private String jwtSecret;

    @Autowired
    public JwtSecretConfig(SecretsManagerHelper secretsHelper) throws Exception {
        this.secretsHelper = secretsHelper;

        Map<String, String> jwtSecrets =
                secretsHelper.getSecret("JWTSecret");

        this.jwtSecret = jwtSecrets.get("jwtSecret");
    }


    public String getJwtSecret() {
        return jwtSecret;
    }
}
