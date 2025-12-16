package com.labaway.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.image.ImageUrls;
import com.labaway.backend.dto.product.main.*;
import com.labaway.backend.dto.product.image.ProductImageDto;
import com.labaway.backend.enums.Language;
import com.labaway.backend.service.ProductService;
import com.labaway.backend.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @InjectMocks
    private ProductController productController;
    @Mock
    private ProductService productService;
    @Mock
    private S3Service s3Service;
    private ProductDto productDto;
    private String productPayloadDtoJson;
    private ProductImageDto productImageDto;

    private List<CategoryDto> categoryDtos;
    @BeforeEach
    void setUp() throws JsonProcessingException {
        MockitoAnnotations.openMocks(this);
        categoryDtos = buildCategoryDtos();
        productImageDto = createProductImageDto(
                "http://example.com/image-small.jpg",
                "http://example.com/image-medium.jpg",
                "http://example.com/image-large.jpg",
                true
        );

        productDto = createProductDto("Test Product", "test-product", BigDecimal.TEN, 5,
                "Test description", categoryDtos, List.of(productImageDto));

        productPayloadDtoJson = createCreateProductJson("Test Product", BigDecimal.TEN, 5,
                "Test description", true, List.of("test-category", "bundle-category"));
    }

    @Test
    void getAll_shouldReturnListOfProductPreviews() {
        ProductPreviewDto previewDto = createProductPreviewDto("Test Product", "test-product", BigDecimal.TEN);

        when(productService.getAllProductPreviews(Language.EN)).thenReturn(List.of(previewDto));

        var result = productController.getAll(Language.EN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getImageUrls().getSmall()).isEqualTo("http://example.com/image-small.jpg");
        assertThat(result.get(0).getImageUrls().getMedium()).isEqualTo("http://example.com/image-medium.jpg");
        assertThat(result.get(0).getImageUrls().getLarge()).isEqualTo("http://example.com/image-large.jpg");

        verify(productService, times(1)).getAllProductPreviews(Language.EN);
    }

    @Test
    void getById_shouldReturnProductWithImages() {
        String slug = productDto.getSlug();

        ProductImageDto imageDto = createProductImageDto(
                "http://example.com/image-small.jpg",
                "http://example.com/image-medium.jpg",
                "http://example.com/image-large.jpg",
                true
        );

        productDto.setImages(List.of(imageDto));

        when(productService.getProductBySlug(slug, Language.EN)).thenReturn(productDto);

        var response = productController.getBySlug(slug, Language.EN);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getImages()).hasSize(1);

        ProductImageDto dtoImage = response.getBody().getImages().get(0);
        assertThat(dtoImage.getImageUrlSmall()).isEqualTo("http://example.com/image-small.jpg");
        assertThat(dtoImage.getImageUrlMedium()).isEqualTo("http://example.com/image-medium.jpg");
        assertThat(dtoImage.getImageUrlLarge()).isEqualTo("http://example.com/image-large.jpg");
        assertThat(dtoImage.isMain()).isTrue();

        verify(productService, times(1)).getProductBySlug(slug, Language.EN);
    }

    @Test
    void create_shouldReturnCreatedProductWithImages() throws IOException {
        when(productService.createProduct(any(ProductPayloadDto.class), any(MultipartFile[].class)))
                .thenReturn(productDto);

        MultipartFile[] mockFiles = new MultipartFile[] {
                createMockMultipartFile("image.jpg", "image content")
        };

        ResponseEntity<ProductDto> response = productController.createProductWithImages(productPayloadDtoJson, mockFiles);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(productDto);

        verify(productService, times(1)).createProduct(any(ProductPayloadDto.class), aryEq(mockFiles));
        verifyNoMoreInteractions(productService);
        verify(productService).createProduct(any(ProductPayloadDto.class), aryEq(mockFiles));
    }

    @Test
    void create_shouldHandleMultipleImages() throws IOException {
        MultipartFile[] mockFiles = new MultipartFile[] {
                createMockMultipartFile("image1.jpg", "content1"),
                createMockMultipartFile("image2.jpg", "content2")
        };

        when(productService.createProduct(any(ProductPayloadDto.class), any(MultipartFile[].class)))
                .thenReturn(productDto);

        ResponseEntity<ProductDto> response = productController.createProductWithImages(productPayloadDtoJson, mockFiles);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(productDto);

        verify(productService).createProduct(any(ProductPayloadDto.class), aryEq(mockFiles));
    }

    @Test
    void create_whenServiceThrowsIOException_returnsInternalServerError() throws IOException {
        MultipartFile[] mockFiles = new MultipartFile[] {
                createMockMultipartFile("image.jpg", "content")
        };

        when(productService.createProduct(any(), any()))
                .thenThrow(new IOException("S3 upload failed"));

        ResponseEntity<ProductDto> response = productController.createProductWithImages(productPayloadDtoJson, mockFiles);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void delete_shouldReturnNoContent() {
        String slug = productDto.getSlug();

        var response = productController.delete(slug);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(productService, times(1)).deleteProductBySlug(slug);
    }

    @Test
    void getRandomProducts_shouldReturnListOfProductPreviews() {
        ProductPreviewDto previewDto1 = createProductPreviewDto("Random Product 1", "random-product-1", BigDecimal.valueOf(99.99));
        ProductPreviewDto previewDto2 = createProductPreviewDto("Random Product 2", "random-product-2", BigDecimal.valueOf(149.99));

        when(productService.getRandomProductPreviews(Language.EN)).thenReturn(List.of(previewDto1, previewDto2));

        var result = productController.getRandomProducts(Language.EN);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSlug()).isEqualTo("random-product-1");
        assertThat(result.get(0).getImageUrls().getSmall()).isEqualTo("http://example.com/image-small.jpg");
        assertThat(result.get(1).getSlug()).isEqualTo("random-product-2");
        assertThat(result.get(1).getImageUrls().getSmall()).isEqualTo("http://example.com/image-small.jpg");

        verify(productService, times(1)).getRandomProductPreviews(Language.EN);
    }

    private static ProductPreviewDto createProductPreviewDto(String name, String slug, BigDecimal price) {
        return ProductPreviewDto.builder()
                .name(name)
                .slug(slug)
                .price(price)
                .imageUrls(ImageUrls.builder()
                        .small("http://example.com/image-small.jpg")
                        .medium("http://example.com/image-medium.jpg")
                        .large("http://example.com/image-large.jpg")
                        .build())
                .build();
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct_whenProductExists() throws IOException {
        String slug = "test-slug";
        MultipartFile[] mockFiles = new MultipartFile[] {
                createMockMultipartFile("image.jpg", "image content")
        };
        ProductDto updatedProductDto = ProductDto.builder()
                .slug(slug)
                .name("Updated Product")
                .build();

        when(productService.updateProduct(anyString(), any(ProductPayloadDto.class), aryEq(mockFiles))).thenReturn(updatedProductDto);

        ResponseEntity<ProductDto> response = productController.updateProduct(slug, productPayloadDtoJson, mockFiles);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedProductDto);

        verify(productService).updateProduct(anyString(), any(ProductPayloadDto.class), aryEq(mockFiles));
    }

    @Test
    void updateProduct_shouldReturnNotFound_whenProductDoesNotExist() throws IOException {
        String slug = "nonexistent-slug";
        MultipartFile[] mockFiles = new MultipartFile[] {
                createMockMultipartFile("image.jpg", "image content")
        };
        when(productService.updateProduct(anyString(), any(ProductPayloadDto.class), aryEq(mockFiles))).thenReturn(null);

        ResponseEntity<ProductDto> response = productController.updateProduct(slug, productPayloadDtoJson, mockFiles);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(productService).updateProduct(anyString(), any(ProductPayloadDto.class), aryEq(mockFiles));
    }

    @Test
    void getRandomProductsByCategory_withSlug_shouldReturnListOfProductPreviews() {
        String categorySlug = "health";
        ProductPreviewDto previewDto1 = createProductPreviewDto("Health Product 1", "health-product-1", BigDecimal.valueOf(59.99));
        ProductPreviewDto previewDto2 = createProductPreviewDto("Health Product 2", "health-product-2", BigDecimal.valueOf(79.99));

        when(productService.getRandomProductPreviewsByCategorySlug(categorySlug, Language.EN))
                .thenReturn(List.of(previewDto1, previewDto2));

        List<ProductPreviewDto> result = productController.getRandomProductsByCategory(categorySlug, Language.EN);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSlug()).isEqualTo("health-product-1");
        assertThat(result.get(0).getImageUrls().getSmall()).isEqualTo("http://example.com/image-small.jpg");
        assertThat(result.get(1).getSlug()).isEqualTo("health-product-2");
        assertThat(result.get(1).getImageUrls().getSmall()).isEqualTo("http://example.com/image-small.jpg");

        verify(productService, times(1)).getRandomProductPreviewsByCategorySlug(categorySlug, Language.EN);
    }

    @Test
    void getRandomProductsByCategory_withoutSlug_shouldReturnListOfRandomProductPreviews() {
        ProductPreviewDto previewDto1 = createProductPreviewDto("Random Product 1", "random-product-1", BigDecimal.valueOf(99.99));
        ProductPreviewDto previewDto2 = createProductPreviewDto("Random Product 2", "random-product-2", BigDecimal.valueOf(149.99));

        when(productService.getRandomProductPreviewsByCategorySlug(null, Language.EN))
                .thenReturn(List.of(previewDto1, previewDto2));

        List<ProductPreviewDto> result = productController.getRandomProductsByCategory(null, Language.EN);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSlug()).isEqualTo("random-product-1");
        assertThat(result.get(0).getImageUrls().getSmall()).isEqualTo("http://example.com/image-small.jpg");
        assertThat(result.get(1).getSlug()).isEqualTo("random-product-2");
        assertThat(result.get(1).getImageUrls().getSmall()).isEqualTo("http://example.com/image-small.jpg");

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

    private List<CategoryDto> buildCategoryDtos() {
        return List.of(
                CategoryDto.builder().name("Test Category").slug("test-category").build(),
                CategoryDto.builder().name("Bundle Category").slug("bundle-category").build()
        );
    }

    private static ProductImageDto createProductImageDto(String smallUrl, String mediumUrl, String largeUrl, boolean isMain) {
        return ProductImageDto.builder()
                .imageUrlSmall(smallUrl)
                .imageUrlMedium(mediumUrl)
                .imageUrlLarge(largeUrl)
                .main(isMain)
                .build();
    }

    private static ProductDto createProductDto(String name, String slug, BigDecimal price, int stock,
                                               String description, List<CategoryDto> categoryDtos, List<ProductImageDto> images) {
        return ProductDto.builder()
                .name(name)
                .slug(slug)
                .price(price)
                .stock(stock)
                .description(description)
                .categories(categoryDtos)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .images(images)
                .build();
    }

    private static String createCreateProductJson(String name, BigDecimal price, int stock,
                                                  String description, boolean active, List<String> categories) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ProductPayloadDto dto = ProductPayloadDto.builder()
                .price(price)
                .stock(stock)
                .active(active)
                .categories(categories)
                .mainImageIndex(0)
                .translation(ProductTranslationDto.builder()
                        .language(Language.EN)
                        .name(name)
                        .description(description)
                        .build())
                .build();

        return mapper.writeValueAsString(dto);

    }

}
