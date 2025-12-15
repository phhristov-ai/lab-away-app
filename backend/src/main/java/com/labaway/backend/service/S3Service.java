package com.labaway.backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.labaway.backend.properties.AwsProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3Service {

    protected final AmazonS3 amazonS3;
    protected final AwsProperties awsProperties;

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

    public String uploadFileWithName(byte[] fileBytes, String fileName) {
        try {
            InputStream inputStream = new ByteArrayInputStream(fileBytes);
            amazonS3.putObject(awsProperties.getS3BucketName(), fileName, inputStream, null);
            return amazonS3.getUrl(awsProperties.getS3BucketName(), fileName).toString();
        } catch (Exception e) {
            throw new RuntimeException("Error uploading image to S3 with name: " + fileName, e);
        }
    }

    public void deleteFile(String fileUrl) {
        String bucketName = awsProperties.getS3BucketName();
        String fileKey = extractFileKey(fileUrl);
        amazonS3.deleteObject(bucketName, fileKey);
    }

    private String extractFileKey(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

}
