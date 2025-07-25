package com.labaway.backend.controller;

import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.category.CreateCategoryDto;
import com.labaway.backend.dto.category.CreateCategoryTranslationDto;
import com.labaway.backend.dto.category.UpdateCategoryDto;
import com.labaway.backend.enums.Language;
import com.labaway.backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(
            @RequestParam(defaultValue = "EN") Language lang) {
        return categoryService.getAllCategories(lang);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<CategoryDto> getCategoryBySlug(
            @PathVariable String slug,
            @RequestParam(defaultValue = "EN") Language lang) {
        return ResponseEntity.ok(categoryService.getCategoryBySlug(slug, lang));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CreateCategoryDto dto) {
        return new ResponseEntity<>(categoryService.createCategory(dto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{slug}/translations")
    public ResponseEntity<Void> addOrUpdateTranslation(
            @PathVariable String slug,
            @RequestBody @Valid CreateCategoryTranslationDto translationDto) {
        categoryService.addOrUpdateTranslation(slug, translationDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{slug}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable String slug,
                                                      @RequestBody UpdateCategoryDto dto,
                                                      @RequestParam(defaultValue = "EN") Language lang) {
        return ResponseEntity.ok(categoryService.updateCategory(slug, dto, lang));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String slug) {
        categoryService.deleteCategory(slug);
        return ResponseEntity.noContent().build();
    }
}
