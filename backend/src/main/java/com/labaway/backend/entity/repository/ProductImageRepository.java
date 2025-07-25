package com.labaway.backend.entity.repository;

import com.labaway.backend.entity.product.Product;
import com.labaway.backend.entity.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
    List<ProductImage> findByProduct(Product product);
    void deleteAllByProductId(UUID productId);

}
