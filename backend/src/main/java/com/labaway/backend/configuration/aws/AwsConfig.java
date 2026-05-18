package com.labaway.backend.configuration.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.labaway.backend.configuration.secrets.SecretsManagerHelper;
import com.labaway.backend.properties.AwsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AwsConfig {

    private final SecretsManagerHelper secretsHelper;
    private final AwsProperties awsProperties;

    @Autowired
    public AwsConfig(
            SecretsManagerHelper secretsHelper,
            AwsProperties awsProperties
    ) throws Exception {

        this.secretsHelper = secretsHelper;
        this.awsProperties = awsProperties;

        Map<String, String> awsSecrets =
                secretsHelper.getSecret("AWSSecrets");

        this.awsProperties.setAccessKeyId(
                awsSecrets.get("awsAccessKeyId"));
        this.awsProperties.setSecretAccessKey(
                awsSecrets.get("awsSecretAccessKey"));
        this.awsProperties.setS3BucketName(
                awsSecrets.get("awsS3BucketName"));
        this.awsProperties.setRegion(
                awsSecrets.get("awsRegion"));
    }
    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(
                awsProperties.getAccessKeyId(),
                awsProperties.getSecretAccessKey()
        );

        return AmazonS3Client.builder()
                .withRegion(Regions.fromName(awsProperties.getRegion()))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}
