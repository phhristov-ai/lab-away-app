package com.labaway.backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.labaway.backend.properties.AwsProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.amazonaws.services.s3.model.S3Object;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class S3Service {

    protected final AmazonS3 amazonS3;
    protected final AwsProperties awsProperties;

    private final Map<String, CachedPage> cache = new ConcurrentHashMap<>();
    private final long CACHE_TTL_SECONDS = 600;

    public S3Service(AmazonS3 amazonS3, AwsProperties awsProperties) {
        this.amazonS3 = amazonS3;
        this.awsProperties = awsProperties;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();

        amazonS3.putObject(new PutObjectRequest(
                awsProperties.getS3BucketName(), fileName, inputStream, null));

        return amazonS3.getUrl(awsProperties.getS3BucketName(), fileName).toString();
    }

    public void deleteFile(String fileUrl) {
        String bucketName = awsProperties.getS3BucketName();
        String fileKey = extractFileKey(fileUrl);
        amazonS3.deleteObject(bucketName, fileKey);
    }

    private String extractFileKey(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    public String getHtml(String path) throws IOException {
        CachedPage cached = cache.get(path);
        if (cached != null && !cached.isExpired()) {
            return cached.getContent();
        }

        String key = path.substring(1) + "/index.html";
        S3Object s3Object = amazonS3.getObject(awsProperties.getS3BucketName(), key);
        try (InputStream inputStream = s3Object.getObjectContent()) {
            String html = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            cache.put(path, new CachedPage(html, CACHE_TTL_SECONDS));
            return html;
        }
    }

    private static class CachedPage {
        private final String content;
        private final Instant expiresAt;

        public CachedPage(String content, long ttlSeconds) {
            this.content = content;
            this.expiresAt = Instant.now().plusSeconds(ttlSeconds);
        }

        public boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }

        public String getContent() {
            return content;
        }
    }
}
