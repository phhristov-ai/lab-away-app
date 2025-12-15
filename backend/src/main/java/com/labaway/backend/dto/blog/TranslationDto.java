package com.labaway.backend.dto.product.image.blog;

import com.labaway.backend.enums.Language;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TranslationDto {
    private Language language;
    private String title;
    private String content;
}