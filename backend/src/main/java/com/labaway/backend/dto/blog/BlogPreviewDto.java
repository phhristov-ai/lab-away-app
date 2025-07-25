package com.labaway.backend.dto.blog;

import com.labaway.backend.dto.category.CategoryDto;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class BlogPreviewDto {
    private String slug;
    private String author;
    private String imageUrl;
    private String title;
    private String excerpt;
    private Integer readingTime;
    private Instant createdAt;
    private Instant updatedAt;
    private List<CategoryDto> categories;
}
