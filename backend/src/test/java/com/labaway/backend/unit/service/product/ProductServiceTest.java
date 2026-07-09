package com.labaway.backend.service.product;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.image.ImageUrls;
import com.labaway.backend.dto.product.media.ProductImageDto;
import com.labaway.backend.dto.product.main.*;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.product.Product;
import com.labaway.backend.entity.product.ProductTranslation;
import com.labaway.backend.entity.repository.product.ProductPreviewProjection;
import com.labaway.backend.enums.Language;
import com.labaway.backend.exception.CategoryNotFoundException;
import com.labaway.backend.exception.ProductNotFoundException;
import com.labaway.backend.entity.repository.category.CategoryRepository;
import com.labaway.backend.entity.repository.product.ProductImageRepository;
import com.labaway.backend.entity.repository.product.ProductRepository;
import com.labaway.backend.service.media.MediaService;
import com.labaway.backend.service.storage.S3Service;
import com.labaway.backend.transformer.product.ProductTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock private ProductRepository productRepository;
    @Mock private ProductImageRepository productImageRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductTransformer productTransformer;
    @Mock private S3Service s3Service;
    @Mock private MediaService mediaService;
    private final UUID productId = UUID.randomUUID();
    private static final String LANGUAGE_EN_CODE = "EN";
    private final String categorySlug1 = "test-cat-1";
    private final String categorySlug2 = "test-cat-2";

    private static final String CATEGORIES_JSON = """
    [
      {"name":"Category 1","slug":"test-cat-1"},
      {"name":"Category 2","slug":"test-cat-2"}
    ]
    """;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateProductSuccessfully() throws IOException {

        ProductPayloadDto createDto = createSampleCreateDto();
        List<Category> categories = createSampleCategories();
        Product product = createSampleProduct();
        Product savedProduct = createSampleSavedProduct("test-product");

        ProductImageDto imageDto = ProductImageDto.builder()
                .imageUrlSmall("http://example.com/image-small.jpg")
                .imageUrlMedium("http://example.com/image-medium.jpg")
                .imageUrlLarge("http://example.com/image-large.jpg")
                .build();

        ImageUrls imageUrls = new ImageUrls(
                imageDto.getImageUrlSmall(),
                imageDto.getImageUrlMedium(),
                imageDto.getImageUrlLarge()
        );

        MultipartFile[] mockFiles = new MultipartFile[] {
                createMockMultipartFile("image.jpg", "image content")
        };

        MultipartFile mockBanner =
                createMockMultipartFile("banner.mp4", "banner content");

        when(mediaService.processAndUploadImage(any(MultipartFile.class)))
                .thenReturn(imageUrls);

        when(categoryRepository.findBySlugInAndLanguage(
                createDto.getCategories(),
                Language.EN
        )).thenReturn(categories);

        when(productTransformer.fromCreateDto(createDto, categories))
                .thenReturn(product);

        when(productRepository.save(product))
                .thenReturn(savedProduct);

        when(productTransformer.toDto(savedProduct, Language.EN))
                .thenReturn(createSampleProductDtoWithImages(
                        "test-product",
                        List.of(imageDto)
                ));

        ProductDto result = productService.createProduct(
                createDto,
                mockFiles,
                mockBanner
        );

        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("test-product");
        assertThat(result.getImages()).hasSize(1);
        assertThat(result.getImages().get(0).getImageUrlSmall())
                .isEqualTo("http://example.com/image-small.jpg");
        assertThat(result.isActive()).isTrue();

        verify(categoryRepository)
                .findBySlugInAndLanguage(createDto.getCategories(), Language.EN);

        verify(productRepository).save(product);

        verify(mediaService, times(1))
                .processAndUploadImage(any(MultipartFile.class));
    }

    private ProductDto createSampleProductDtoWithImages(String slug, List<ProductImageDto> images) {
        return ProductDto.builder()
                .slug(slug)
                .name("Sample Product")
                .description("Sample description")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .images(images)
                .active(true)
                .categories(new ArrayList<>())
                .build();
    }

    @Test
    void shouldUpdateProductSuccessfully() throws IOException {
        ProductPayloadDto createDto = createSampleCreateDto();
        String slug = "test-product";

        ImageUrls sampleImageUrls = ImageUrls.builder()
                .small("http://example.com/image-small.jpg")
                .medium("http://example.com/image-medium.jpg")
                .large("http://example.com/image-large.jpg")
                .build();

        when(mediaService.processAndUploadImage(any(MultipartFile.class)))
                .thenReturn(sampleImageUrls);

        Product product = createSampleSavedProduct(slug);
        List<Category> categories = createSampleCategories();

        ProductDto expectedDto = createSampleProductDto(slug);
        expectedDto.setName(createDto.getTranslation().getName());

        MultipartFile[] mockFiles = new MultipartFile[] {
                createMockMultipartFile("image.jpg", "image content")
        };

        MultipartFile mockBanner = createMockMultipartFile("banner.mp4", "banner content");

        mockFindProductAndCategories(slug, createDto.getCategories(), product, categories);

        mockSaveAndTransform(product, expectedDto);

        when(productTransformer.fromCreateDto(any(), any())).thenReturn(product);

        ProductDto result = productService.updateProduct(slug, createDto, mockFiles, mockBanner);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(createDto.getTranslation().getName());

        verifyInteractions(slug, createDto.getCategories(), product);

        verify(productRepository).save(product);

        verify(mediaService, times(mockFiles.length)).processAndUploadImage(any(MultipartFile.class));

        verify(mediaService).uploadBanner(mockBanner);
    }

    private MockMultipartFile createMockMultipartFile(String filename, String content) {
        return new MockMultipartFile(
                "file",
                filename,
                MediaType.IMAGE_JPEG_VALUE,
                content.getBytes(StandardCharsets.UTF_8)
        );
    }

    private void mockFindProductAndCategories(String slug, List<String> categorySlugs, Product product, List<Category> categories) {
        when(productRepository.findBySlug(slug)).thenReturn(Optional.of(product));
        when(categoryRepository.findBySlugInAndLanguage(categorySlugs, Language.EN)).thenReturn(categories);
    }

    private void mockSaveAndTransform(Product product, ProductDto expectedDto) {
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productTransformer.toDto(product, Language.EN)).thenReturn(expectedDto);
    }

    private void verifyInteractions(String slug, List<String> categorySlugs, Product product) {
        verify(productRepository).findBySlug(slug);
        verify(categoryRepository).findBySlugInAndLanguage(categorySlugs, Language.EN);
        verify(productRepository).save(product);
        verify(productTransformer).toDto(product, Language.EN);
    }


    @Test
    void shouldReturnProductBySlugWithSharedContentAndFaqs() {
        String slug = "test-product";
        Product product = createSampleSavedProduct(slug);
        ProductDto expectedDto = createSampleProductDto(slug);

        when(productRepository.findBySlug(slug)).thenReturn(Optional.of(product));
        when(productTransformer.toDto(product, Language.EN)).thenReturn(expectedDto);

        ProductDto result = productService.getProductBySlug(slug, Language.EN);

        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo(slug);
    }

    @Test
    void testGetAllProductPreviews() {
        List<ProductPreviewProjection> projections = List.of(
                createMockProjection("Product 1", "slug-1"),
                createMockProjection("Product 2", "slug-2")
        );

        when(productRepository.findAllProductPreviewsByLanguage("EN")).thenReturn(projections);

        when(productTransformer.fromProjection(any(ProductPreviewProjection.class)))
                .thenAnswer(invocation -> buildProductPreviewDtoFromProjection(invocation.getArgument(0)));
        List<ProductPreviewDto> result = productService.getAllProductPreviews(Language.EN);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSlug()).isEqualTo("slug-1");
        assertThat(result.get(0).isActive()).isTrue();
        assertThat(result.get(1).getSlug()).isEqualTo("slug-2");
        assertThat(result.get(1).isActive()).isTrue();

        verify(productRepository).findAllProductPreviewsByLanguage("EN");
    }

    @Test
    void testGetRandomProductPreviews() {
        List<ProductPreviewProjection> projections = List.of(
                createMockProjection("Product 1", "slug-1"),
                createMockProjection("Product 2", "slug-2")
        );

        when(productRepository.findRandomProductPreviewsByLanguage(LANGUAGE_EN_CODE)).thenReturn(projections);
        when(productTransformer.fromProjection(any(ProductPreviewProjection.class)))
                .thenAnswer(invocation -> buildProductPreviewDtoFromProjection(invocation.getArgument(0)));

        List<ProductPreviewDto> result = productService.getRandomProductPreviews(Language.EN);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSlug()).isEqualTo("slug-1");
        assertThat(result.get(0).isActive()).isTrue();
        assertThat(result.get(1).getSlug()).isEqualTo("slug-2");
        assertThat(result.get(1).isActive()).isTrue();

        verify(productRepository).findRandomProductPreviewsByLanguage(LANGUAGE_EN_CODE);
    }

    @Test
    void getRandomProductPreviewsByCategorySlug_shouldReturnProducts_whenSlugExists() {
        String slug = "wellness";
        List<ProductPreviewProjection> projections = List.of(
                createMockProjection("Product 1", "slug-1"),
                createMockProjection("Product 2", "slug-2")
        );

        when(categoryRepository.existsBySlug(slug)).thenReturn(true);
        when(productRepository.findRandomByLanguageAndCategory(LANGUAGE_EN_CODE, slug)).thenReturn(projections);
        when(productTransformer.fromProjection(any(ProductPreviewProjection.class)))
                .thenAnswer(invocation -> buildProductPreviewDtoFromProjection(invocation.getArgument(0)));

        List<ProductPreviewDto> result = productService.getRandomProductPreviewsByCategorySlug(slug, Language.EN);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSlug()).isEqualTo("slug-1");
        assertThat(result.get(0).isActive()).isTrue();
        assertThat(result.get(1).getSlug()).isEqualTo("slug-2");
        assertThat(result.get(1).isActive()).isTrue();

        verify(categoryRepository).existsBySlug(slug);
        verify(productRepository).findRandomByLanguageAndCategory(LANGUAGE_EN_CODE, slug);
    }

    @Test
    void getRandomProductPreviewsByCategorySlug_shouldFallbackToGenericRandom_whenSlugIsNull() {
        List<ProductPreviewProjection> projections = List.of(
                createMockProjection("Product A", "random-1"),
                createMockProjection("Product B", "random-2")
        );

        when(productRepository.findRandomProductPreviewsByLanguage(LANGUAGE_EN_CODE)).thenReturn(projections);
        when(productTransformer.fromProjection(any(ProductPreviewProjection.class)))
                .thenAnswer(invocation -> buildProductPreviewDtoFromProjection(invocation.getArgument(0)));

        List<ProductPreviewDto> result = productService.getRandomProductPreviewsByCategorySlug(null, Language.EN);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSlug()).isEqualTo("random-1");
        assertThat(result.get(0).isActive()).isTrue();
        assertThat(result.get(1).getSlug()).isEqualTo("random-2");
        assertThat(result.get(1).isActive()).isTrue();

        verify(productRepository).findRandomProductPreviewsByLanguage(LANGUAGE_EN_CODE);
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void getRandomProductPreviewsByCategorySlug_shouldThrowException_whenSlugInvalid() {
        String invalidSlug = "unknown-category";

        when(categoryRepository.existsBySlug(invalidSlug)).thenReturn(false);

        assertThatThrownBy(() -> productService.getRandomProductPreviewsByCategorySlug(invalidSlug, Language.EN))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category not found with slug: " + invalidSlug);

        verify(categoryRepository).existsBySlug(invalidSlug);
        verifyNoMoreInteractions(productRepository);
    }

    private ProductPreviewDto buildProductPreviewDtoFromProjection(ProductPreviewProjection proj) {
        return ProductPreviewDto.builder()
                .name(proj.getName())
                .slug(proj.getSlug())
                .price(proj.getPrice())
                .active(proj.getActive())
                .imageUrls(mapImageUrls(proj.getImageUrlSmall(), proj.getImageUrlMedium(), proj.getImageUrlLarge()))
                .categories(createCategoryDtos())
                .build();
    }

    private ImageUrls mapImageUrls(String small, String medium, String large) {
        return ImageUrls.builder()
                .small(small)
                .medium(medium)
                .large(large)
                .build();
    }


    private List<CategoryDto> createCategoryDtos() {
        return List.of(
                CategoryDto.builder().name("Category 1").slug("test-cat-1").build(),
                CategoryDto.builder().name("Category 2").slug("test-cat-2").build()
        );
    }

    @Test
    void shouldReturnRandomProductPreviews() {
        List<ProductPreviewProjection> projections = List.of(
                createMockProjection("Product 1", "slug-1"),
                createMockProjection("Product 2", "slug-2")
        );

        when(productRepository.findAllProductPreviewsByLanguage(LANGUAGE_EN_CODE)).thenReturn(projections);

        when(productTransformer.fromProjection(any())).thenAnswer(invocation -> {
            ProductPreviewProjection proj = invocation.getArgument(0);
            return buildProductPreviewDtoFromProjection(proj);
        });

        List<ProductPreviewDto> result = productService.getAllProductPreviews(Language.EN);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSlug()).isEqualTo("slug-1");
        assertThat(result.get(1).getSlug()).isEqualTo("slug-2");

        verify(productRepository).findAllProductPreviewsByLanguage(LANGUAGE_EN_CODE);
    }

    private ProductPreviewProjection createMockProjection(String name, String slug) {
        ProductPreviewProjection mockProjection = mock(ProductPreviewProjection.class);
        when(mockProjection.getName()).thenReturn(name);
        when(mockProjection.getSlug()).thenReturn(slug);
        when(mockProjection.getPrice()).thenReturn(BigDecimal.valueOf(99.99));
        when(mockProjection.getActive()).thenReturn(true);

        when(mockProjection.getImageUrlSmall()).thenReturn("http://example.com/image-small.jpg");
        when(mockProjection.getImageUrlMedium()).thenReturn("http://example.com/image-medium.jpg");
        when(mockProjection.getImageUrlLarge()).thenReturn("http://example.com/image-large.jpg");

        when(mockProjection.getCategories()).thenReturn(CATEGORIES_JSON);
        return mockProjection;
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        String slug = "test-product";
        Product product = createSampleSavedProduct(slug);

        when(productRepository.findBySlug(slug)).thenReturn(Optional.of(product));

        productService.deleteProductBySlug(slug);

        verify(productImageRepository).deleteAllByProductId(product.getId());
        verify(productRepository).delete(product);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentProduct() {
        when(productRepository.findBySlug("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProductBySlug("missing"))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product with slug 'missing' not found");
    }

    private ProductPayloadDto createSampleCreateDto() {
        ProductTranslationDto productTranslationDto = ProductTranslationDto.builder()
                .language(Language.EN)
                .description("Description")
                .name("Product Name")
                .build();
        return ProductPayloadDto.builder()
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .active(true)
                .categories(List.of(categorySlug1, categorySlug2))
                .translation(productTranslationDto)
                .build();
    }

    private List<Category> createSampleCategories() {
        Category cat1 = Category.builder()
                .id(UUID.randomUUID())
                .slug("test-cat-1")
                .build();

        Category cat2 = Category.builder()
                .id(UUID.randomUUID())
                .slug("test-cat-2")
                .build();

        return List.of(cat1, cat2);
    }

    private Product createSampleProduct() {
        Product product = Product.builder()
                .slug("test-product")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .active(true)
                .images(new ArrayList<>())
                .build();

        product.getTranslations().add(createTranslation(product, Language.EN, "Test Product", "Test description"));
        return product;
    }


    private Product createSampleSavedProduct(String slug) {
        Product product = Product.builder()
                .id(productId)
                .slug(slug)
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .active(true)
                .images(new ArrayList<>())
                .build();

        product.getTranslations().add(createTranslation(product, Language.EN, "Test Product", "Test description"));
        return product;
    }

    private ProductTranslation createTranslation(Product product, Language language, String name, String description) {
        return ProductTranslation.builder()
                .product(product)
                .language(language)
                .name(name)
                .description(description)
                .build();
    }

    private ProductDto createSampleProductDto(String slug) {
        return ProductDto.builder().name("Test Product").slug(slug).build();
    }

}
