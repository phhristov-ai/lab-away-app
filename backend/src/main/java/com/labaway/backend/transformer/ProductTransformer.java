package com.labaway.backend.transformer;

import com.labaway.backend.dto.product.main.*;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.product.Product;
import com.labaway.backend.entity.product.ProductTranslation;
import com.labaway.backend.entity.repository.ProductPreviewProjection;
import com.labaway.backend.enums.Language;
import com.labaway.backend.util.JsonParsingUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ProductTransformer {
    private final ProductImageTransformer productImageTransformer;
    private final CategoryTransformer categoryTransformer;
    private final JsonParsingUtils jsonParsingUtils;

    public ProductDto toDto(Product product, Language language) {
        ProductTranslation translation = getTranslation(product, language);

        return ProductDto.builder()
                .name(translation.getName())
                .slug(product.getSlug())
                .price(product.getPrice())
                .stock(product.getStock())
                .description(translation.getDescription())
                .categories(product.getCategories().stream()
                        .map(category -> categoryTransformer.toDto(category, language))
                        .toList())
                .createdAt(product.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .updatedAt(product.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .images(product.getImages().stream()
                        .map(productImageTransformer::toDto)
                        .toList())
                .build();
    }

    public Product fromCreateDto(ProductPayloadDto dto, List<Category> categories) {
        Product product = Product.builder()
                .slug(generateSlug(dto.getTranslation().getName()))
                .price(dto.getPrice())
                .stock(dto.getStock())
                .active(dto.isActive())
                .categories(new HashSet<>(categories))
                .build();

        product.getTranslations().add(buildProductTranslation(dto.getTranslation(), product));

        return product;
    }

    public void updateEntity(Product product, ProductPayloadDto dto, List<Category> categories) {
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setActive(dto.isActive());
        product.setCategories(new HashSet<>(categories));

        ProductTranslationDto translationDto = dto.getTranslation();
        Language language = translationDto.getLanguage();

        Optional<ProductTranslation> existingTranslationOpt = product.getTranslations().stream()
                .filter(t -> t.getLanguage() == language)
                .findFirst();

        if (existingTranslationOpt.isPresent()) {
            ProductTranslation existing = existingTranslationOpt.get();
            existing.setName(translationDto.getName());
            existing.setDescription(translationDto.getDescription());
        } else {
            ProductTranslation newTranslation = buildProductTranslation(translationDto, product);
            product.getTranslations().add(newTranslation);
        }
    }

    private String generateSlug(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }

    private ProductTranslation buildProductTranslation(ProductTranslationDto translationDto, Product product) {
        ProductTranslation translation = new ProductTranslation();
        translation.setLanguage(translationDto.getLanguage());
        translation.setName(translationDto.getName());
        translation.setDescription(translationDto.getDescription());
        translation.setProduct(product);
        return translation;
    }

    private ProductTranslation getTranslation(Product product, Language language) {
        return product.getTranslations().stream()
                .filter(t -> t.getLanguage() == language)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "No translation found for language: " + language));
    }

    public ProductPreviewDto fromProjection(ProductPreviewProjection projection) {
        return ProductPreviewDto.builder()
                .name(projection.getName())
                .slug(projection.getSlug())
                .price(projection.getPrice())
                .thumbnailUrl(projection.getThumbnailUrl())
                .categories(jsonParsingUtils.parseCategoryList(projection.getCategories()))
                .build();
    }

}