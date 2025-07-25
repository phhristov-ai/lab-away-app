package com.labaway.backend.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCategoryDto {

    @NotBlank(message = "Name must not be empty")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Slug must not be empty")
    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;
}
