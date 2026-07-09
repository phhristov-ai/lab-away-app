package com.labaway.backend.dto.product.main;

import com.labaway.backend.dto.product.media.ProductImageDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductPayloadDto(

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        Integer stock,

        @NotNull
        boolean active,

        @NotNull(message = "At least one category slug is required")
        @Size(min = 1, message = "At least one category slug is required")
        List<String> categories,

        @NotNull(message = "Main image index is required")
        @Min(value = 0, message = "Main image index must be a non-negative integer")
        Integer mainImageIndex,

        @NotNull(message = "One translation is required")
        @Valid
        ProductTranslationDto translation,

        List<ProductImageDto> images

) {}