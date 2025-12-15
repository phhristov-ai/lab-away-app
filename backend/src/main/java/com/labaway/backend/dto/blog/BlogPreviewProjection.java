package com.labaway.backend.dto.blog;

import java.time.Instant;

public interface BlogPreviewProjection {
    String getSlug();
    String getAuthor();
    String getImageUrl();        // legacy
    String getImageUrlSmall();
    String getImageUrlMedium();
    String getImageUrlLarge();

    String getTitle();
    String getExcerpt();
    Integer getReadingTime();
    Instant getCreatedAt();
    Instant getUpdatedAt();
    String getCategories();
}
