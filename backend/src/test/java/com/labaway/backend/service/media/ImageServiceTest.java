package com.labaway.backend.service.media;

import com.labaway.backend.dto.image.ImageUrls;
import com.labaway.backend.service.media.ImageService;
import com.labaway.backend.service.storage.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ImageServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ImageService imageService;

    private byte[] validImageContent;

    @BeforeEach
    void setUp() throws IOException {
        validImageContent = getClass().getResourceAsStream("/images/Gonorrhea.webp").readAllBytes();

        MockitoAnnotations.openMocks(this);
        when(file.getOriginalFilename()).thenReturn("Gonorrhea");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(validImageContent));
    }



    @Test
    void processAndUploadImage_shouldResizeAndUploadImages() throws IOException {
        Map<String, String> expectedUrls = Map.of(
                "Gonorrhea-480.webp", "https://s3.amazonaws.com/bucket/Gonorrhea-480.webp",
                "Gonorrhea-768.webp", "https://s3.amazonaws.com/bucket/Gonorrhea-768.webp",
                "Gonorrhea-1200.webp", "https://s3.amazonaws.com/bucket/Gonorrhea-1200.webp"
        );

        when(s3Service.uploadFileWithName(any(byte[].class), anyString()))
                .thenAnswer(invocation -> expectedUrls.get(invocation.getArgument(1)));

        ImageUrls result = imageService.processAndUploadImage(file);

        assertNotNull(result);
        assertEquals(expectedUrls.get("Gonorrhea-480.webp"), result.getSmall());
        assertEquals(expectedUrls.get("Gonorrhea-768.webp"), result.getMedium());
        assertEquals(expectedUrls.get("Gonorrhea-1200.webp"), result.getLarge());

        expectedUrls.keySet().forEach(fileName ->
                verify(s3Service, times(1)).uploadFileWithName(any(byte[].class), eq(fileName))
        );
    }


    @Test
    void processAndUploadImage_shouldHandleEmptyFileNameGracefully() throws IOException {
        when(file.getOriginalFilename()).thenReturn(null);

        String defaultFileName = "default_image";

        Map<String, String> expectedUrls = Map.of(
                defaultFileName + "-480.webp", "https://s3.amazonaws.com/bucket/small-default_image.webp",
                defaultFileName + "-768.webp", "https://s3.amazonaws.com/bucket/medium-default_image.webp",
                defaultFileName + "-1200.webp", "https://s3.amazonaws.com/bucket/large-default_image.webp"
        );

        when(s3Service.uploadFileWithName(any(byte[].class), anyString()))
                .thenAnswer(invocation -> expectedUrls.get(invocation.getArgument(1)));

        ImageUrls result = imageService.processAndUploadImage(file);

        assertNotNull(result);
        assertEquals(expectedUrls.get(defaultFileName + "-480.webp"), result.getSmall());
        assertEquals(expectedUrls.get(defaultFileName + "-768.webp"), result.getMedium());
        assertEquals(expectedUrls.get(defaultFileName + "-1200.webp"), result.getLarge());

        expectedUrls.keySet().forEach(fileName ->
                verify(s3Service, times(1)).uploadFileWithName(any(byte[].class), eq(fileName))
        );
    }



    @Test
    void processAndUploadImage_shouldThrowExceptionWhenUploadFails() {
        when(file.getOriginalFilename()).thenReturn("test-image");

        String smallFileName = "test-image-480.webp";
        String mediumFileName = "test-image-768.webp";
        String largeFileName = "test-image-1200.webp";

        when(s3Service.uploadFileWithName(any(byte[].class), anyString()))
                .thenAnswer(invocation -> {
                    String fileName = invocation.getArgument(1);
                    if (fileName.equals(smallFileName)) {
                        throw new RuntimeException("S3 upload failed");
                    } else if (fileName.equals(mediumFileName)) {
                        return "https://s3.amazonaws.com/bucket/" + fileName;
                    } else if (fileName.equals(largeFileName)) {
                        return "https://s3.amazonaws.com/bucket/" + fileName;
                    }
                    return null;
                });

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> imageService.processAndUploadImage(file));

        assertEquals("Error processing and uploading image", thrown.getMessage());
    }


}
