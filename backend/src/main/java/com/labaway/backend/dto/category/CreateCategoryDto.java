package com.labaway.backend.dto.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryDto(
        @NotBlank(message = "Slug must not be empty")
        @Size(max = 255, message = "Slug must not exceed 255 characters")
        String slug,

        @Valid
        @NotNull
        CreateCategoryTranslationDto translation
) {}