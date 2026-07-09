package com.labaway.backend.dto.blog;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.image.ImageUrls;
import java.time.Instant;
import java.util.List;

public record BlogResponseDto(
        String slug,
        String author,
        ImageUrls imageUrls,
        String title,
        String content,
        List<CategoryDto> categories,
        Instant createdAt,
        Instant updatedAt,
        int readingTime
) {}