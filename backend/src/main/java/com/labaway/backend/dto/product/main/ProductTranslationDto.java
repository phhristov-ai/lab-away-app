package com.labaway.backend.dto.product.main;

import com.labaway.backend.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductTranslationDto(
        @NotNull Language language,
        @NotBlank @Size(max = 255) String name,
        String description
) {}
