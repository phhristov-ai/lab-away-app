package com.labaway.backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.labaway.backend.properties.AwsProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class S3ServiceTest {

    private AmazonS3 amazonS3;
    private AwsProperties awsProperties;
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        amazonS3 = mock(AmazonS3.class);
        awsProperties = mock(AwsProperties.class);
        s3Service = new S3Service(amazonS3, awsProperties);
    }

    @Test
    void uploadFile_shouldUploadToS3AndReturnUrl() throws IOException {
        MockMultipartFile mockFile = createMockFile("file.txt", "Sample content");
        String bucketName = "test-bucket";

        when(awsProperties.getS3BucketName()).thenReturn(bucketName);
        when(amazonS3.getUrl(eq(bucketName), anyString()))
                .thenReturn(new URL("https://s3.amazonaws.com/test-bucket/fakeFileName"));
        String url = s3Service.uploadFile(mockFile);

        assertEquals("https://s3.amazonaws.com/test-bucket/fakeFileName", url);
        verifyPutObjectRequest(bucketName, mockFile.getOriginalFilename());
    }

    @Test
    void deleteFile_shouldCallAmazonS3DeleteObject() {
        String bucketName = "test-bucket";
        String fileUrl = "https://s3.amazonaws.com/test-bucket/file-to-delete.txt";

        when(awsProperties.getS3BucketName()).thenReturn(bucketName);
        s3Service.deleteFile(fileUrl);

        verify(amazonS3, times(1))
                .deleteObject(bucketName, "file-to-delete.txt");
    }

    private MockMultipartFile createMockFile(String filename, String content) {
        return new MockMultipartFile(
                "file", filename, "text/plain", content.getBytes());
    }

    private void verifyPutObjectRequest(String expectedBucketName, String expectedFileNamePart) {
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(amazonS3, times(1)).putObject(requestCaptor.capture());

        PutObjectRequest actualRequest = requestCaptor.getValue();
        assertEquals(expectedBucketName, actualRequest.getBucketName());
        assert actualRequest.getKey().contains(expectedFileNamePart);
    }
}
