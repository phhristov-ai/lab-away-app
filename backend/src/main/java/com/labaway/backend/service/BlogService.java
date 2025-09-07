package com.labaway.backend.service;

import com.labaway.backend.dto.blog.*;
import com.labaway.backend.entity.blog.Blog;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.enums.Language;
import com.labaway.backend.exception.ResourceNotFoundException;
import com.labaway.backend.entity.repository.BlogRepository;
import com.labaway.backend.entity.repository.CategoryRepository;
import com.labaway.backend.transformer.BlogTransformer;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.language.bm.Lang;
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


    private void uploadImageIfPresent(Blog blog, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String imageUrl = s3Service.uploadFile(file);
            blog.setImageUrl(imageUrl);
        }
    }

    private void replaceImageIfPresent(Blog blog, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            if (blog.getImageUrl() != null && !blog.getImageUrl().isEmpty()) {
                s3Service.deleteFile(blog.getImageUrl());
            }
            String imageUrl = s3Service.uploadFile(file);
            blog.setImageUrl(imageUrl);
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

    public void updateImageUrl(String slug, String imageUrl) {
        Blog blog = blogRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Blog not found"));
        blog.setImageUrl(imageUrl);
        blogRepository.save(blog);
    }

    @Transactional
    public void deleteBlog(String slug) {
        Blog blog = blogRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with slug " + slug));

        if (blog.getImageUrl() != null && !blog.getImageUrl().isEmpty()) {
            s3Service.deleteFile(blog.getImageUrl());
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