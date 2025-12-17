package com.labaway.backend.entity.repository.category;

import com.labaway.backend.entity.category.Category;
import com.labaway.backend.enums.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    void deleteBySlug(String slug);
    boolean existsBySlug(String slug);

    @Query("""
    SELECT c FROM Category c
    LEFT JOIN FETCH c.translations t
    WHERE t.language = :language
    """)
    List<Category> findAllByLanguage(@Param("language") Language language);

    @Query("""
        SELECT c FROM Category c
        LEFT JOIN c.translations t
        WHERE c.slug = :slug AND (t.language = :language OR t IS NULL)
        """)
    Optional<Category> findBySlugAndLanguage(@Param("slug") String slug, @Param("language") Language language);

    @Query("""
    SELECT DISTINCT c FROM Category c
    LEFT JOIN FETCH c.translations t
    WHERE c.slug IN :slugs AND t.language = :language
""")
    List<Category> findBySlugInAndLanguage(@Param("slugs") List<String> slugs, @Param("language") Language language);

    @Query("""
    SELECT c FROM Category c
    WHERE c.slug = :slug
""")
    Optional<Category> findBySlug(@Param("slug") String slug);

    List<Category> findBySlugIn(List<String> slugs);

}
