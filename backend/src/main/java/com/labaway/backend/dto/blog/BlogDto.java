package com.labaway.backend.dto.blog;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BlogDto(
        @NotBlank(message = "Author is required")
        String author,

        @NotNull(message = "Categories are required")
        List<String> categorySlugs,

        @NotNull(message = "One translation is required")
        @Valid
        TranslationDto translation
) {}