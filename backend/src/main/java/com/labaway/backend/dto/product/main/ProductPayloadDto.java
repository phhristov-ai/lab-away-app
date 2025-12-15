package com.labaway.backend.dto.product.main;

import com.labaway.backend.dto.product.image.ProductImageDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPayloadDto {

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stock;

    private boolean active;

    @NotNull(message = "At least one category slug is required")
    @Size(min = 1, message = "At least one category slug is required")
    private List<String> categories;

    @NotNull(message = "Main image index is required")
    @Min(value = 0, message = "Main image index must be a non-negative integer")
    private Integer mainImageIndex;

    @NotNull(message = "One translation is required")
    private @Valid ProductTranslationDto translation;

    private List<ProductImageDto> images;

}
