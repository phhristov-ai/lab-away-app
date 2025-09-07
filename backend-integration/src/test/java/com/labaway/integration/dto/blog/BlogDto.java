package com.labaway.integration.dto.blog;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BlogDto {

    private String author;

    private List<String> categorySlugs;

    private TranslationDto translation;
}
