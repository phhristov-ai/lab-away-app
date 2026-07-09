package com.labaway.backend.dto.product.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductImageDto(

        @NotBlank(message = "Small image URL is required")
        @Size(max = 500, message = "Small image URL must not exceed 500 characters")
        String imageUrlSmall,

        @NotBlank(message = "Medium image URL is required")
        @Size(max = 500, message = "Medium image URL must not exceed 500 characters")
        String imageUrlMedium,

        @NotBlank(message = "Large image URL is required")
        @Size(max = 500, message = "Large image URL must not exceed 500 characters")
        String imageUrlLarge,

        Integer position

) {}