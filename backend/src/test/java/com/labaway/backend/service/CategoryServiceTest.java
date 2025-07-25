package com.labaway.backend.service;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.category.CreateCategoryDto;
import com.labaway.backend.dto.category.CreateCategoryTranslationDto;
import com.labaway.backend.dto.category.UpdateCategoryDto;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.category.CategoryTranslation;
import com.labaway.backend.entity.repository.CategoryRepository;
import com.labaway.backend.entity.repository.CategoryTranslationRepository;
import com.labaway.backend.enums.Language;
import com.labaway.backend.transformer.CategoryTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryTransformer categoryTransformer;
    @Mock
    private CategoryTranslationRepository categoryTranslationRepository;
    @InjectMocks
    private CategoryService categoryService;
    private final UUID categoryId = UUID.randomUUID();
    private Category category;
    private CategoryDto categoryDto;
    private CreateCategoryDto createDto;
    private UpdateCategoryDto updateDto;
    private String slug;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        slug = "test-category";

        category = createTestCategory();
        categoryDto = createTestCategoryDto();
        createDto = createTestCreateCategoryDto();
        updateDto = createTestUpdateCategoryDto();
    }

    @Test
    void getAllCategories_returnsMappedDtos() {
        List<Category> categories = List.of(category);
        when(categoryRepository.findAllByLanguage(Language.EN)).thenReturn(categories);
        when(categoryTransformer.toDto(category, Language.EN)).thenReturn(categoryDto);

        List<CategoryDto> result = categoryService.getAllCategories(Language.EN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Tech");
        verify(categoryRepository).findAllByLanguage(Language.EN);
    }


    @Test
    void getCategoryBySlug_returnsMappedDto() {
        when(categoryRepository.findBySlugAndLanguage(slug, Language.EN)).thenReturn(Optional.of(category));
        when(categoryTransformer.toDto(category, Language.EN)).thenReturn(categoryDto);

        CategoryDto result = categoryService.getCategoryBySlug(slug, Language.EN);

        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo(slug);
        verify(categoryRepository).findBySlugAndLanguage(slug, Language.EN);
    }

    @Test
    void getCategoryBySlug_throwsExceptionWhenNotFound() {
        when(categoryRepository.findBySlugAndLanguage(slug, Language.EN)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryBySlug(slug, Language.EN))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found with slug: " + slug);
    }

    @Test
    void createCategory_savesAndReturnsDto() {
        when(categoryTransformer.fromCreateDto(createDto)).thenReturn(category);
        CategoryTranslation translation = createCategoryTranslation(category, Language.EN, "Tech");

        when(categoryTransformer.fromCreateTranslationDto(eq(createDto.getTranslation()), any(Category.class)))
                .thenReturn(translation);
        when(categoryRepository.saveAndFlush(category)).thenReturn(category);
        when(categoryTransformer.toDto(category, Language.EN)).thenReturn(categoryDto);

        CategoryDto result = categoryService.createCategory(createDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Tech");
        verify(categoryRepository).saveAndFlush(category);
    }


    private CategoryTranslation createCategoryTranslation(Category category, Language language, String name) {
        return CategoryTranslation.builder()
                .language(language)
                .name(name)
                .category(category)
                .build();
    }

    @Test
    void updateCategory_updatesAndReturnsDto() {
        when(categoryRepository.findBySlugAndLanguage(slug, Language.EN)).thenReturn(Optional.of(category));
        doAnswer(invocation -> {
            UpdateCategoryDto dtoArg = invocation.getArgument(0);
            Category entityArg = invocation.getArgument(1);
            entityArg.setSlug(dtoArg.getSlug());
            return null;
        }).when(categoryTransformer).updateEntityFromDto(updateDto, category, Language.EN);

        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryTransformer.toDto(category, Language.EN)).thenReturn(categoryDto);

        CategoryDto result = categoryService.updateCategory(slug, updateDto, Language.EN);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Tech");
        verify(categoryRepository).save(category);
    }

    @Test
    void updateCategory_throwsExceptionIfNotFound() {
        when(categoryRepository.findBySlugAndLanguage(slug, Language.EN)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(slug, updateDto, Language.EN))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found with slug " + slug);
    }

    @Test
    void deleteCategory_deletesBySlug() {
        categoryService.deleteCategory(slug);
        verify(categoryRepository).deleteBySlug(slug);
    }

    @Test
    void addOrUpdateTranslation_updatesExistingTranslation() {
        CreateCategoryTranslationDto dto = new CreateCategoryTranslationDto();
        dto.setLanguage(Language.EN);
        dto.setName("Updated Name");

        CategoryTranslation existingTranslation = createCategoryTranslation(category, Language.EN, "Old Name");

        when(categoryRepository.findBySlug(slug)).thenReturn(Optional.of(category));
        when(categoryTranslationRepository.findByCategoryIdAndLanguage(category.getId(), Language.EN))
                .thenReturn(Optional.of(existingTranslation));
        when(categoryTranslationRepository.save(any(CategoryTranslation.class))).thenAnswer(i -> i.getArgument(0));

        categoryService.addOrUpdateTranslation(slug, dto);

        verify(categoryRepository).findBySlug(slug);
        verify(categoryTranslationRepository).findByCategoryIdAndLanguage(category.getId(), Language.EN);

        ArgumentCaptor<CategoryTranslation> captor = ArgumentCaptor.forClass(CategoryTranslation.class);
        verify(categoryTranslationRepository).save(captor.capture());

        CategoryTranslation savedTranslation = captor.getValue();
        assertThat(savedTranslation.getName()).isEqualTo("Updated Name");
        assertThat(savedTranslation.getLanguage()).isEqualTo(Language.EN);
        assertThat(savedTranslation.getCategory()).isEqualTo(category);
    }

    @Test
    void addOrUpdateTranslation_createsNewTranslationIfNotExists() {
        CreateCategoryTranslationDto dto = new CreateCategoryTranslationDto();
        dto.setLanguage(Language.EN);
        dto.setName("New Name");

        when(categoryRepository.findBySlug(slug)).thenReturn(Optional.of(category));
        when(categoryTranslationRepository.findByCategoryIdAndLanguage(category.getId(), Language.EN))
                .thenReturn(Optional.empty());
        when(categoryTranslationRepository.save(any(CategoryTranslation.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        categoryService.addOrUpdateTranslation(slug, dto);

        // Assert
        verify(categoryRepository).findBySlug(slug);
        verify(categoryTranslationRepository).findByCategoryIdAndLanguage(category.getId(), Language.EN);

        ArgumentCaptor<CategoryTranslation> captor = ArgumentCaptor.forClass(CategoryTranslation.class);
        verify(categoryTranslationRepository).save(captor.capture());

        CategoryTranslation savedTranslation = captor.getValue();
        assertThat(savedTranslation.getName()).isEqualTo("New Name");
        assertThat(savedTranslation.getLanguage()).isEqualTo(Language.EN);
        assertThat(savedTranslation.getCategory()).isEqualTo(category);
    }



    private Category createTestCategory() {
        Category category = new Category();
        category.setId(categoryId);
        category.setSlug(slug);
        return category;
    }

    private CategoryDto createTestCategoryDto() {
        return CategoryDto.builder()
                .name("Tech")
                .slug(slug)
                .build();
    }

    private CreateCategoryDto createTestCreateCategoryDto() {
        CreateCategoryDto dto = new CreateCategoryDto();
        return dto;
    }

    private UpdateCategoryDto createTestUpdateCategoryDto() {
        UpdateCategoryDto dto = new UpdateCategoryDto();
        dto.setName("Tech Updated");
        return dto;
    }

}
