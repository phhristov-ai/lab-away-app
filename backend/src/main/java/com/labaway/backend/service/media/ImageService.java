package com.labaway.backend.service.media;

import com.labaway.backend.dto.image.ImageUrls;
import com.labaway.backend.service.storage.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.*;
import java.awt.*;
import java.awt.image.*;

@Service
public class ImageService {

    private final S3Service s3Service;

    public ImageService(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public ImageUrls processAndUploadImage(MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            String fileName = originalFileName != null ? originalFileName : "default_image";

            byte[] fileBytes = readImageBytes(file);

            String smallImageUrl = processAndUploadImageForSize(fileBytes, fileName, 480, 480);
            String mediumImageUrl = processAndUploadImageForSize(fileBytes, fileName, 768, 768);
            String largeImageUrl = processAndUploadImageForSize(fileBytes, fileName, 1200, 1200);

            return new ImageUrls(smallImageUrl, mediumImageUrl, largeImageUrl);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing and uploading image", e);
        }
    }

    private String processAndUploadImageForSize(byte[] fileBytes, String baseFileName, int width, int height) throws IOException {
        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        processImageForSize(fileBytes, imageStream, width, height);

        byte[] webPImageBytes = imageStream.toByteArray();

        return uploadToS3(webPImageBytes, baseFileName, width);
    }

    private void processImageForSize(byte[] fileBytes, ByteArrayOutputStream outputStream, int width, int height) throws IOException {
        BufferedImage originalImage = readImage(fileBytes);

        BufferedImage resizedImage = resizeImagePreservingAspectRatio(originalImage, width, height);

        convertToWebP(resizedImage, outputStream);
    }

    private byte[] readImageBytes(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] inputBytes = inputStream.readAllBytes();
            if (inputBytes.length == 0) {
                throw new IOException("The image file is empty or could not be read.");
            }

            return inputBytes;
        }
    }

    private BufferedImage readImage(byte[] imageBytes) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(byteArrayInputStream);

            if (image == null) {
                throw new IOException("Failed to read image from byte array. The image may be in an unsupported format or corrupted.");
            }
            return image;
        }
    }

    private BufferedImage resizeImagePreservingAspectRatio(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        double scale = Math.min((double) targetWidth / originalWidth, (double) targetHeight / originalHeight);

        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resizedImage;
    }

    private void convertToWebP(BufferedImage image, ByteArrayOutputStream outputStream) throws IOException {
        boolean isWritten = ImageIO.write(image, "webp", outputStream);
        if (!isWritten) {
            throw new IOException("Failed to write image as WebP");
        }
    }
    private String uploadToS3(byte[] imageBytes, String baseFileName, int width) {
        String fileName = baseFileName + "-" + width + ".webp";
        return s3Service.uploadFileWithName(imageBytes, fileName);
    }

}
