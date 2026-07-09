package com.labaway.backend.unit.service.category;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.category.CreateCategoryDto;
import com.labaway.backend.dto.category.CreateCategoryTranslationDto;
import com.labaway.backend.dto.category.UpdateCategoryDto;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.category.CategoryTranslation;
import com.labaway.backend.entity.repository.category.CategoryRepository;
import com.labaway.backend.entity.repository.category.CategoryTranslationRepository;
import com.labaway.backend.enums.Language;
import com.labaway.backend.service.category.CategoryService;
import com.labaway.backend.transformer.category.CategoryTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryTransformer categoryTransformer;

    @Mock
    private CategoryTranslationRepository categoryTranslationRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAllCategories_returnsMappedDtos() {
        Category category = createTestCategory();
        CategoryDto categoryDto = createTestCategoryDto();

        List<Category> categories = List.of(category);

        when(categoryRepository.findAllByLanguage(Language.EN))
                .thenReturn(categories);

        when(categoryTransformer.toDto(category, Language.EN))
                .thenReturn(categoryDto);

        List<CategoryDto> result = categoryService.getAllCategories(Language.EN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Tech");

        verify(categoryRepository).findAllByLanguage(Language.EN);
        verify(categoryTransformer).toDto(category, Language.EN);
    }

    @Test
    void getCategoryBySlug_returnsMappedDto() {
        String slug = "test-category";

        Category category = createTestCategory();
        CategoryDto categoryDto = createTestCategoryDto();

        when(categoryRepository.findBySlugAndLanguage(slug, Language.EN))
                .thenReturn(Optional.of(category));

        when(categoryTransformer.toDto(category, Language.EN))
                .thenReturn(categoryDto);

        CategoryDto result = categoryService.getCategoryBySlug(slug, Language.EN);

        assertThat(result).isNotNull();
        assertThat(result.slug()).isEqualTo(slug);

        verify(categoryRepository).findBySlugAndLanguage(slug, Language.EN);
        verify(categoryTransformer).toDto(category, Language.EN);
    }

    @Test
    void getCategoryBySlug_throwsExceptionWhenNotFound() {
        String slug = "missing-category";

        when(categoryRepository.findBySlugAndLanguage(slug, Language.EN))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryBySlug(slug, Language.EN))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found with slug: " + slug);

        verify(categoryRepository).findBySlugAndLanguage(slug, Language.EN);
        verifyNoInteractions(categoryTransformer);
    }

    @Test
    void createCategory_savesAndReturnsDto() {
        Category category = createTestCategory();
        CategoryDto categoryDto = createTestCategoryDto();

        CategoryTranslation translation =
                createCategoryTranslation(category, Language.EN, "Tech");

        CreateCategoryDto createDto =
                createTestCreateCategoryDto(createTestCreateCategoryTranslationDto());

        when(categoryTransformer.fromCreateDto(createDto))
                .thenReturn(category);

        when(categoryTransformer.fromCreateTranslationDto(
                eq(createDto.translation()),
                any(Category.class)
        )).thenReturn(translation);

        when(categoryRepository.saveAndFlush(category))
                .thenReturn(category);

        when(categoryTransformer.toDto(category, Language.EN))
                .thenReturn(categoryDto);

        CategoryDto result = categoryService.createCategory(createDto);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Tech");

        verify(categoryTransformer).fromCreateDto(createDto);
        verify(categoryRepository).saveAndFlush(category);
        verify(categoryTransformer).toDto(category, Language.EN);
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
        String slug = "test-category";

        Category category = createTestCategory();
        CategoryDto categoryDto = createTestCategoryDto();

        UpdateCategoryDto updateDto = createTestUpdateCategoryDto();

        when(categoryRepository.findBySlugAndLanguage(slug, Language.EN))
                .thenReturn(Optional.of(category));

        doAnswer(invocation -> {
            UpdateCategoryDto dtoArg = invocation.getArgument(0);
            Category entityArg = invocation.getArgument(1);

            entityArg.setSlug(dtoArg.slug());

            return null;
        }).when(categoryTransformer)
                .updateEntityFromDto(updateDto, category, Language.EN);

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(categoryTransformer.toDto(category, Language.EN))
                .thenReturn(categoryDto);

        CategoryDto result =
                categoryService.updateCategory(slug, updateDto, Language.EN);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Tech");

        verify(categoryRepository).findBySlugAndLanguage(slug, Language.EN);
        verify(categoryTransformer)
                .updateEntityFromDto(updateDto, category, Language.EN);
        verify(categoryRepository).save(category);
        verify(categoryTransformer).toDto(category, Language.EN);
    }

    @Test
    void updateCategory_throwsExceptionIfNotFound() {
        String slug = "missing-category";

        UpdateCategoryDto updateDto = createTestUpdateCategoryDto();

        when(categoryRepository.findBySlugAndLanguage(slug, Language.EN))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                categoryService.updateCategory(slug, updateDto, Language.EN)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found with slug " + slug);

        verify(categoryRepository)
                .findBySlugAndLanguage(slug, Language.EN);

        verifyNoInteractions(categoryTransformer);
    }

    @Test
    void deleteCategory_deletesBySlug() {
        String slug = "test-category";

        categoryService.deleteCategory(slug);

        verify(categoryRepository).deleteBySlug(slug);
    }

    @Test
    void addOrUpdateTranslation_updatesExistingTranslation() {
        String slug = "test-category";

        Category category = createTestCategory();

        CreateCategoryTranslationDto dto =
                new CreateCategoryTranslationDto(
                        Language.EN,
                        "Updated Name"
                );

        CategoryTranslation existingTranslation =
                createCategoryTranslation(
                        category,
                        Language.EN,
                        "Old Name"
                );

        when(categoryRepository.findBySlug(slug))
                .thenReturn(Optional.of(category));

        when(categoryTranslationRepository.findByCategoryIdAndLanguage(
                category.getId(),
                Language.EN
        )).thenReturn(Optional.of(existingTranslation));

        when(categoryTranslationRepository.save(any(CategoryTranslation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        categoryService.addOrUpdateTranslation(slug, dto);

        verify(categoryRepository).findBySlug(slug);

        verify(categoryTranslationRepository)
                .findByCategoryIdAndLanguage(
                        category.getId(),
                        Language.EN
                );

        ArgumentCaptor<CategoryTranslation> captor =
                ArgumentCaptor.forClass(CategoryTranslation.class);

        verify(categoryTranslationRepository).save(captor.capture());

        CategoryTranslation savedTranslation = captor.getValue();

        assertThat(savedTranslation.getName())
                .isEqualTo("Updated Name");

        assertThat(savedTranslation.getLanguage())
                .isEqualTo(Language.EN);

        assertThat(savedTranslation.getCategory())
                .isEqualTo(category);
    }

    @Test
    void addOrUpdateTranslation_createsNewTranslationIfNotExists() {
        String slug = "test-category";

        Category category = createTestCategory();

        CreateCategoryTranslationDto dto =
                new CreateCategoryTranslationDto(
                        Language.EN,
                        "New Name"
                );

        when(categoryRepository.findBySlug(slug))
                .thenReturn(Optional.of(category));

        when(categoryTranslationRepository.findByCategoryIdAndLanguage(
                category.getId(),
                Language.EN
        )).thenReturn(Optional.empty());

        when(categoryTranslationRepository.save(any(CategoryTranslation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        categoryService.addOrUpdateTranslation(slug, dto);

        verify(categoryRepository).findBySlug(slug);

        verify(categoryTranslationRepository)
                .findByCategoryIdAndLanguage(
                        category.getId(),
                        Language.EN
                );

        ArgumentCaptor<CategoryTranslation> captor =
                ArgumentCaptor.forClass(CategoryTranslation.class);

        verify(categoryTranslationRepository).save(captor.capture());

        CategoryTranslation savedTranslation = captor.getValue();

        assertThat(savedTranslation.getName())
                .isEqualTo("New Name");

        assertThat(savedTranslation.getLanguage())
                .isEqualTo(Language.EN);

        assertThat(savedTranslation.getCategory())
                .isEqualTo(category);
    }

    private Category createTestCategory() {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setSlug("test-category");
        category.setProducts(new HashSet<>());
        category.setTranslations(new HashSet<>());

        return category;
    }

    private CategoryDto createTestCategoryDto() {
        return new CategoryDto(
                "Tech",
                "test-category"
        );
    }

    private CreateCategoryDto createTestCreateCategoryDto(
            CreateCategoryTranslationDto translationDto
    ) {
        return new CreateCategoryDto(
                "test-category",
                translationDto
        );
    }

    private CreateCategoryTranslationDto createTestCreateCategoryTranslationDto() {
        return new CreateCategoryTranslationDto(
                Language.EN,
                "Tech"
        );
    }

    private UpdateCategoryDto createTestUpdateCategoryDto() {
        return new UpdateCategoryDto(
                "Tech Updated",
                "test-category"
        );
    }

}
