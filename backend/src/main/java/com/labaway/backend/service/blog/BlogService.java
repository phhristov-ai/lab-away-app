package com.labaway.backend.service.blog;

import com.labaway.backend.dto.blog.*;
import com.labaway.backend.dto.image.ImageUrls;
import com.labaway.backend.entity.blog.Blog;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.enums.Language;
import com.labaway.backend.exception.ResourceNotFoundException;
import com.labaway.backend.entity.repository.blog.BlogRepository;
import com.labaway.backend.entity.repository.category.CategoryRepository;
import com.labaway.backend.service.media.ImageService;
import com.labaway.backend.service.storage.S3Service;
import com.labaway.backend.transformer.blog.BlogTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final BlogTransformer blogTransformer;
    private final S3Service s3Service;

    private final ImageService imageService;

    @Transactional(readOnly = true)
    public List<BlogPreviewDto> getAllBlogsForPreview(Language lang) {
        List<BlogPreviewProjection> projections = blogRepository.findAllBlogsForPreview(lang.name());
        return blogTransformer.mapToBlogPreviewDtos(projections);
    }

    @Transactional(readOnly = true)
    public BlogResponseDto getBlogBySlug(String slug, Language lang) {
        Blog blog = blogRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found"));
        return blogTransformer.toDto(blog, lang);
    }

    @Transactional
    public BlogResponseDto createBlog(BlogDto blogDto, MultipartFile file) throws IOException {
        TranslationDto translation = blogDto.getTranslation();
        Language lang = translation.getLanguage();
        String title = translation.getTitle().trim();
        String slug = generateSlug(title);
        if (blogRepository.findBySlug(slug).isPresent()) {
            throw new IllegalArgumentException("A blog with this slug already exists: " + slug);
        }
        Blog blog = blogTransformer.fromCreateDto(blogDto);

        updateBlogCategories(blog, blogDto.getCategorySlugs());
        blog.setSlug(slug);
        uploadImageIfPresent(blog, file);

        Blog savedBlog =  blogRepository.saveAndFlush(blog);
        return blogTransformer.toDto(savedBlog, lang);
    }

    @Transactional
    public BlogResponseDto updateBlog(String currentSlug, BlogDto blogDto, MultipartFile file) throws IOException {
        Blog blog = getBlogOrThrow(currentSlug);
        blogTransformer.updateEntity(blog, blogDto);

        updateBlogCategories(blog, blogDto.getCategorySlugs());

        replaceImageIfPresent(blog, file);
        Blog savedBlog = blogRepository.save(blog);
        return blogTransformer.toDto(savedBlog, Language.EN);
    }


    private void uploadImageIfPresent(Blog blog, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            ImageUrls imageUrls = imageService.processAndUploadImage(file);

            blog.setImageUrlSmall(imageUrls.getSmall());
            blog.setImageUrlMedium(imageUrls.getMedium());
            blog.setImageUrlLarge(imageUrls.getLarge());
        }
    }


    private void replaceImageIfPresent(Blog blog, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            if (blog.getImageUrlSmall() != null && !blog.getImageUrlSmall().isEmpty()) {
                s3Service.deleteFile(blog.getImageUrlSmall());
            }

            if (blog.getImageUrlMedium() != null && !blog.getImageUrlMedium().isEmpty()) {
                s3Service.deleteFile(blog.getImageUrlMedium());
            }

            if (blog.getImageUrlLarge() != null && !blog.getImageUrlLarge().isEmpty()) {
                s3Service.deleteFile(blog.getImageUrlLarge());
            }

            ImageUrls newImageUrls = imageService.processAndUploadImage(file);

            blog.setImageUrlSmall(newImageUrls.getSmall());
            blog.setImageUrlMedium(newImageUrls.getMedium());
            blog.setImageUrlLarge(newImageUrls.getLarge());
        }
    }


    public List<BlogPreviewDto> getRandomBlogPreviews(Language lang) {
        return blogRepository.findRandomBlogsByLanguage(lang.name()).stream()
                .map(blogTransformer::mapToBlogPreviewDto)
                .toList();
    }

    private Set<Category> fetchAndValidateCategories(List<String> categorySlugs) {
        List<Category> categories = categoryRepository.findBySlugIn(categorySlugs);
        if (categories.size() != categorySlugs.size()) {
            throw new ResourceNotFoundException("One or more categories not found");
        }
        return new HashSet<>(categories);
    }

    private void updateBlogCategories(Blog blog, List<String> categorySlugs) {
        if (categorySlugs != null && !categorySlugs.isEmpty()) {
            Set<Category> categories = fetchAndValidateCategories(categorySlugs);
            blog.setCategories(categories);
        }
    }


    private Blog getBlogOrThrow(String slug) {
        return blogRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with slug: " + slug));
    }

    @Transactional
    public void deleteBlog(String slug) {
        Blog blog = blogRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with slug " + slug));

        if (blog.getImageUrlSmall() != null && !blog.getImageUrlSmall().isEmpty()) {
            s3Service.deleteFile(blog.getImageUrlSmall());
        }

        if (blog.getImageUrlMedium() != null && !blog.getImageUrlMedium().isEmpty()) {
            s3Service.deleteFile(blog.getImageUrlMedium());
        }

        if (blog.getImageUrlLarge() != null && !blog.getImageUrlLarge().isEmpty()) {
            s3Service.deleteFile(blog.getImageUrlLarge());
        }

        blogRepository.delete(blog);
    }



    private String generateSlug(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");
    }
}