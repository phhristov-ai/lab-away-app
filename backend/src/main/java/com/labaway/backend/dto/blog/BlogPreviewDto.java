package com.labaway.backend.dto.blog;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.image.ImageUrls;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class BlogPreviewDto {
    private String slug;
    private String author;
    private ImageUrls imageUrls;
    private String title;
    private String excerpt;
    private Integer readingTime;
    private Instant createdAt;
    private Instant updatedAt;
    private List<CategoryDto> categories;
}
