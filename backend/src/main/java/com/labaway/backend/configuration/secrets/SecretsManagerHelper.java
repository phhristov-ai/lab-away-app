package com.labaway.backend.configuration.secrets;

import java.util.Map;

public interface SecretsManagerHelper {
    Map<String, String> getSecret(String secretName) throws Exception;
}
