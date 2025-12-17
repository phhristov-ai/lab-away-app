package com.labaway.backend.entity.repository;

import com.labaway.backend.entity.blog.BlogTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BlogTranslationRepository extends JpaRepository<BlogTranslation, UUID> {

}