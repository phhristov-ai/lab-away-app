package com.labaway.backend.unit.controller.product;

import com.labaway.backend.controller.product.ProductController;
import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.image.ImageUrls;
import com.labaway.backend.dto.product.main.*;
import com.labaway.backend.dto.product.media.ProductImageDto;
import com.labaway.backend.enums.Language;
import com.labaway.backend.service.product.ProductService;
import com.labaway.backend.service.storage.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @Mock
    private S3Service s3Service;

    @Test
    void getAll_shouldReturnListOfProductPreviews() {
        ProductPreviewDto previewDto = createProductPreviewDto(
                "Test Product",
                "test-product",
                BigDecimal.TEN,
                true
        );

        when(productService.getAllProductPreviews(Language.EN))
                .thenReturn(List.of(previewDto));

        var result = productController.getAll(Language.EN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).imageUrls().small())
                .isEqualTo("http://example.com/image-small.jpg");
        assertThat(result.get(0).imageUrls().medium())
                .isEqualTo("http://example.com/image-medium.jpg");
        assertThat(result.get(0).imageUrls().large())
                .isEqualTo("http://example.com/image-large.jpg");
        assertThat(result.get(0).active()).isTrue();

        verify(productService).getAllProductPreviews(Language.EN);
    }

    @Test
    void getById_shouldReturnProductWithImages() {
        String slug = "test-product";

        ProductImageDto imageDto = createProductImageDto(
                "http://example.com/image-small.jpg",
                "http://example.com/image-medium.jpg",
                "http://example.com/image-large.jpg"
        );

        ProductDto productDto = createProductDto(
                "Test Product",
                slug,
                BigDecimal.TEN,
                5,
                "Test description",
                List.of(createCategoryDto("Test Category", "test-category")),
                List.of(imageDto),
                true
        );

        when(productService.getProductBySlug(slug, Language.EN))
                .thenReturn(productDto);

        var response = productController.getBySlug(slug, Language.EN);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().images()).hasSize(1);
        assertThat(response.getBody().active()).isTrue();

        ProductImageDto dtoImage = response.getBody().images().get(0);

        assertThat(dtoImage.imageUrlSmall())
                .isEqualTo("http://example.com/image-small.jpg");
        assertThat(dtoImage.imageUrlMedium())
                .isEqualTo("http://example.com/image-medium.jpg");
        assertThat(dtoImage.imageUrlLarge())
                .isEqualTo("http://example.com/image-large.jpg");

        verify(productService).getProductBySlug(slug, Language.EN);
    }

    @Test
    void create_shouldReturnCreatedProductWithImages() {
        ProductImageDto imageDto = createProductImageDto(
                "http://example.com/image-small.jpg",
                "http://example.com/image-medium.jpg",
                "http://example.com/image-large.jpg"
        );

        ProductDto productDto = createProductDto(
                "Test Product",
                "test-product",
                BigDecimal.TEN,
                5,
                "Test description",
                List.of(createCategoryDto("Test Category", "test-category")),
                List.of(imageDto),
                true
        );

        String productPayloadDtoJson = createCreateProductJson(
                "Test Product",
                BigDecimal.TEN,
                5,
                "Test description",
                true,
                List.of("test-category")
        );

        when(productService.createProduct(
                any(ProductPayloadDto.class),
                any(MultipartFile[].class),
                any(MultipartFile.class)
        )).thenReturn(productDto);

        MultipartFile[] mockGalleryFiles = new MultipartFile[]{
                createMockMultipartFile("image.jpg", "image content")
        };

        MultipartFile mockBannerFile =
                createMockMultipartFile("video.mp4", "video content");

        ResponseEntity<ProductDto> response =
                productController.createProductWithImages(
                        productPayloadDtoJson,
                        mockGalleryFiles,
                        mockBannerFile
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody())
                .isEqualTo(productDto);

        verify(productService).createProduct(
                any(ProductPayloadDto.class),
                argThat(arr -> arr.length == 1),
                eq(mockBannerFile)
        );

        verifyNoMoreInteractions(productService);
    }

    @Test
    void create_shouldHandleMultipleImages() {
        ProductImageDto imageDto = createProductImageDto(
                "http://example.com/image-small.jpg",
                "http://example.com/image-medium.jpg",
                "http://example.com/image-large.jpg"
        );

        ProductDto productDto = createProductDto(
                "Test Product",
                "test-product",
                BigDecimal.TEN,
                5,
                "Test description",
                List.of(createCategoryDto("Test Category", "test-category")),
                List.of(imageDto),
                true
        );

        String productPayloadDtoJson = createCreateProductJson(
                "Test Product",
                BigDecimal.TEN,
                5,
                "Test description",
                true,
                List.of("test-category")
        );

        MultipartFile[] mockFiles = new MultipartFile[]{
                createMockMultipartFile("image1.jpg", "content1"),
                createMockMultipartFile("image2.jpg", "content2")
        };

        MultipartFile mockBanner =
                createMockMultipartFile("banner.mp4", "banner content");

        when(productService.createProduct(
                any(ProductPayloadDto.class),
                any(MultipartFile[].class),
                any(MultipartFile.class)
        )).thenReturn(productDto);

        ResponseEntity<ProductDto> response =
                productController.createProductWithImages(
                        productPayloadDtoJson,
                        mockFiles,
                        mockBanner
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody())
                .isEqualTo(productDto);

        verify(productService).createProduct(
                any(ProductPayloadDto.class),
                aryEq(mockFiles),
                eq(mockBanner)
        );

        verifyNoMoreInteractions(productService);
    }

    @Test
    void create_whenServiceThrowsIOException_returnsInternalServerError() {
        String productPayloadDtoJson = createCreateProductJson(
                "Test Product",
                BigDecimal.TEN,
                5,
                "Test description",
                true,
                List.of("test-category")
        );

        MultipartFile[] mockFiles = new MultipartFile[]{
                createMockMultipartFile("image.jpg", "content")
        };

        MultipartFile mockBanner =
                createMockMultipartFile("banner.mp4", "banner content");

        when(productService.createProduct(
                any(ProductPayloadDto.class),
                any(MultipartFile[].class),
                any(MultipartFile.class)
        )).thenThrow(new RuntimeException("S3 upload failed"));

        ResponseEntity<ProductDto> response =
                productController.createProductWithImages(
                        productPayloadDtoJson,
                        mockFiles,
                        mockBanner
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        assertThat(response.getBody()).isNull();

        verify(productService).createProduct(
                any(ProductPayloadDto.class),
                aryEq(mockFiles),
                eq(mockBanner)
        );
    }
    @Test
    void delete_shouldReturnNoContent() {
        String slug = "test-product";

        var response = productController.delete(slug);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(productService, times(1)).deleteProductBySlug(slug);
    }

    @Test
    void getRandomProducts_shouldReturnListOfProductPreviews() {
        ProductPreviewDto previewDto1 = createProductPreviewDto("Random Product 1", "random-product-1", BigDecimal.valueOf(99.99), true);
        ProductPreviewDto previewDto2 = createProductPreviewDto("Random Product 2", "random-product-2", BigDecimal.valueOf(149.99), false);

        when(productService.getRandomProductPreviews(Language.EN)).thenReturn(List.of(previewDto1, previewDto2));

        var result = productController.getRandomProducts(Language.EN);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).slug()).isEqualTo("random-product-1");
        assertThat(result.get(0).imageUrls().small()).isEqualTo("http://example.com/image-small.jpg");
        assertThat(result.get(0).active()).isTrue();
        assertThat(result.get(1).slug()).isEqualTo("random-product-2");
        assertThat(result.get(1).imageUrls().small()).isEqualTo("http://example.com/image-small.jpg");
        assertThat(result.get(1).active()).isFalse();

        verify(productService, times(1)).getRandomProductPreviews(Language.EN);
    }

    private static ProductPreviewDto createProductPreviewDto(
            String name,
            String slug,
            BigDecimal price,
            boolean active) {

        return new ProductPreviewDto(
                name,
                slug,
                price,
                new ImageUrls(
                        "http://example.com/image-small.jpg",
                        "http://example.com/image-medium.jpg",
                        "http://example.com/image-large.jpg"
                ),
                List.of(),
                active
        );
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct_whenProductExists() throws IOException {
        String slug = "test-slug";

        String productPayloadDtoJson = createCreateProductJson(
                "Updated Product",
                BigDecimal.TEN,
                5,
                "Updated description",
                true,
                List.of("test-category")
        );

        MultipartFile[] mockFiles = new MultipartFile[]{
                createMockMultipartFile("image.jpg", "image content")
        };

        MultipartFile mockBanner =
                createMockMultipartFile("banner.mp4", "banner content");

        ProductDto updatedProductDto = createProductDto(
                "Updated Product",
                slug,
                BigDecimal.TEN,
                5,
                "Updated description",
                List.of(createCategoryDto("Test Category", "test-category")),
                List.of(createProductImageDto(
                        "http://example.com/image-small.jpg",
                        "http://example.com/image-medium.jpg",
                        "http://example.com/image-large.jpg"
                )),
                true
        );

        when(productService.updateProduct(
                anyString(),
                any(ProductPayloadDto.class),
                any(MultipartFile[].class),
                any(MultipartFile.class)
        )).thenReturn(updatedProductDto);

        ResponseEntity<ProductDto> response =
                productController.updateProduct(
                        slug,
                        productPayloadDtoJson,
                        mockFiles,
                        mockBanner
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
                .isEqualTo(updatedProductDto);

        verify(productService).updateProduct(
                eq(slug),
                any(ProductPayloadDto.class),
                argThat(arr -> arr.length == 1),
                eq(mockBanner)
        );
    }

    @Test
    void updateProduct_shouldReturnNotFound_whenProductDoesNotExist() throws IOException {
        String slug = "nonexistent-slug";

        String productPayloadDtoJson = createCreateProductJson(
                "Test Product",
                BigDecimal.TEN,
                5,
                "Test description",
                true,
                List.of("test-category")
        );

        MultipartFile[] mockFiles = new MultipartFile[]{
                createMockMultipartFile("image.jpg", "image content")
        };

        MultipartFile mockBanner =
                createMockMultipartFile("banner.mp4", "banner content");

        when(productService.updateProduct(
                anyString(),
                any(ProductPayloadDto.class),
                any(MultipartFile[].class),
                any(MultipartFile.class)
        )).thenReturn(null);

        ResponseEntity<ProductDto> response =
                productController.updateProduct(
                        slug,
                        productPayloadDtoJson,
                        mockFiles,
                        mockBanner
                );

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(response.getBody())
                .isNull();

        verify(productService).updateProduct(
                eq(slug),
                any(ProductPayloadDto.class),
                aryEq(mockFiles),
                eq(mockBanner)
        );
    }

    @Test
    void getRandomProductsByCategory_withSlug_shouldReturnListOfProductPreviews() {
        String categorySlug = "health";
        ProductPreviewDto previewDto1 = createProductPreviewDto("Health Product 1", "health-product-1", BigDecimal.valueOf(59.99), false);
        ProductPreviewDto previewDto2 = createProductPreviewDto("Health Product 2", "health-product-2", BigDecimal.valueOf(79.99), true);

        when(productService.getRandomProductPreviewsByCategorySlug(categorySlug, Language.EN))
                .thenReturn(List.of(previewDto1, previewDto2));

        List<ProductPreviewDto> result = productController.getRandomProductsByCategory(categorySlug, Language.EN);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).slug()).isEqualTo("health-product-1");
        assertThat(result.get(0).imageUrls().small()).isEqualTo("http://example.com/image-small.jpg");
        assertThat(result.get(0).active()).isFalse();
        assertThat(result.get(1).slug()).isEqualTo("health-product-2");
        assertThat(result.get(1).imageUrls().small()).isEqualTo("http://example.com/image-small.jpg");
        assertThat(result.get(1).active()).isTrue();

        verify(productService, times(1)).getRandomProductPreviewsByCategorySlug(categorySlug, Language.EN);
    }

    @Test
    void getRandomProductsByCategory_withoutSlug_shouldReturnListOfRandomProductPreviews() {
        ProductPreviewDto previewDto1 = createProductPreviewDto("Random Product 1", "random-product-1", BigDecimal.valueOf(99.99), true);
        ProductPreviewDto previewDto2 = createProductPreviewDto("Random Product 2", "random-product-2", BigDecimal.valueOf(149.99), false);

        when(productService.getRandomProductPreviewsByCategorySlug(null, Language.EN))
                .thenReturn(List.of(previewDto1, previewDto2));

        List<ProductPreviewDto> result = productController.getRandomProductsByCategory(null, Language.EN);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).slug()).isEqualTo("random-product-1");
        assertThat(result.get(0).imageUrls().small()).isEqualTo("http://example.com/image-small.jpg");
        assertThat(result.get(0).active()).isTrue();
        assertThat(result.get(1).slug()).isEqualTo("random-product-2");
        assertThat(result.get(1).imageUrls().small()).isEqualTo("http://example.com/image-small.jpg");
        assertThat(result.get(1).active()).isFalse();

        verify(productService, times(1)).getRandomProductPreviewsByCategorySlug(null, Language.EN);
    }

    private MockMultipartFile createMockMultipartFile(String filename, String content) {
        return new MockMultipartFile(
                "file",
                filename,
                MediaType.IMAGE_JPEG_VALUE,
                content.getBytes(StandardCharsets.UTF_8)
        );
    }

    private CategoryDto createCategoryDto(String name, String slug) {
        return new CategoryDto(name, slug);
    }

    private static ProductImageDto createProductImageDto(String smallUrl, String mediumUrl, String largeUrl) {
        return new ProductImageDto(
                smallUrl,
                mediumUrl,
                largeUrl,
                null
        );
    }

    private static ProductDto createProductDto(String name, String slug, BigDecimal price, int stock,
                                               String description, List<CategoryDto> categoryDtos,
                                               List<ProductImageDto> images, boolean active) {
        return new ProductDto(
                name,
                slug,
                price,
                stock,
                description,
                categoryDtos,
                LocalDateTime.now(),
                LocalDateTime.now(),
                images,
                active,
                null
        );
    }

    private static String createCreateProductJson(String name, BigDecimal price, int stock,
                                                  String description, boolean active, List<String> categories) {
        ObjectMapper mapper = new ObjectMapper();

        ProductPayloadDto dto = new ProductPayloadDto(
                price,
                stock,
                active,
                categories,
                1,
                new ProductTranslationDto(
                        Language.EN,
                        name,
                        description
                ),
                null
        );

        return mapper.writeValueAsString(dto);

    }

}