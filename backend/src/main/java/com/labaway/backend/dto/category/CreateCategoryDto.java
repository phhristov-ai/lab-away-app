package com.labaway.backend.dto.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryDto {

    @NotBlank(message = "Slug must not be empty")
    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;

    @Valid
    @NotNull
    private CreateCategoryTranslationDto translation;
}
