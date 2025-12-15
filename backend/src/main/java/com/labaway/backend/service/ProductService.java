package com.labaway.backend.service;

import com.labaway.backend.dto.image.ImageUrls;
import com.labaway.backend.dto.product.image.ProductImageDto;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductTransformer productTransformer;
    private final ProductImageRepository productImageRepository;
    private final S3Service s3Service;

    private final ImageService imageService;

    public ProductDto createProduct(ProductPayloadDto productPayloadDto, MultipartFile[] files) throws IOException {
        List<ProductImage> productImages = processImageFiles(files, productPayloadDto.getMainImageIndex());
        Product savedProduct = prepareAndSaveProduct(productPayloadDto, productImages);
        return productTransformer.toDto(savedProduct, productPayloadDto.getTranslation().getLanguage());
    }

    @Transactional
    public ProductDto updateProduct(String slug, ProductPayloadDto dto, MultipartFile[] files) throws IOException {
        Product product = findProductBySlug(slug);

        updateProductImages(product, dto, files);

        List<Category> categories = resolveCategories(dto);
        productTransformer.updateEntity(product, dto, categories);

        Product savedProduct = productRepository.save(product);
        return productTransformer.toDto(savedProduct, dto.getTranslation().getLanguage());
    }

    private Product findProductBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .orElseThrow(() -> new ProductNotFoundException("Product with slug '" + slug + "' not found"));
    }

    private void updateProductImages(Product product, ProductPayloadDto dto, MultipartFile[] files) throws IOException {
        Set<String> retainedUrls = extractRetainedImageKeys(dto);

        deleteRemovedImages(product, retainedUrls);
        uploadNewImages(product, dto.getMainImageIndex(), files);
        updateMainImageFlag(product, dto.getMainImageIndex());
    }

    private void updateMainImageFlag(Product product, Integer mainImageIndex) {
        List<ProductImage> images = product.getImages();

        if (mainImageIndex == null || mainImageIndex < 0 || mainImageIndex >= images.size()) {
            for (int i = 0; i < images.size(); i++) {
                images.get(i).setMain(i == 0);
            }
            return;
        }

        for (int i = 0; i < images.size(); i++) {
            images.get(i).setMain(i == mainImageIndex);
        }
    }

    private Set<String> extractRetainedImageKeys(ProductPayloadDto dto) {
        if (dto.getImages() == null) {
            return Collections.emptySet();
        }

        return dto.getImages().stream()
                .map(ProductImageDto::getImageUrlLarge)
                .collect(Collectors.toSet());
    }


    private void deleteRemovedImages(Product product, Set<String> retainedUrls) {
        List<ProductImage> toRemove = product.getImages().stream()
                .filter(image -> !retainedUrls.contains(image.getImageUrlSmall())
                        && !retainedUrls.contains(image.getImageUrlMedium())
                        && !retainedUrls.contains(image.getImageUrlLarge()))
                .collect(Collectors.toList());

        for (ProductImage image : toRemove) {
            try {
                if (image.getImageUrlSmall() != null && !image.getImageUrlSmall().isEmpty()) {
                    s3Service.deleteFile(image.getImageUrlSmall());
                }
                if (image.getImageUrlMedium() != null && !image.getImageUrlMedium().isEmpty()) {
                    s3Service.deleteFile(image.getImageUrlMedium());
                }
                if (image.getImageUrlLarge() != null && !image.getImageUrlLarge().isEmpty()) {
                    s3Service.deleteFile(image.getImageUrlLarge());
                }
            } catch (Exception e) {
                System.err.println("Failed to delete S3 file: " + image + " - " + e.getMessage());
            }
        }

        product.getImages().removeAll(toRemove);
    }

    private void uploadNewImages(Product product, Integer mainImageIndex, MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            return;
        }

        List<ProductImage> newImages = processImageFiles(files, mainImageIndex);
        for (ProductImage image : newImages) {
            image.setProduct(product);
        }
        product.getImages().addAll(newImages);
    }

    private List<Category> resolveCategories(ProductPayloadDto dto) {
        List<Category> categories = categoryRepository.findBySlugInAndLanguage(
                dto.getCategories(), dto.getTranslation().getLanguage());

        if (categories.size() != dto.getCategories().size()) {
            throw new CategoryNotFoundException("One or more categories not found for language: " + dto.getTranslation().getLanguage());
        }

        return categories;
    }

    private Product prepareAndSaveProduct(ProductPayloadDto productPayloadDto, List<ProductImage> productImages) {
        Language language = productPayloadDto.getTranslation().getLanguage();
        List<Category> categories = categoryRepository.findBySlugInAndLanguage(productPayloadDto.getCategories(), language);

        if (categories.size() != productPayloadDto.getCategories().size()) {
            throw new CategoryNotFoundException("One or more categories not found for language: " + language);
        }

        Product product = productTransformer.fromCreateDto(productPayloadDto, categories);

        for (ProductImage image : productImages) {
            image.setProduct(product);
        }

        product.setImages(productImages);

        return productRepository.save(product);
    }

    private void deleteOldImages(Product product) {
        if (product.getImages() == null) return;

        for (ProductImage oldImage : product.getImages()) {
            try {
                if (oldImage.getImageUrlSmall() != null && !oldImage.getImageUrlSmall().isEmpty()) {
                    s3Service.deleteFile(oldImage.getImageUrlSmall());
                }
                if (oldImage.getImageUrlMedium() != null && !oldImage.getImageUrlMedium().isEmpty()) {
                    s3Service.deleteFile(oldImage.getImageUrlMedium());
                }
                if (oldImage.getImageUrlLarge() != null && !oldImage.getImageUrlLarge().isEmpty()) {
                    s3Service.deleteFile(oldImage.getImageUrlLarge());
                }
            } catch (Exception e) {
                System.err.println("Failed to delete S3 file: " + oldImage + " - " + e.getMessage());
            }
        }

        productImageRepository.deleteAllByProductId(product.getId());
    }


    private List<ProductImage> processImageFiles(MultipartFile[] files, Integer mainIndex) throws IOException {
        List<ProductImage> productImages = new ArrayList<>();
        if (files == null || files.length == 0) {
            return productImages;
        }

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            if (file != null && !file.isEmpty()) {
                ImageUrls imageUrls = imageService.processAndUploadImage(file);

                ProductImage productImage = ProductImage.builder()
                        .imageUrlSmall(imageUrls.getSmall())
                        .imageUrlMedium(imageUrls.getMedium())
                        .imageUrlLarge(imageUrls.getLarge())
                        .main(i == mainIndex)
                        .build();

                productImages.add(productImage);
            }
        }

        return productImages;
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
        Product product = findProductBySlug(slug);

        return productTransformer.toDto(product, lang);
    }

    @Transactional
    public void deleteProductBySlug(String slug) {
        Product product = findProductBySlug(slug);

        deleteOldImages(product);
        productRepository.delete(product);
    }
}