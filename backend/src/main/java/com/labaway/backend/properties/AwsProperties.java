package com.labaway.backend.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class AwsProperties {
    private String accessKeyId;
    private String secretAccessKey;
    private String s3BucketName;
    private String region;
    private String prerenderedBucketName;
}