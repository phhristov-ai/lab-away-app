package com.labaway.backend.service;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.category.CreateCategoryDto;
import com.labaway.backend.dto.category.CreateCategoryTranslationDto;
import com.labaway.backend.dto.category.UpdateCategoryDto;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.category.CategoryTranslation;
import com.labaway.backend.entity.repository.CategoryTranslationRepository;
import com.labaway.backend.enums.Language;
import com.labaway.backend.exception.CategoryNotFoundException;
import com.labaway.backend.entity.repository.CategoryRepository;
import com.labaway.backend.transformer.CategoryTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryTransformer categoryTransformer;
    private final CategoryTranslationRepository categoryTranslationRepository;

    public List<CategoryDto> getAllCategories(Language language) {
        List<Category> categories = categoryRepository.findAllByLanguage(language);
        return categories.stream()
                .map(category -> categoryTransformer.toDto(category, language))
                .toList();
    }

    public CategoryDto getCategoryBySlug(String slug, Language language) {
        Category category = categoryRepository.findBySlugAndLanguage(slug, language)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with slug: " + slug));

        return categoryTransformer.toDto(category, language);
    }

    @Transactional
    public CategoryDto createCategory(CreateCategoryDto dto) {
        if (categoryRepository.existsBySlug(dto.getSlug())) {
            throw new CategoryNotFoundException("Category with slug already exists");
        }
        Category category = categoryTransformer.fromCreateDto(dto);
        Category savedCategory = categoryRepository.saveAndFlush(category);

        CategoryTranslation translation = categoryTransformer.fromCreateTranslationDto(dto.getTranslation(), savedCategory);
        categoryTranslationRepository.save(translation);

        return categoryTransformer.toDto(savedCategory, translation.getLanguage());
    }


    public CategoryDto updateCategory(String slug, UpdateCategoryDto dto, Language language) {
        Category existing = categoryRepository.findBySlugAndLanguage(slug, language)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with slug " + slug));

        categoryTransformer.updateEntityFromDto(dto, existing, language);
        Category saved = categoryRepository.save(existing);

        return categoryTransformer.toDto(saved, language);
    }


    @Transactional
    public void addOrUpdateTranslation(String slug, CreateCategoryTranslationDto dto) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        Language language = dto.getLanguage();

        CategoryTranslation translation = categoryTranslationRepository
                .findByCategoryIdAndLanguage(category.getId(), language)
                .orElseGet(() -> {
                    CategoryTranslation newTranslation = new CategoryTranslation();
                    newTranslation.setCategory(category);
                    newTranslation.setLanguage(language);
                    return newTranslation;
                });

        translation.setName(dto.getName());
        categoryTranslationRepository.save(translation);
    }

    @Transactional
    public void deleteCategory(String slug) {
        categoryRepository.deleteBySlug(slug);
    }
}
