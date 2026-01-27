package com.labaway.backend.dto.product.main;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.image.ImageUrls;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductPreviewDto {
    private String name;
    private String slug;
    private BigDecimal price;
    private ImageUrls imageUrls;
    private List<CategoryDto> categories;
    private boolean active;
}
