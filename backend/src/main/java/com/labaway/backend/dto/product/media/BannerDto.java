package com.labaway.backend.dto.product.media;

import com.labaway.backend.enums.BannerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BannerDto {
    private BannerType type;
    private String url;
}