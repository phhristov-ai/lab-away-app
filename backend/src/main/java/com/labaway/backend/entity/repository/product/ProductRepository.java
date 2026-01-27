package com.labaway.backend.entity.repository.product;

import com.labaway.backend.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findBySlug(String slug);

    @Query(value = "SELECT * FROM product_preview_view WHERE language = :language", nativeQuery = true)
    List<ProductPreviewProjection> findAllProductPreviewsByLanguage(@Param("language") String language);

    @Query(value = "SELECT * FROM product_preview_view WHERE language = :language AND active = true ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<ProductPreviewProjection> findRandomProductPreviewsByLanguage(@Param("language") String language);

    @Query(value = """
    SELECT * FROM product_preview_view 
    WHERE language = :language 
      AND JSON_CONTAINS(categories, JSON_OBJECT('slug', :categorySlug))
    ORDER BY RAND()
    LIMIT 3
    """, nativeQuery = true)
    List<ProductPreviewProjection> findRandomByLanguageAndCategory(
            @Param("language") String language,
            @Param("categorySlug") String categorySlug
    );
}
