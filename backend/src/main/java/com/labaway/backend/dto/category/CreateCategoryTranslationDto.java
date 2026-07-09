package com.labaway.backend.dto.category;

import com.labaway.backend.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryTranslationDto(
        @NotNull(message = "Language must not be null")
        Language language,

        @NotBlank(message = "Name must not be empty")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name
) {}