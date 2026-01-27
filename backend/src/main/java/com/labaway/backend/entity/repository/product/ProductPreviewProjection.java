package com.labaway.backend.entity.repository.product;

import java.math.BigDecimal;

public interface ProductPreviewProjection {

    String getName();
    String getSlug();
    BigDecimal getPrice();
    Boolean getActive();
    String getImageUrlSmall();
    String getImageUrlMedium();
    String getImageUrlLarge();
    String getCategories();
}

