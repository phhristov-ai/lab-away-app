package com.labaway.backend.entity.repository;

import com.labaway.backend.dto.blog.BlogPreviewProjection;
import com.labaway.backend.entity.blog.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BlogRepository extends JpaRepository<Blog, UUID> {
    Optional<Blog> findBySlug(String slug);
    void deleteBySlug(String slug);

    @Query(value = "SELECT * FROM blog_preview_view WHERE language = :language", nativeQuery = true)
    List<BlogPreviewProjection> findAllBlogsForPreview(@Param("language") String language);

    @Query(value = "SELECT * FROM blog_preview_view WHERE language = :language ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<BlogPreviewProjection> findRandomBlogsByLanguage(@Param("language") String language);
}
