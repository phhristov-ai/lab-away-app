package com.labaway.backend.dto.blog;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.image.ImageUrls;
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
    private ImageUrls imageUrls;
    private String title;
    private String content;
    private List<CategoryDto> categories;
    private Instant createdAt;
    private Instant updatedAt;
    private int readingTime;

}
