package com.labaway.backend.dto.blog;

import com.labaway.backend.dto.category.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogResponseDto {

    private String slug;
    private String author;
    private String imageUrl;
    private String title;
    private String content;
    private List<CategoryDto> categories;
    private Instant createdAt;
    private Instant updatedAt;

    private int readingTime;

    public BlogResponseDto(String slug, String author, String imageUrl,
                           String title, String content,
                           int readingTime,
                           Instant createdAt, Instant updatedAt) {
        this.slug = slug;
        this.author = author;
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
        this.readingTime = readingTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.categories = null;
    }
}
