package com.labaway.backend.controller;

import com.labaway.backend.dto.blog.*;
import com.labaway.backend.enums.Language;
import com.labaway.backend.service.BlogService;
import com.labaway.backend.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BlogControllerTest {
    @InjectMocks
    private BlogController blogController;
    @Mock
    private BlogService blogService;
    @Mock
    private S3Service s3Service;
    private String slug;
    private BlogResponseDto blogResponseDto;
    private BlogDto blogDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        slug = "test-blog";

        blogResponseDto = createBlogResponseDto(slug, "Test Blog", "Content goes here");
        blogDto = createCreateBlogDto("Test Blog", "Content goes here");
    }

    @Test
    void getAll_returnsListOfBlogs() {
        BlogPreviewDto dto = createSampleBlogPreviewDto();

        when(blogService.getAllBlogsForPreview(Language.EN)).thenReturn(List.of(dto));

        List<BlogPreviewDto> result = blogController.getAllBlogsForPreiew(Language.EN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Blog");
        verify(blogService).getAllBlogsForPreview(Language.EN);
    }

    private BlogPreviewDto createSampleBlogPreviewDto() {
        return BlogPreviewDto.builder()
                .slug("test-blog")
                .title("Test Blog")
                .author("John Doe")
                .imageUrl("https://example.com/test.jpg")
                .excerpt("Test excerpt")
                .readingTime(2)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .categories(Collections.emptyList())
                .build();
    }

    @Test
    void getById_returnsBlogDto() {
        String slug = "test-blog";
        when(blogService.getBlogBySlug(slug, Language.EN)).thenReturn(blogResponseDto);
        ResponseEntity<BlogResponseDto> response = blogController.getBySlug(slug, Language.EN);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(blogResponseDto);
        verify(blogService).getBlogBySlug(slug, Language.EN);
    }

    @Test
    void create_returnsCreatedBlogDto() throws IOException {
        MockMultipartFile file = createMockImageFile();
        String expectedUrl = "https://bucket.s3.amazonaws.com/blog-images/blog-slug/image.jpg";
        when(s3Service.uploadFile(file)).thenReturn(expectedUrl);
        when(blogService.createBlog(blogDto, file)).thenReturn(blogResponseDto);

        ResponseEntity<BlogResponseDto> response = blogController.createBlog(blogDto, file);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(blogResponseDto);
        verify(blogService).createBlog(blogDto, file);
    }

    @Test
    void create_withNullFile_returnsCreatedBlogDto() throws IOException {
        when(blogService.createBlog(blogDto, null)).thenReturn(blogResponseDto);

        ResponseEntity<BlogResponseDto> response = blogController.createBlog(blogDto, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(blogResponseDto);
        verify(blogService).createBlog(blogDto, null);
    }

    @Test
    void create_whenIOExceptionThrown_returnsInternalServerError() throws IOException {
        MockMultipartFile file = createMockImageFile();
        when(blogService.createBlog(blogDto, file))
                .thenThrow(new IOException("Upload failed"));

        ResponseEntity<BlogResponseDto> response = blogController.createBlog(blogDto, file);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
        verify(blogService).createBlog(blogDto, file);
    }


    @Test
    void delete_removesBlog() {
        doNothing().when(blogService).deleteBlog(slug);

        ResponseEntity<Void> response = blogController.delete(slug);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(blogService).deleteBlog(slug);
    }

    @Test
    void updateBlog_shouldReturnUpdatedBlogDto() throws IOException {
        String slug = "test-blog";
        BlogDto updateDto = createCreateBlogDto("Title", "Content");
        BlogResponseDto updatedBlogDto = createBlogResponseDto(slug, "New Title", "NEw Content");
        MultipartFile file = createMockImageFile();
        when(blogService.updateBlog(eq(slug), any(BlogDto.class), any(MultipartFile.class))).thenReturn(updatedBlogDto);
        ResponseEntity<BlogResponseDto> response = blogController.updateBlog(slug, updateDto, file);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(blogService).updateBlog(slug, updateDto, file);
    }

    @Test
    void getRandomProducts_shouldReturnListOfProductPreviews() {
        BlogPreviewDto previewDto1 = createSampleBlogPreviewDto();
        BlogPreviewDto previewDto2 = createSampleBlogPreviewDto();

        when(blogService.getRandomBlogPreviews(Language.EN)).thenReturn(List.of(previewDto1, previewDto2));

        var result = blogController.getRandomBlogs(Language.EN);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSlug()).isEqualTo("test-blog");
        assertThat(result.get(1).getSlug()).isEqualTo("test-blog");

        verify(blogService, times(1)).getRandomBlogPreviews(Language.EN);
    }
    private MockMultipartFile createMockImageFile() {
        return new MockMultipartFile(
                "file",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image-content".getBytes()
        );
    }

    private static BlogResponseDto createBlogResponseDto(String slug, String title, String content) {
        return BlogResponseDto.builder()
                .title(title)
                .content(content)
                .build();
    }

    private static BlogDto createCreateBlogDto(String title, String content) {
        TranslationDto translation = TranslationDto.builder()
                .language(Language.EN)
                .title(title)
                .content(content)
                .build();

        return BlogDto.builder()
                .author("John Doe")
                .categorySlugs(List.of("tech", "java"))
                .translation(translation)
                .build();
    }

}
