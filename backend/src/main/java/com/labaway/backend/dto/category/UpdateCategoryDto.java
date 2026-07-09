package com.labaway.backend.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryDto(

        @NotBlank(message = "Name must not be empty")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,

        @NotBlank(message = "Slug must not be empty")
        @Size(max = 255, message = "Slug must not exceed 255 characters")
        String slug

) {}