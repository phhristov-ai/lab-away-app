package com.labaway.backend.service.product;

import com.labaway.backend.dto.image.ImageUrls;
import com.labaway.backend.dto.product.media.ProductImageDto;
import com.labaway.backend.dto.product.main.*;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.product.Product;
import com.labaway.backend.entity.product.ProductImage;
import com.labaway.backend.enums.BannerType;
import com.labaway.backend.enums.Language;
import com.labaway.backend.event.BannerReplacedEvent;
import com.labaway.backend.exception.CategoryNotFoundException;
import com.labaway.backend.exception.ProductNotFoundException;
import com.labaway.backend.entity.repository.category.CategoryRepository;
import com.labaway.backend.entity.repository.product.ProductImageRepository;
import com.labaway.backend.entity.repository.product.ProductRepository;
import com.labaway.backend.service.media.MediaService;
import com.labaway.backend.service.model.BannerData;
import com.labaway.backend.service.storage.S3Service;
import com.labaway.backend.transformer.product.ProductTransformer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final MediaService mediaService;
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log =
            LoggerFactory.getLogger(ProductService.class);

    public ProductDto createProduct(ProductPayloadDto productPayloadDto, MultipartFile[] files, MultipartFile banner)  {
        List<ProductImage> productImages = processImageFiles(files);
        BannerData bannerData = processBanner(banner);
        Product savedProduct = prepareAndSaveProduct(productPayloadDto, productImages, bannerData);
        return productTransformer.toDto(savedProduct, productPayloadDto.translation().language());
    }

    @Transactional
    public ProductDto updateProduct(
            String slug,
            ProductPayloadDto dto,
            MultipartFile[] files,
            MultipartFile banner
    ) {

        Product product = findProductBySlug(slug);

        String oldBannerUrl = product.getBannerUrl();

        updateProductImages(product, dto, files);

        List<Category> categories = resolveCategories(dto);
        productTransformer.updateEntity(product, dto, categories);

        BannerData bannerData = processBanner(banner);
        applyBanner(product, bannerData);

        Product savedProduct = productRepository.save(product);

        String newBannerUrl = savedProduct.getBannerUrl();

        if (oldBannerUrl != null && !oldBannerUrl.equals(newBannerUrl)) {
            eventPublisher.publishEvent(new BannerReplacedEvent(oldBannerUrl));
        }

        return productTransformer.toDto(savedProduct, dto.translation().language());
    }

    private Product findProductBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .orElseThrow(() -> new ProductNotFoundException("Product with slug '" + slug + "' not found"));
    }

    private void updateProductImages(Product product, ProductPayloadDto dto, MultipartFile[] files) {
        Set<String> retainedUrls = extractRetainedImageKeys(dto);

        deleteRemovedImages(product, retainedUrls);
        uploadNewImages(product, files);
    }

    private Set<String> extractRetainedImageKeys(ProductPayloadDto dto) {
        if (dto.images() == null) {
            return Collections.emptySet();
        }

        return dto.images().stream()
                .map(ProductImageDto::imageUrlLarge)
                .collect(Collectors.toSet());
    }


    private void deleteRemovedImages(Product product, Set<String> retainedUrls) {
        List<ProductImage> toRemove = product.getImages().stream()
                .filter(image -> !retainedUrls.contains(image.getImageUrlSmall())
                        && !retainedUrls.contains(image.getImageUrlMedium())
                        && !retainedUrls.contains(image.getImageUrlLarge()))
                .toList();

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
                log.error("Failed to delete S3 file: " + image + " - " + e.getMessage());
            }
        }

        product.getImages().removeAll(toRemove);
    }

    private void uploadNewImages(Product product, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return;
        }

        List<ProductImage> newImages = processImageFiles(files);
        for (ProductImage image : newImages) {
            image.setProduct(product);
        }
        product.getImages().addAll(newImages);
    }

    private List<Category> resolveCategories(ProductPayloadDto dto) {
        List<Category> categories = categoryRepository.findBySlugInAndLanguage(
                dto.categories(), dto.translation().language());

        if (categories.size() != dto.categories().size()) {
            throw new CategoryNotFoundException("One or more categories not found for language: " + dto.translation().language());
        }

        return categories;
    }

    private BannerData processBanner(MultipartFile banner) {
        if (banner == null || banner.isEmpty()) {
            return null;
        }
        String url = mediaService.uploadBanner(banner);
        BannerType type = resolveBannerType(banner);

        return new BannerData(url, type);
    }
    private BannerType resolveBannerType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType != null && contentType.startsWith("video")) {
            return BannerType.VIDEO;
        }

        return BannerType.IMAGE;
    }

    private Product prepareAndSaveProduct(ProductPayloadDto productPayloadDto,
                                          List<ProductImage> productImages,
                                          BannerData bannerData) {
        Language language = productPayloadDto.translation().language();
        List<Category> categories = categoryRepository.findBySlugInAndLanguage(productPayloadDto.categories(), language);

        if (categories.size() != productPayloadDto.categories().size()) {
            throw new CategoryNotFoundException("One or more categories not found for language: " + language);
        }

        Product product = productTransformer.fromCreateDto(productPayloadDto, categories);

        for (ProductImage image : productImages) {
            image.setProduct(product);
        }
        product.getImages().addAll(productImages);
        applyBanner(product, bannerData);

        return productRepository.save(product);
    }

    private void deleteOldImages(Product product) {
        if (product.getImages() == null) return;

        for (ProductImage oldImage : product.getImages()) {
            try {
                deleteIfPresent(oldImage.getImageUrlSmall());
                deleteIfPresent(oldImage.getImageUrlMedium());
                deleteIfPresent(oldImage.getImageUrlLarge());
            } catch (Exception e) {
                log.error("Failed to delete S3 file: " + oldImage + " - " + e.getMessage());
            }
        }
        productImageRepository.deleteAllByProductId(product.getId());
    }

    private void deleteIfPresent(String url) {
        if (url != null && !url.isBlank()) {
            s3Service.deleteFile(url);
        }
    }

    private List<ProductImage> processImageFiles(MultipartFile[] files) {
        List<ProductImage> productImages = new ArrayList<>();

        if (files == null || files.length == 0) {
            return productImages;
        }

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                ImageUrls imageUrls = mediaService.processAndUploadImage(file);

                ProductImage productImage = ProductImage.builder()
                        .imageUrlSmall(imageUrls.small())
                        .imageUrlMedium(imageUrls.medium())
                        .imageUrlLarge(imageUrls.large())
                        .build();

                productImages.add(productImage);
            }
        }

        return productImages;
    }

    private void applyBanner(Product product, BannerData bannerData) {
        if (bannerData == null) return;
        product.setBannerUrl(bannerData.getUrl());
        product.setBannerType(bannerData.getType());
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