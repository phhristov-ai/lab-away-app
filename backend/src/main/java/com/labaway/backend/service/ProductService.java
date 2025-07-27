package com.labaway.backend.service;

import com.labaway.backend.dto.product.main.*;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.product.Product;
import com.labaway.backend.entity.product.ProductImage;
import com.labaway.backend.enums.Language;
import com.labaway.backend.exception.CategoryNotFoundException;
import com.labaway.backend.exception.ProductNotFoundException;
import com.labaway.backend.exception.ResourceNotFoundException;
import com.labaway.backend.entity.repository.CategoryRepository;
import com.labaway.backend.entity.repository.ProductImageRepository;
import com.labaway.backend.entity.repository.ProductRepository;
import com.labaway.backend.transformer.ProductTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductTransformer productTransformer;
    private final ProductImageRepository productImageRepository;
    private final S3Service s3Service;

    public ProductDto createProduct(ProductPayloadDto productPayloadDto, MultipartFile[] files) throws IOException {
        List<ProductImage> productImages = processImageFiles(files, productPayloadDto.getMainImageIndex());
        Product savedProduct = prepareAndSaveProduct(productPayloadDto, productImages);
        return productTransformer.toDto(savedProduct, productPayloadDto.getTranslation().getLanguage());
    }

    @Transactional
    public ProductDto updateProduct(String slug, ProductPayloadDto dto, MultipartFile[] files) throws IOException {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ProductNotFoundException("Product with slug '" + slug + "' not found"));

        if (files != null && files.length > 0) {
            product.getImages().clear();
            deleteOldImages(product);

            List<ProductImage> productImages = processImageFiles(files, dto.getMainImageIndex());

            for (ProductImage image : productImages) {
                image.setProduct(product);
            }

            product.getImages().addAll(productImages);
        }

        List<Category> categories = categoryRepository.findBySlugInAndLanguage(dto.getCategories(), dto.getTranslation().getLanguage());
        if (categories.size() != dto.getCategories().size()) {
            throw new CategoryNotFoundException("One or more categories not found for language: " + dto.getTranslation().getLanguage());
        }

        productTransformer.updateEntity(product, dto, categories);

        Product savedProduct = productRepository.save(product);
        return productTransformer.toDto(savedProduct, dto.getTranslation().getLanguage());
    }


    private Product prepareAndSaveProduct(ProductPayloadDto productPayloadDto, List<ProductImage> productImages) {
        Language language = productPayloadDto.getTranslation().getLanguage();
        List<Category> categories = categoryRepository.findBySlugInAndLanguage(productPayloadDto.getCategories(), language);

        if (categories.size() != productPayloadDto.getCategories().size()) {
            throw new CategoryNotFoundException("One or more categories not found for language: " + language);
        }

        Product product = productTransformer.fromCreateDto(productPayloadDto, categories);
        product.setImages(productImages);
        return productRepository.save(product);
    }


    private void deleteOldImages(Product product) {
        for (ProductImage oldImage : product.getImages()) {
            try {
                s3Service.deleteFile(oldImage.getImageUrl());
            } catch (Exception e) {
                System.err.println("Failed to delete S3 file: " + oldImage.getImageUrl() + " - " + e.getMessage());
            }
        }
        productImageRepository.deleteAllByProductId(product.getId());
    }

    private List<ProductImage> processImageFiles(MultipartFile[] files, Integer mainIndex) throws IOException {
        List<ProductImage> productImages = new ArrayList<>();
        if (files == null || mainIndex == null) {
            return productImages;
        }

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            if (file != null && !file.isEmpty()) {
                String imageUrl = s3Service.uploadFile(file);
                productImages.add(ProductImage.builder()
                        .imageUrl(imageUrl)
                        .main(i == mainIndex)
                        .build());
            }
        }

        return productImages;
    }


    private Set<Category> resolveCategories(List<String> categorySlugs, Set<Category> currentCategories) {
        if (categorySlugs == null || categorySlugs.isEmpty()) {
            return currentCategories;
        }
        List<Category> foundCategories = categoryRepository.findBySlugInAndLanguage(categorySlugs, Language.EN);
        if (foundCategories.size() != categorySlugs.size()) {
            throw new CategoryNotFoundException("Some categories not found");
        }
        return new HashSet<>(foundCategories);
    }

    public List<ProductPreviewDto> getAllProductPreviews(Language lang) {
        return productRepository.findAllProductPreviewsByLanguage(lang.name()).stream()
                .map(productTransformer::fromProjection)
                .toList();
    }

    public List<ProductPreviewDto> getRandomProductPreviewsByCategorySlug(String slug, Language lang) {
        if (slug == null || slug.isEmpty()) {
            return getRandomProductPreviews(lang);
        }

        if (!categoryRepository.existsBySlug(slug)) {
            throw new CategoryNotFoundException("Category not found with slug: " + slug);
        }

        return productRepository.findRandomByLanguageAndCategory(lang.name(), slug).stream()
                .map(productTransformer::fromProjection)
                .toList();
    }

    public List<ProductPreviewDto> getRandomProductPreviews(Language lang) {
        return productRepository.findRandomProductPreviewsByLanguage(lang.name()).stream()
                .map(productTransformer::fromProjection)
                .toList();
    }

    public ProductDto getProductBySlug(String slug, Language lang) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return productTransformer.toDto(product, lang);
    }

    @Transactional
    public void deleteProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug " + slug));

        deleteOldImages(product);
        productRepository.delete(product);
    }
}