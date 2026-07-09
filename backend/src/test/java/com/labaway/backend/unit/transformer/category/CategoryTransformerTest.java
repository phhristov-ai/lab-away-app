package com.labaway.backend.transformer.category;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.category.CreateCategoryDto;
import com.labaway.backend.dto.category.CreateCategoryTranslationDto;
import com.labaway.backend.dto.category.UpdateCategoryDto;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.category.CategoryTranslation;
import com.labaway.backend.enums.Language;
import com.labaway.backend.transformer.category.CategoryTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTransformerTest {

    private CategoryTransformer categoryTransformer;

    @BeforeEach
    void setUp() {
        categoryTransformer = new CategoryTransformer();
    }

    @Test
    void shouldMapCategoryToCategoryDto() {
        Category category = getCategory();

        CategoryDto categoryDto = categoryTransformer.toDto(category, Language.EN);

        assertCategoryDto(categoryDto, "STD", "std");
    }

    @Test
    void shouldMapFromCreateDto() {
        CreateCategoryDto createDto = createCategoryDto("STD", "std");
        Category category = categoryTransformer.fromCreateDto(createDto);
        CategoryTranslation translation = categoryTransformer.fromCreateTranslationDto(
                createDto.getTranslation(), category);
        category.setTranslations(Set.of(translation));

        assertCategoryEntity(category, "STD", "std");
    }

    @Test
    void shouldUpdateEntityFromDto() {
        UpdateCategoryDto updateDto = updateCategoryDto("Drugs", "drugs");

        Category category = getCategory();
        categoryTransformer.updateEntityFromDto(updateDto, category, Language.EN);

        assertThat(category.getSlug()).isEqualTo("drugs");
        Optional<CategoryTranslation> translationOpt = category.getTranslations().stream()
                .filter(t -> t.getLanguage() == Language.EN)
                .findFirst();

        assertThat(translationOpt).isPresent();
        assertThat(translationOpt.get().getName()).isEqualTo("Drugs");
    }

    @Test
    void shouldCreateTranslationFromDto() {
        CreateCategoryTranslationDto dto = new CreateCategoryTranslationDto();
        dto.setLanguage(Language.EN);
        dto.setName("STD");

        Category category = new Category();

        CategoryTranslation translation = categoryTransformer.fromCreateTranslationDto(dto, category);

        assertThat(translation.getLanguage()).isEqualTo(Language.EN);
        assertThat(translation.getName()).isEqualTo("STD");
        assertThat(translation.getCategory()).isEqualTo(category);
    }

    @Test
    void shouldAddNewTranslationIfMissingOnUpdate() {
        Category category = Category.builder()
                .slug("std")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        UpdateCategoryDto updateDto = updateCategoryDto("New Name", "updated-slug");

        categoryTransformer.updateEntityFromDto(updateDto, category, Language.DE);

        assertThat(category.getSlug()).isEqualTo("updated-slug");
        Optional<CategoryTranslation> newTranslation = category.getTranslations().stream()
                .filter(t -> t.getLanguage() == Language.DE)
                .findFirst();

        assertThat(newTranslation).isPresent();
        assertThat(newTranslation.get().getName()).isEqualTo("New Name");
    }

    @Test
    void shouldReturnNullNameWhenTranslationMissing() {
        Category category = getCategory();

        CategoryDto dto = categoryTransformer.toDto(category, Language.DE);

        assertThat(dto.getName()).isNull();
    }

    private Category getCategory() {
        Category category = Category.builder()
                .slug("std")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        CategoryTranslation translation = CategoryTranslation.builder()
                .language(Language.EN)
                .name("STD")
                .category(category)
                .build();

        category.setTranslations(Set.of(translation));

        return category;
    }


    private static CreateCategoryDto createCategoryDto(String name, String slug) {
        CreateCategoryDto dto = new CreateCategoryDto();
        dto.setSlug(slug);
        CreateCategoryTranslationDto translationDto = new CreateCategoryTranslationDto();
        translationDto.setLanguage(Language.EN);
        translationDto.setName(name);

        dto.setTranslation(translationDto);
        return dto;
    }


    private static UpdateCategoryDto updateCategoryDto(String name, String slug) {
        UpdateCategoryDto dto = new UpdateCategoryDto();
        dto.setName(name);
        dto.setSlug(slug);
        return dto;
    }

    private static void assertCategoryDto(CategoryDto dto, String expectedName, String expectedSlug) {
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo(expectedName);
        assertThat(dto.getSlug()).isEqualTo(expectedSlug);
    }

    private static void assertCategoryEntity(Category entity, String expectedName, String expectedSlug) {
        assertThat(entity.getSlug()).isEqualTo(expectedSlug);
        assertThat(entity.getTranslations()).hasSize(1);
        assertThat(entity.getTranslations().iterator().next().getName()).isEqualTo(expectedName);
        assertThat(entity.getTranslations().iterator().next().getLanguage()).isEqualTo(Language.EN);
    }
}