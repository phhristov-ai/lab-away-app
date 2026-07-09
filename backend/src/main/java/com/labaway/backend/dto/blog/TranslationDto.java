package com.labaway.backend.dto.blog;

import com.labaway.backend.enums.Language;

public record TranslationDto(
        Language language,
        String title,
        String content
) {}