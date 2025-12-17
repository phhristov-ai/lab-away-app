package com.labaway.backend.transformer.product;

import com.labaway.backend.dto.product.image.ProductImageDto;
import com.labaway.backend.entity.product.Product;
import com.labaway.backend.entity.product.ProductImage;
import com.labaway.backend.entity.product.ProductTranslation;
import com.labaway.backend.enums.Language;
import com.labaway.backend.transformer.product.ProductImageTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductImageTransformerTest {

    private ProductImageTransformer transformer;
    private Product product;

    @BeforeEach
    void setUp() {
        transformer = new ProductImageTransformer();
        product = createSampleProduct();
    }

    @Test
    void shouldMapEntityToDto() {
        ProductImage image = createSampleImageEntity();

        ProductImageDto dto = transformer.toDto(image);

        assertThat(dto).isNotNull();
        assertThat(dto.getImageUrlSmall()).isEqualTo("http://example.com/image-small.jpg");
        assertThat(dto.getImageUrlMedium()).isEqualTo("http://example.com/image-medium.jpg");
        assertThat(dto.getImageUrlLarge()).isEqualTo("http://example.com/image-large.jpg");
        assertThat(dto.isMain()).isTrue();
    }

    private Product createSampleProduct() {
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .slug("test-product")
                .price(BigDecimal.valueOf(100))
                .stock(5)
                .active(true)
                .build();

        ProductTranslation translation = createTranslation(product, Language.EN, "Test Product", "Test product description");
        product.getTranslations().add(translation);

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

    private ProductImage createSampleImageEntity() {
        return ProductImage.builder()
                .imageUrlSmall("http://example.com/image-small.jpg")
                .imageUrlMedium("http://example.com/image-medium.jpg")
                .imageUrlLarge("http://example.com/image-large.jpg")
                .main(true)
                .product(product)
                .build();
    }
}

