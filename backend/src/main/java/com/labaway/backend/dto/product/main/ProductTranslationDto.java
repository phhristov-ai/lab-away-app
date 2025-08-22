package com.labaway.backend.dto.product.main;

import com.labaway.backend.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTranslationDto {
    @NotNull
    private Language language;

    @NotBlank
    @Size(max = 255)
    private String name;

    private String description;
}
