package com.labaway.backend.dto.blog;

import java.time.Instant;

public interface BlogPreviewProjection {
    String getSlug();
    String getAuthor();
    String getImageUrl();
    String getTitle();
    String getExcerpt();
    Integer getReadingTime();
    Instant getCreatedAt();
    Instant getUpdatedAt();
    String getCategories();
}
