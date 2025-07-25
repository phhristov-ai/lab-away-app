package com.labaway.backend.controller;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.category.CreateCategoryDto;
import com.labaway.backend.dto.category.CreateCategoryTranslationDto;
import com.labaway.backend.dto.category.UpdateCategoryDto;
import com.labaway.backend.enums.Language;
import com.labaway.backend.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;
    @Mock
    private CategoryService categoryService;
    private UUID categoryId;
    private CategoryDto categoryDto;
    private CreateCategoryDto createCategoryDto;
    private UpdateCategoryDto updateCategoryDto;

    private String slug;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        slug = "test-category";
        categoryId = UUID.randomUUID();

        categoryDto = createCategoryDto("Books", slug);
        createCategoryDto = createCreateCategoryDto("Books");
        updateCategoryDto = createUpdateCategoryDto("Updated Books");
    }

    @Test
    void addOrUpdateTranslation_callsServiceAndReturnsCreated() {
        CreateCategoryTranslationDto translationDto = new CreateCategoryTranslationDto();
        translationDto.setLanguage(Language.EN);
        translationDto.setName("Translated Category");

        ResponseEntity<Void> response = categoryController.addOrUpdateTranslation(slug, translationDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(categoryService).addOrUpdateTranslation(slug, translationDto);
    }


    @Test
    void getAllCategories_returnsList() {
        when(categoryService.getAllCategories(Language.EN)).thenReturn(List.of(categoryDto));

        List<CategoryDto> result = categoryController.getAllCategories(Language.EN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Books");
        verify(categoryService).getAllCategories(Language.EN);
    }

    @Test
    void getCategoryById_returnsCategoryDto() {
        when(categoryService.getCategoryBySlug(slug, Language.EN)).thenReturn(categoryDto);

        var response = categoryController.getCategoryBySlug(slug, Language.EN);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(categoryDto);
        verify(categoryService).getCategoryBySlug(slug, Language.EN);
    }

    @Test
    void createCategory_returnsCreatedCategoryDto() {
        when(categoryService.createCategory(createCategoryDto)).thenReturn(categoryDto);

        var response = categoryController.createCategory(createCategoryDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(categoryDto);
        verify(categoryService).createCategory(createCategoryDto);
    }

    @Test
    void updateCategory_returnsUpdatedCategoryDto() {
        when(categoryService.updateCategory(slug, updateCategoryDto, Language.EN)).thenReturn(categoryDto);

        var response = categoryController.updateCategory(slug, updateCategoryDto, Language.EN);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(categoryDto);
        verify(categoryService).updateCategory(slug, updateCategoryDto, Language.EN);
    }

    @Test
    void deleteCategory_returnsNoContent() {
        doNothing().when(categoryService).deleteCategory(slug);

        var response = categoryController.deleteCategory(slug);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(categoryService).deleteCategory(slug);
    }

    private static CategoryDto createCategoryDto(String name, String slug) {
        return CategoryDto.builder()
                .name(name)
                .slug(slug)
                .build();
    }

    private static CreateCategoryDto createCreateCategoryDto(String name) {
        CreateCategoryDto dto = new CreateCategoryDto();
        return dto;
    }
    private static UpdateCategoryDto createUpdateCategoryDto(String name) {
        UpdateCategoryDto dto = new UpdateCategoryDto();
        dto.setName(name);
        return dto;
    }
}
