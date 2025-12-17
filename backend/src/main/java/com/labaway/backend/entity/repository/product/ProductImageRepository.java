package com.labaway.backend.entity.repository.product;

import com.labaway.backend.entity.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
    void deleteAllByProductId(UUID productId);

}
