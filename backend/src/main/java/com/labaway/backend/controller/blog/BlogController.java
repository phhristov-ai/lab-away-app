package com.labaway.backend.controller;

import com.labaway.backend.dto.blog.BlogDto;
import com.labaway.backend.dto.blog.BlogPreviewDto;
import com.labaway.backend.dto.blog.BlogResponseDto;
import com.labaway.backend.enums.Language;
import com.labaway.backend.service.blog.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;

    @GetMapping
    public List<BlogPreviewDto> getAllBlogsForPreiew(@RequestParam(defaultValue = "EN") Language lang) {
        return blogService.getAllBlogsForPreview(lang);
    }

    @GetMapping("/random")
    public List<BlogPreviewDto> getRandomBlogs(
            @RequestParam(defaultValue = "EN") Language lang) {
        return blogService.getRandomBlogPreviews(lang);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<BlogResponseDto> getBySlug(
            @PathVariable String slug,
            @RequestParam(defaultValue = "EN") Language lang) {
        return ResponseEntity.ok(blogService.getBlogBySlug(slug, lang));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BlogResponseDto> createBlog(
            @RequestPart("blog") @Valid BlogDto blogDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            BlogResponseDto createdBlog = blogService.createBlog(blogDto, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBlog);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{slug}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BlogResponseDto> updateBlog(
            @PathVariable String slug,
            @RequestPart("blog") @Valid BlogDto blogDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            BlogResponseDto updatedBlog = blogService.updateBlog(slug, blogDto, file);
            return ResponseEntity.ok(updatedBlog);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> delete(@PathVariable String slug) {
        blogService.deleteBlog(slug);
        return ResponseEntity.noContent().build();
    }
}
