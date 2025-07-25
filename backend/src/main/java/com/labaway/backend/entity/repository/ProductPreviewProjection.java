package com.labaway.backend.entity.repository;

import java.math.BigDecimal;

public interface ProductPreviewProjection {
    String getName();
    String getSlug();
    BigDecimal getPrice();
    String getThumbnailUrl();
    String getCategories();
}
