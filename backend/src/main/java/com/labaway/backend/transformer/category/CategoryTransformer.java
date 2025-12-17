package com.labaway.backend.transformer.category;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.category.CreateCategoryDto;
import com.labaway.backend.dto.category.CreateCategoryTranslationDto;
import com.labaway.backend.dto.category.UpdateCategoryDto;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.category.CategoryTranslation;
import com.labaway.backend.enums.Language;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CategoryTransformer {

    public CategoryDto toDto(Category category, Language language) {
        Optional<CategoryTranslation> translationOpt = category.getTranslations().stream()
                .filter(t -> language == null || t.getLanguage() == language)
                .findFirst();

        String name = translationOpt.map(CategoryTranslation::getName).orElse(null);

        return CategoryDto.builder()
                .slug(category.getSlug())
                .name(name)
                .build();
    }

    public Category fromCreateDto(CreateCategoryDto dto) {
        return Category.builder()
                .slug(dto.getSlug())
                .build();
    }

    public void updateEntityFromDto(UpdateCategoryDto dto, Category category, Language language) {
        category.setSlug(dto.getSlug());

        Optional<CategoryTranslation> translationOpt = category.getTranslations().stream()
                .filter(t -> t.getLanguage() == language)
                .findFirst();

        if (translationOpt.isPresent()) {
            CategoryTranslation translation = translationOpt.get();
            translation.setName(dto.getName());
        } else {
            CategoryTranslation newTranslation = new CategoryTranslation();
            newTranslation.setCategory(category);
            newTranslation.setLanguage(language);
            newTranslation.setName(dto.getName());
            category.getTranslations().add(newTranslation);
        }
    }

    public CategoryTranslation fromCreateTranslationDto(CreateCategoryTranslationDto dto, Category category) {
        return CategoryTranslation.builder()
                .category(category)
                .language(dto.getLanguage())
                .name(dto.getName())
                .build();
    }

}