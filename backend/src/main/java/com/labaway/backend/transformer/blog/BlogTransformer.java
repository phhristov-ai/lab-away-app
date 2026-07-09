package com.labaway.backend.transformer.blog;

import com.labaway.backend.dto.blog.*;
import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.image.ImageUrls;
import com.labaway.backend.entity.blog.Blog;
import com.labaway.backend.entity.blog.BlogTranslation;
import com.labaway.backend.enums.Language;
import com.labaway.backend.transformer.category.CategoryTransformer;
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

        return new BlogResponseDto(
                blog.getSlug(),
                blog.getAuthor(),
                mapImageUrls(blog),
                translation != null ? translation.getTitle() : null,
                translation != null ? translation.getContent() : null,
                categoryDtos,
                blog.getCreatedAt(),
                blog.getUpdatedAt(),
                translation != null ? translation.getReadingTime() : 0
        );
    }

    private ImageUrls mapImageUrls(Blog blog) {
        if (blog == null) {
            return null;
        }

        return new ImageUrls(
                blog.getImageUrlSmall(),
                blog.getImageUrlMedium(),
                blog.getImageUrlLarge()
        );
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
                .author(dto.author())
                .build();
        blog.getTranslations().add(createTranslation(dto.translation(), blog));
        return blog;
    }
    private BlogTranslation createTranslation(TranslationDto dto, Blog blog) {
        return BlogTranslation.builder()
                .language(dto.language())
                .title(dto.title().trim())
                .content(dto.content().trim())
                .readingTime(estimateReadingTime(dto.content()))
                .blog(blog)
                .build();
    }

    public void updateEntity(Blog blog, BlogDto dto) {
        blog.setAuthor(dto.author());

        TranslationDto translationDto = dto.translation();
        Language language = translationDto.language();

        Optional<BlogTranslation> existingTranslationOpt = blog.getTranslations().stream()
                .filter(t -> t.getLanguage() == language)
                .findFirst();

        if (existingTranslationOpt.isPresent()) {
            BlogTranslation existing = existingTranslationOpt.get();
            existing.setTitle(translationDto.title());
            existing.setContent(translationDto.content());
            existing.setReadingTime(estimateReadingTime(translationDto.content()));
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
        return new BlogPreviewDto(
                projection.getSlug(),
                projection.getAuthor(),
                mapImageUrls(projection),
                projection.getTitle(),
                projection.getExcerpt(),
                projection.getReadingTime(),
                projection.getCreatedAt(),
                projection.getUpdatedAt(),
                jsonParsingUtils.parseCategoryList(projection.getCategories())
        );
    }

    private ImageUrls mapImageUrls(BlogPreviewProjection projection) {
        return new ImageUrls(
                projection.getImageUrlSmall(),
                projection.getImageUrlMedium(),
                projection.getImageUrlLarge()
        );
    }
}