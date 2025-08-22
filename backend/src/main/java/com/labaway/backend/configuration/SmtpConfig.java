package com.labaway.backend.configuration;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmtpConfig {

    private final String username;
    private final String password;
    private final String fromEmail;

    public SmtpConfig() throws Exception {
        AwsSecretsManagerHelper helper = new AwsSecretsManagerHelper();
        Map<String, String> secrets = helper.getSecret("SMTPSecrets");

        this.username = secrets.get("smtpUsername");
        this.password = secrets.get("smtpPassword");
        this.fromEmail = secrets.get("fromEmail");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFromEmail() {
        return fromEmail;
    }
}
