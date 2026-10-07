package com.labaway.backend.unit.service.storage;

import com.labaway.backend.configuration.properties.AwsProperties;
import com.labaway.backend.service.storage.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class S3ServiceTest {

    private S3Client s3Client;
    private AwsProperties awsProperties;
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);

        awsProperties = new AwsProperties(
                "eu-north-1",
                new AwsProperties.S3("test-bucket")
        );

        s3Service = new S3Service(s3Client, awsProperties);
    }

    @Test
    void uploadFile_shouldUploadToS3AndReturnUrl() throws IOException {

        MockMultipartFile mockFile =
                createMockFile("file.txt", "Sample content");

        String url = s3Service.uploadFile(mockFile);

        assertTrue(url.contains("test-bucket"));
        assertTrue(url.contains(".txt"));

        ArgumentCaptor<PutObjectRequest> captor =
                ArgumentCaptor.forClass(PutObjectRequest.class);

        verify(s3Client).putObject(
                captor.capture(),
                any(RequestBody.class)
        );

        PutObjectRequest request = captor.getValue();

        assertEquals("test-bucket", request.bucket());
        assertTrue(request.key().endsWith("_file.txt"));
        assertEquals("text/plain", request.contentType());
    }

    @Test
    void deleteFile_shouldCallS3DeleteObject() {

        String bucketName = "test-bucket";
        String fileUrl = "https://s3.amazonaws.com/test-bucket/file-to-delete.txt";

        s3Service.deleteFile(fileUrl);

        ArgumentCaptor<DeleteObjectRequest> captor =
                ArgumentCaptor.forClass(DeleteObjectRequest.class);

        verify(s3Client, times(1))
                .deleteObject(captor.capture());

        DeleteObjectRequest request = captor.getValue();

        assertEquals(bucketName, request.bucket());
        assertEquals("file-to-delete.txt", request.key());
    }

    @Test
    void uploadFileWithName_shouldUploadBytesToS3AndReturnUrl() {

        byte[] fileBytes = "Sample content".getBytes();
        String fileName = "test-file.webp";
        String bucketName = "test-bucket";

        String url = s3Service.uploadFileWithName(fileBytes, fileName);

        assertEquals(
                "https://test-bucket.s3.eu-north-1.amazonaws.com/test-file.webp",
                url
        );

        ArgumentCaptor<PutObjectRequest> requestCaptor =
                ArgumentCaptor.forClass(PutObjectRequest.class);

        ArgumentCaptor<RequestBody> bodyCaptor =
                ArgumentCaptor.forClass(RequestBody.class);

        verify(s3Client, times(1))
                .putObject(
                        requestCaptor.capture(),
                        bodyCaptor.capture()
                );

        PutObjectRequest request = requestCaptor.getValue();

        assertEquals(bucketName, request.bucket());
        assertEquals(fileName, request.key());
    }

    private MockMultipartFile createMockFile(String filename, String content) {
        return new MockMultipartFile(
                "file", filename, "text/plain", content.getBytes());
    }

}
