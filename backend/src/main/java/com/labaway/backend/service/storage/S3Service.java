package com.labaway.backend.service.storage;

import com.labaway.backend.configuration.properties.AwsProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public S3Service(S3Client s3Client, AwsProperties awsProperties) {
        this.s3Client = s3Client;
        this.awsProperties = awsProperties;
    }

    public String uploadFile(MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(awsProperties.s3().bucketName())
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromInputStream(
                        file.getInputStream(),
                        file.getSize()
                )
        );

        return buildS3Url(fileName);
    }


    public String uploadFileWithName(byte[] fileBytes, String fileName) {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(awsProperties.s3().bucketName())
                .key(fileName)
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(fileBytes)
        );

        return buildS3Url(fileName);
    }


    public void deleteFile(String fileUrl) {

        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        String fileKey = extractFileKey(fileUrl);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(awsProperties.s3().bucketName())
                .key(fileKey)
                .build();

        s3Client.deleteObject(request);
    }


    private String buildS3Url(String fileName) {
        return String.format(
                "https://%s.s3.%s.amazonaws.com/%s",
                awsProperties.s3().bucketName(),
                awsProperties.region(),
                fileName
        );
    }


    private String extractFileKey(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }
}