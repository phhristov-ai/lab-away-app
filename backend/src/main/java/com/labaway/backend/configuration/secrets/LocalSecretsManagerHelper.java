package com.labaway.backend.configuration;

import com.labaway.backend.configuration.SecretsManagerHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Profile("local")
public class LocalSecretsManagerHelper implements SecretsManagerHelper {

    @Value("${db.username}") private String dbUsername;
    @Value("${db.password}") private String dbPassword;
    @Value("${db.engine}") private String dbEngine;
    @Value("${db.host}") private String dbHost;
    @Value("${db.port}") private String dbPort;
    @Value("${db.name}") private String dbName;
    @Value("${db.instanceIdentifier}") private String dbInstanceIdentifier;

    @Value("${smtp.username}") private String smtpUsername;
    @Value("${smtp.password}") private String smtpPassword;
    @Value("${smtp.fromEmail}") private String smtpFromEmail;

    @Value("${jwt.secret}") private String jwtSecret;

    @Value("${aws.accessKeyId}") private String awsAccessKeyId;
    @Value("${aws.secretAccessKey}") private String awsSecretAccessKey;
    @Value("${aws.s3.bucketName}") private String awsS3BucketName;
    @Value("${aws.region}") private String awsRegion;

    @Value("${paypal.clientId}")
    private String paypalClientId;

    @Value("${paypal.secret}")
    private String paypalSecret;

    @Value("${stripe.publicKey}")
    private String stripePublicKey;

    @Value("${stripe.secretKey}")
    private String stripeSecretKey;

    @Override
    public Map<String, String> getSecret(String secretName) {
        Map<String, String> secrets = new HashMap<>();

        switch (secretName) {
            case "SMTPSecrets":
                secrets.put("smtpUsername", smtpUsername);
                secrets.put("smtpPassword", smtpPassword);
                secrets.put("fromEmail", smtpFromEmail);
                break;

            case "MySQL-Database":
                secrets.put("username", dbUsername);
                secrets.put("password", dbPassword);
                secrets.put("host", dbHost);
                secrets.put("port", dbPort);
                secrets.put("engine", dbEngine);
                secrets.put("dbname", dbName);
                break;

            case "JWTSecret":
                secrets.put("jwtSecret", jwtSecret);
                break;

            case "AWSSecrets":
                secrets.put("awsAccessKeyId", awsAccessKeyId);
                secrets.put("awsSecretAccessKey", awsSecretAccessKey);
                secrets.put("awsS3BucketName", awsS3BucketName);
                secrets.put("awsRegion", awsRegion);
                break;

            case "PayPalKeys":
                secrets.put("paypalClientId", paypalClientId);
                secrets.put("paypalSecret", paypalSecret);
                break;

            case "StripeKeys":
                secrets.put("stripePublicKey", stripePublicKey);
                secrets.put("stripeSecretKey", stripeSecretKey);
                break;

            default:
                throw new IllegalArgumentException("Unknown secret name: " + secretName);
        }

        return secrets;
    }
}