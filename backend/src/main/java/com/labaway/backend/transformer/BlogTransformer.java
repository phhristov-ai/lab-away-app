package com.labaway.backend.transformer;

import com.labaway.backend.dto.blog.*;
import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.entity.blog.Blog;
import com.labaway.backend.entity.blog.BlogTranslation;
import com.labaway.backend.enums.Language;
import com.labaway.backend.util.JsonParsingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BlogTransformer {

    private final CategoryTransformer categoryTransformer;

    private final JsonParsingUtils jsonParsingUtils;

    public BlogResponseDto toDto(Blog blog, Language language) {
        List<CategoryDto> categoryDtos = mapCategories(blog, language);
        BlogTranslation translation = extractTranslation(blog, language);

        return BlogResponseDto.builder()
                .slug(blog.getSlug())
                .author(blog.getAuthor())
                .imageUrl(blog.getImageUrl())
                .categories(categoryDtos)
                .title(translation != null ? translation.getTitle() : null)
                .content(translation != null ? translation.getContent() : null)
                .readingTime(translation != null ? translation.getReadingTime() : 0)
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .build();
    }

    private List<CategoryDto> mapCategories(Blog blog, Language language) {
        return blog.getCategories().stream()
                .map(category -> categoryTransformer.toDto(category, language))
                .toList();
    }

    private BlogTranslation extractTranslation(Blog blog, Language language) {
        return blog.getTranslations().stream()
                .filter(t -> t.getLanguage() == language)
                .findFirst()
                .orElse(null);
    }

    public Blog fromCreateDto(BlogDto dto) {
        Blog blog = Blog.builder()
                .author(dto.getAuthor())
                .build();
        blog.getTranslations().add(createTranslation(dto.getTranslation(), blog));
        return blog;
    }
    private BlogTranslation createTranslation(TranslationDto dto, Blog blog) {
        return BlogTranslation.builder()
                .language(dto.getLanguage())
                .title(dto.getTitle())
                .content(dto.getContent())
                .readingTime(estimateReadingTime(dto.getContent()))
                .blog(blog)
                .build();
    }

    public void updateEntity(Blog blog, BlogDto dto) {
        blog.setAuthor(dto.getAuthor());

        TranslationDto translationDto = dto.getTranslation();
        Language language = translationDto.getLanguage();

        Optional<BlogTranslation> existingTranslationOpt = blog.getTranslations().stream()
                .filter(t -> t.getLanguage() == language)
                .findFirst();

        if (existingTranslationOpt.isPresent()) {
            BlogTranslation existing = existingTranslationOpt.get();
            existing.setTitle(translationDto.getTitle());
            existing.setContent(translationDto.getContent());
            existing.setReadingTime(estimateReadingTime(translationDto.getContent()));
        } else {
            BlogTranslation newTranslation = createTranslation(translationDto, blog);
            blog.getTranslations().add(newTranslation);
        }
    }

    private int estimateReadingTime(String content) {
        if (content == null || content.isBlank()) return 0;
        int wordCount = content.trim().split("\\s+").length;
        return (int) Math.ceil(wordCount / 200.0);
    }

    public List<BlogPreviewDto> mapToBlogPreviewDtos(List<BlogPreviewProjection> projections) {
        return projections.stream()
                .map(this::mapToBlogPreviewDto)
                .toList();
    }

    public BlogPreviewDto mapToBlogPreviewDto(BlogPreviewProjection projection) {
        return BlogPreviewDto.builder()
                .slug(projection.getSlug())
                .author(projection.getAuthor())
                .imageUrl(projection.getImageUrl())
                .title(projection.getTitle())
                .excerpt(projection.getExcerpt())
                .readingTime(projection.getReadingTime())
                .createdAt(projection.getCreatedAt())
                .updatedAt(projection.getUpdatedAt())
                .categories(jsonParsingUtils.parseCategoryList(projection.getCategories()))
                .build();
    }
}