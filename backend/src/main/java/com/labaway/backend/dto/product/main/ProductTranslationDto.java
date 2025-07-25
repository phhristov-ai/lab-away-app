package com.labaway.backend.dto.product.main;

import com.labaway.backend.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductTranslationDto {
    @NotNull
    private Language language;

    @NotBlank
    @Size(max = 255)
    private String name;

    private String description;
}
