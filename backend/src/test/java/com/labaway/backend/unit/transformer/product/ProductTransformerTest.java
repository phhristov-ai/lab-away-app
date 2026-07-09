package com.labaway.backend.unit.transformer.product;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.product.media.ProductImageDto;
import com.labaway.backend.dto.product.main.*;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.category.CategoryTranslation;
import com.labaway.backend.entity.product.Product;
import com.labaway.backend.entity.product.ProductImage;
import com.labaway.backend.entity.product.ProductTranslation;
import com.labaway.backend.entity.repository.product.ProductPreviewProjection;
import com.labaway.backend.enums.Language;
import com.labaway.backend.transformer.category.CategoryTransformer;
import com.labaway.backend.transformer.product.ProductImageTransformer;
import com.labaway.backend.transformer.product.ProductTransformer;
import com.labaway.backend.util.JsonParsingUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductTransformerTest {

    private ProductTransformer productTransformer;
    @Mock
    private CategoryTransformer categoryTransformer;
    @Mock
    private ProductImageTransformer productImageTransformer;
    @Mock
    private JsonParsingUtils jsonParsingUtils;
    private final UUID imageId = UUID.randomUUID();

    private Product product;
    private ProductTranslation existingTranslation;
    private ProductTranslation newTranslation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productTransformer = new ProductTransformer(productImageTransformer, categoryTransformer, jsonParsingUtils);

        product = buildProductWithTranslations();
    }

    @Test
    void fromProjection_mapsCorrectly() {
        ProductPreviewProjection projection = mock(ProductPreviewProjection.class);
        when(projection.getName()).thenReturn("Product 1");
        when(projection.getSlug()).thenReturn("slug-1");
        when(projection.getPrice()).thenReturn(BigDecimal.valueOf(99.99));
        when(projection.getActive()).thenReturn(true);
        when(projection.getImageUrlSmall()).thenReturn("http://example.com/image-small.jpg");
        when(projection.getImageUrlMedium()).thenReturn("http://example.com/image-medium.jpg");
        when(projection.getImageUrlLarge()).thenReturn("http://example.com/image-large.jpg");
        when(projection.getCategories()).thenReturn("[{\"name\":\"Category1\",\"slug\":\"category1\"}]");

        when(jsonParsingUtils.parseCategoryList(projection.getCategories()))
                .thenReturn(List.of(createCategoryDto("Category1", "category1")));

        ProductPreviewDto dto = productTransformer.fromProjection(projection);

        assertThat(dto.name()).isEqualTo("Product 1");
        assertThat(dto.slug()).isEqualTo("slug-1");
        assertThat(dto.price()).isEqualTo(BigDecimal.valueOf(99.99));
        assertThat(dto.active()).isTrue();

        assertThat(dto.imageUrls()).isNotNull();
        assertThat(dto.imageUrls().small()).isEqualTo("http://example.com/image-small.jpg");
        assertThat(dto.imageUrls().medium()).isEqualTo("http://example.com/image-medium.jpg");
        assertThat(dto.imageUrls().large()).isEqualTo("http://example.com/image-large.jpg");

        assertThat(dto.categories()).hasSize(1);
        assertThat(dto.categories().get(0).name()).isEqualTo("Category1");
        assertThat(dto.categories().get(0).slug()).isEqualTo("category1");

        verify(jsonParsingUtils).parseCategoryList(projection.getCategories());
    }

    private CategoryDto createCategoryDto(String name, String slug) {
        return new CategoryDto(name, slug);
    }

    private Product buildProductWithTranslations() {
        Product product = new Product();
        product.setId(UUID.randomUUID());

        existingTranslation = new ProductTranslation();
        existingTranslation.setLanguage(Language.EN);
        existingTranslation.setName("Old Name");
        existingTranslation.setDescription("Old Description");

        product.setTranslations(new ArrayList<>(Collections.singletonList(existingTranslation)));

        return product;
    }

    @Test
    void shouldMapProductToDto() {
        Product product = buildProductWithCategories();
        ProductImageDto imageDto = buildProductImageDto();

        when(productImageTransformer.toDto(any(ProductImage.class))).thenReturn(imageDto);
        when(categoryTransformer.toDto(any(Category.class), eq(Language.EN)))
                .thenAnswer(invocation -> createSimpleCategoryDto(invocation.getArgument(0)));

        ProductDto dto = productTransformer.toDto(product, Language.EN);

        assertThat(dto).isNotNull();
        ProductTranslation translation = getTranslation(product, Language.EN);

        assertThat(translation).isNotNull();
        assertThat(dto.name()).isEqualTo(translation.getName());
        assertThat(dto.description()).isEqualTo(translation.getDescription());
        assertThat(dto.slug()).isEqualTo(product.getSlug());
        assertThat(dto.images()).hasSize(1);
        assertThat(dto.active()).isTrue();

        ProductImageDto dtoImage = dto.images().get(0);
        assertThat(dtoImage.imageUrlSmall()).isEqualTo(imageDto.imageUrlSmall());
        assertThat(dtoImage.imageUrlMedium()).isEqualTo(imageDto.imageUrlMedium());
        assertThat(dtoImage.imageUrlLarge()).isEqualTo(imageDto.imageUrlLarge());

        List<String> expectedSlugs = product.getCategories().stream()
                .map(Category::getSlug)
                .toList();

        List<String> actualSlugs = dto.categories().stream()
                .map(CategoryDto::slug)
                .toList();

        assertThat(actualSlugs).containsExactlyInAnyOrderElementsOf(expectedSlugs);
    }

    private CategoryDto createSimpleCategoryDto(Category category) {
        return new CategoryDto(
                "Test Category",
                category.getSlug()
        );
    }

    @Test
    void shouldMapFromCreateDto() {
        ProductPayloadDto productPayloadDto = buildCreateProductDto();
        Set<Category> categories = buildCategorySet();

        Product product = productTransformer.fromCreateDto(productPayloadDto, new ArrayList<>(categories));

        assertBasicProductFieldsMatch(productPayloadDto, product);
        assertProductCategoriesMatch(categories, product);
    }

    private Product buildProductWithCategories() {
        Product product = createBaseProduct();
        addTranslationsToProduct(product);
        addImagesToProduct(product);
        return product;
    }

    private void addImagesToProduct(Product product) {
        ProductImage image = buildProductImage();
        image.setProduct(product);
        product.getImages().add(image);
    }

    private Product createBaseProduct() {
        return Product.builder()
                .id(UUID.randomUUID())
                .slug("test-product")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .active(true)
                .categories(buildCategorySet())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private void addTranslationsToProduct(Product product) {
        ProductTranslation translation = ProductTranslation.builder()
                .product(product)
                .language(Language.EN)
                .name("Test Product")
                .description("Test product description")
                .build();
        product.getTranslations().add(translation);
    }

    private Set<Category> buildCategorySet() {
        Category category1 = buildCategory("electronics", "Electronics");
        Category category2 = buildCategory("health", "Health");

        return Set.of(category1, category2);
    }

    private Category buildCategory(String slug, String name) {
        Category category = Category.builder()
                .id(UUID.randomUUID())
                .slug(slug)
                .build();

        CategoryTranslation translation = buildTranslation(category, Language.EN, name);
        category.getTranslations().add(translation);

        return category;
    }

    private CategoryTranslation buildTranslation(Category category, Language language, String name) {
        return CategoryTranslation.builder()
                .id(UUID.randomUUID())
                .category(category)
                .language(language)
                .name(name)
                .build();
    }

    private ProductImage buildProductImage() {
        return ProductImage.builder()
                .id(imageId)
                .imageUrlSmall("http://example.com/image-small.jpg")
                .imageUrlMedium("http://example.com/image-medium.jpg")
                .imageUrlLarge("http://example.com/image-large.jpg")
                .build();
    }

    private ProductImageDto buildProductImageDto() {
        return new ProductImageDto(
                "http://example.com/image-small.jpg",
                "http://example.com/image-medium.jpg",
                "http://example.com/image-large.jpg",
                null
        );
    }

    private ProductPayloadDto buildCreateProductDto() {
        return new ProductPayloadDto(
                BigDecimal.valueOf(50),
                5,
                true,
                List.of("electronics", "health"),
                0,
                buildTranslationDto(Language.EN),
                List.of()
        );
    }

    private ProductTranslationDto buildTranslationDto(Language language) {
        return new ProductTranslationDto(
                language,
                "New Product",
                "New product description"
        );
    }

    private void assertBasicProductFieldsMatch(ProductPayloadDto dto, Product product) {
        assertThat(product).isNotNull();
        assertThat(product.getPrice()).isEqualTo(dto.price());
        assertThat(product.getStock()).isEqualTo(dto.stock());
        assertThat(product.isActive()).isEqualTo(dto.active());

        ProductTranslation translation = getTranslation(product, dto.translation().language());
        assertThat(translation).isNotNull();
    }

    private ProductTranslation getTranslation(Product product, Language language) {
        return product.getTranslations().stream()
                .filter(t -> t.getLanguage() == language)
                .findFirst()
                .orElse(null);
    }


    private void assertProductCategoriesMatch(Set<Category> expectedCategories, Product product) {
        assertThat(product.getCategories()).containsExactlyInAnyOrderElementsOf(expectedCategories);
    }
}