package com.labaway.backend.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class AwsMySQLConfig {

    private final AwsSecretsManagerHelper secretsHelper;

    public AwsMySQLConfig() {
        this.secretsHelper = new AwsSecretsManagerHelper();
    }

    @Bean(name = "amazonS3MySQL")
    public AmazonS3 amazonS3() throws Exception {
        Map<String, String> secrets = secretsHelper.getSecret("MySQL");

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(
                secrets.get("accessKeyId"),
                secrets.get("secretAccessKey")
        );

        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(secrets.get("region")))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}