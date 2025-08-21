package com.labaway.backend.configuration;

import java.util.Map;

public interface SecretsManagerHelper {
    Map<String, String> getSecret(String secretName) throws Exception;
}
