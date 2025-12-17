package com.labaway.backend.entity.repository.category;

import com.labaway.backend.entity.category.CategoryTranslation;
import com.labaway.backend.enums.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoryTranslationRepository extends JpaRepository<CategoryTranslation, UUID> {
    Optional<CategoryTranslation> findByCategoryIdAndLanguage(UUID categoryId, Language language);
}