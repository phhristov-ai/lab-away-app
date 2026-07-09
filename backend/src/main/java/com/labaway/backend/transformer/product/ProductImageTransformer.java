package com.labaway.backend.transformer.product;

import com.labaway.backend.dto.product.media.ProductImageDto;
import com.labaway.backend.entity.product.ProductImage;
import org.springframework.stereotype.Component;

@Component
public class ProductImageTransformer {

    public ProductImageDto toDto(ProductImage image) {
        return new ProductImageDto(
                image.getImageUrlSmall(),
                image.getImageUrlMedium(),
                image.getImageUrlLarge(),
                image.getPosition()
        );
    }
}