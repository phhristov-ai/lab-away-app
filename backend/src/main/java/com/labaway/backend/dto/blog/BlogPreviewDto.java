package com.labaway.backend.dto.blog;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.image.ImageUrls;

import java.time.Instant;
import java.util.List;

public record BlogPreviewDto(
        String slug,
        String author,
        ImageUrls imageUrls,
        String title,
        String excerpt,
        Integer readingTime,
        Instant createdAt,
        Instant updatedAt,
        List<CategoryDto> categories
) {}