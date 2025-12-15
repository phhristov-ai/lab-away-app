package com.labaway.backend.dto.blog;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BlogDto {

    @NotBlank(message = "Author is required")
    private String author;

    @NotNull(message = "Categories are required")
    private List<String> categorySlugs;

    @NotNull(message = "One translation is required")
    private @Valid TranslationDto translation;
}
