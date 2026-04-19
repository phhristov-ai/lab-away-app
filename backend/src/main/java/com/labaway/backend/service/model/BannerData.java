package com.labaway.backend.service.model;

import com.labaway.backend.enums.BannerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BannerData {
    private String url;
    private BannerType type;
}