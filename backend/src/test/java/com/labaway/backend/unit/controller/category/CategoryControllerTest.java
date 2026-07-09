package com.labaway.backend.unit.controller.category;

import com.labaway.backend.controller.category.CategoryController;
import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.category.CreateCategoryDto;
import com.labaway.backend.dto.category.CreateCategoryTranslationDto;
import com.labaway.backend.dto.category.UpdateCategoryDto;
import com.labaway.backend.enums.Language;
import com.labaway.backend.service.category.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    @Test
    void addOrUpdateTranslation_callsServiceAndReturnsCreated() {
        String slug = "test-category";
        CreateCategoryTranslationDto translationDto =
                new CreateCategoryTranslationDto(Language.EN, "Translated Category");

        ResponseEntity<Void> response =
                categoryController.addOrUpdateTranslation(slug, translationDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(categoryService).addOrUpdateTranslation(slug, translationDto);
    }

    @Test
    void getAllCategories_returnsList() {
        Language language = Language.EN;
        CategoryDto categoryDto = createCategoryDto("Books", "books");

        when(categoryService.getAllCategories(language))
                .thenReturn(List.of(categoryDto));

        List<CategoryDto> result = categoryController.getAllCategories(language);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Books");

        verify(categoryService).getAllCategories(language);
    }

    private CategoryDto createCategoryDto(String name, String slug) {
        return new CategoryDto(name, slug);
    }

    @Test
    void getCategoryById_returnsCategoryDto() {
        String slug = "test-category";
        Language language = Language.EN;

        CategoryDto categoryDto = createCategoryDto("Books", slug);

        when(categoryService.getCategoryBySlug(slug, language))
                .thenReturn(categoryDto);
        var response = categoryController.getCategoryBySlug(slug, language);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(categoryDto);

        verify(categoryService).getCategoryBySlug(slug, language);
    }

    @Test
    void createCategory_returnsCreatedCategoryDto() {
        CreateCategoryDto createCategoryDto = createCreateCategoryDto("books");

        CategoryDto categoryDto = createCategoryDto("Books", "books");

        when(categoryService.createCategory(createCategoryDto))
                .thenReturn(categoryDto);

        var response = categoryController.createCategory(createCategoryDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(categoryDto);

        verify(categoryService).createCategory(createCategoryDto);
    }

    private CreateCategoryDto createCreateCategoryDto(String slug) {
        return new CreateCategoryDto(
                slug,
                new CreateCategoryTranslationDto(
                        Language.EN,
                        "Books"
                )
        );
    }

    @Test
    void updateCategory_returnsUpdatedCategoryDto() {
        String slug = "test-category";
        Language language = Language.EN;

        UpdateCategoryDto updateCategoryDto = createUpdateCategoryDto(
                "Updated Books",
                "updated-books"
        );

        CategoryDto categoryDto = createCategoryDto(
                "Updated Books",
                "updated-books"
        );

        when(categoryService.updateCategory(slug, updateCategoryDto, language))
                .thenReturn(categoryDto);

        var response = categoryController.updateCategory(slug, updateCategoryDto, language);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(categoryDto);

        verify(categoryService).updateCategory(slug, updateCategoryDto, language);
    }

    private UpdateCategoryDto createUpdateCategoryDto(String name, String slug) {
        return new UpdateCategoryDto(
                name,
                slug
        );
    }

    @Test
    void deleteCategory_returnsNoContent() {
        String slug = "test-category";
        doNothing().when(categoryService).deleteCategory(slug);

        var response = categoryController.deleteCategory(slug);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(categoryService).deleteCategory(slug);
    }
}