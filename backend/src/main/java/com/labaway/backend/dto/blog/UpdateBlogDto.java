package com.labaway.backend.dto.blog;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateBlogDto {

    private String slug;

    private String author;

    private String imageUrl;

    private List<String> categorySlugs;
}