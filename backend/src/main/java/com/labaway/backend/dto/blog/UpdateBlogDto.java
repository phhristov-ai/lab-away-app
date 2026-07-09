package com.labaway.backend.dto.blog;

import java.util.List;

public record UpdateBlogDto(
        String slug,
        String author,
        String imageUrl,
        List<String> categorySlugs
) {}