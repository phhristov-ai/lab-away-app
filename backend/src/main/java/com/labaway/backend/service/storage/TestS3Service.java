package com.labaway.backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.labaway.backend.properties.AwsProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Service
//@Profile("test")
//@Primary
public class TestS3Service extends S3Service {
    private final List<String> uploadedKeys = new ArrayList<>();
    private final Map<String, byte[]> cachedFiles = new HashMap<>();

    public TestS3Service(AmazonS3 amazonS3, AwsProperties awsProperties) {
        super(amazonS3, awsProperties);
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String fileUrl = super.uploadFile(file);
        System.out.println(fileUrl);
        String key = extractKeyFromUrl(fileUrl);
        uploadedKeys.add(key);
        return fileUrl;
    }

    @Override
    public void deleteFile(String fileUrl) {
        String bucketName = awsProperties.getS3BucketName();
        String key = extractKeyFromUrl(fileUrl);

        // Fetch file content before deleting
        try (S3Object s3Object = amazonS3.getObject(bucketName, key);
             S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            byte[] content = inputStream.readAllBytes();
            cachedFiles.put(key, content);
            System.out.println("Cached file content for key: " + key);
        } catch (IOException e) {
            System.err.println("Failed to cache file before deletion: " + key);
            e.printStackTrace();
        }

        // Delete the file
        amazonS3.deleteObject(bucketName, key);
        System.out.println("Deleted file from S3: " + key);
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    @PreDestroy
    public void cleanUpTestFiles() {
        try {
            // Delete uploaded files
            for (String key : uploadedKeys) {
                try {
                    amazonS3.deleteObject(awsProperties.getS3BucketName(), key);
                    System.out.println("Deleted uploaded test file: " + key);
                } catch (Exception e) {
                    System.err.println("Failed to delete uploaded test file: " + key);
                    e.printStackTrace();
                }
            }

            // Re-upload deleted original files
            String bucketName = awsProperties.getS3BucketName();
            cachedFiles.forEach((key, content) -> {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(content)) {
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(content.length);

                    amazonS3.putObject(new PutObjectRequest(bucketName, key, bais, metadata));
                    System.out.println("Re-uploaded cached file: " + key);
                } catch (Exception e) {
                    System.err.println("Failed to re-upload cached file: " + key);
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("Unexpected exception in cleanUpTestFiles:");
            e.printStackTrace();
        }
    }


}
