package com.labaway.backend.dto.product.media;

import com.labaway.backend.enums.BannerType;

public record BannerDto(
        BannerType type,
        String url
) {}