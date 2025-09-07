package com.labaway.integration.dto.blog;

import com.labaway.integration.enums.Language;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TranslationDto {
    private Language language;
    private String title;
    private String content;
}