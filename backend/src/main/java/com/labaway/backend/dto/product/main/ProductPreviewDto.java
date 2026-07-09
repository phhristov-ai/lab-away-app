package com.labaway.backend.dto.product.main;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.image.ImageUrls;

import java.math.BigDecimal;
import java.util.List;

public record ProductPreviewDto(
        String name,
        String slug,
        BigDecimal price,
        ImageUrls imageUrls,
        List<CategoryDto> categories,
        boolean active
) {}