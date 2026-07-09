package com.labaway.backend.dto.product.main;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.product.media.BannerDto;
import com.labaway.backend.dto.product.media.ProductImageDto;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;

public record ProductDto(

        @NotBlank(message = "Name must not be empty")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,

        @NotBlank(message = "Slug must not be empty")
        @Size(max = 255, message = "Slug must not exceed 255 characters")
        String slug,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        Integer stock,

        String description,

        @NotNull(message = "At least one category slug is required")
        @Size(min = 1, message = "At least one category slug is required")
        List<CategoryDto> categories,

        @NotNull
        LocalDateTime createdAt,

        @NotNull
        LocalDateTime updatedAt,

        @NotNull
        List<ProductImageDto> images,

        @NotNull
        Boolean active,

        BannerDto banner

) {}