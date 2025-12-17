package com.labaway.backend.transformer.product;

import com.labaway.backend.dto.product.image.ProductImageDto;
import com.labaway.backend.entity.product.ProductImage;
import org.springframework.stereotype.Component;

@Component
public class ProductImageTransformer {

    public ProductImageDto toDto(ProductImage image) {
        return ProductImageDto.builder()
                .imageUrlSmall(image.getImageUrlSmall())
                .imageUrlMedium(image.getImageUrlMedium())
                .imageUrlLarge(image.getImageUrlLarge())
                .main(image.isMain())
                .build();
    }
}
