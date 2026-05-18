package com.labaway.backend.configuration.mail;

import com.labaway.backend.configuration.secrets.SecretsManagerHelper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmtpConfig {

    private final SecretsManagerHelper helper;
    private String username;
    private String password;
    private String fromEmail;

    @Autowired
    public SmtpConfig(SecretsManagerHelper helper) {
        this.helper = helper;
    }

    @PostConstruct
    public void init() throws Exception {

        Map<String, String> secrets =
                helper.getSecret("SMTPSecrets");

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
