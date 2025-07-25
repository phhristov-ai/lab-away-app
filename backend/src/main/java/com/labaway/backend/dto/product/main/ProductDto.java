package com.labaway.backend.dto.product.main;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.product.image.ProductImageDto;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;

@Data
@Builder
public class ProductDto {

    @NotBlank(message = "Name must not be empty")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Slug must not be empty")
    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stock;

    private String description;

    @NotNull(message = "At least one category slug is required")
    @Size(min = 1, message = "At least one category slug is required")
    private List<CategoryDto> categories;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @NotNull
    private List<ProductImageDto> images;

}
