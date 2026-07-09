package com.labaway.backend.unit.service.blog;

import com.labaway.backend.dto.blog.*;
import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.image.ImageUrls;
import com.labaway.backend.entity.blog.Blog;
import com.labaway.backend.entity.blog.BlogTranslation;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.category.CategoryTranslation;
import com.labaway.backend.entity.repository.blog.BlogRepository;
import com.labaway.backend.entity.repository.blog.BlogTranslationRepository;
import com.labaway.backend.entity.repository.category.CategoryRepository;
import com.labaway.backend.enums.Language;
import com.labaway.backend.exception.ResourceNotFoundException;
import com.labaway.backend.service.blog.BlogService;
import com.labaway.backend.service.media.MediaService;
import com.labaway.backend.service.storage.S3Service;
import com.labaway.backend.transformer.blog.BlogTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class BlogServiceTest {

    @InjectMocks
    private BlogService blogService;
    @Mock
    private BlogRepository blogRepository;
    @Mock
    private BlogTransformer blogTransformer;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BlogTranslationRepository blogTranslationRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private MediaService mediaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBlogsForPreview_returnsMappedDtos() {
        Language lang = Language.EN;
        List<BlogPreviewProjection> projections = List.of(mock(BlogPreviewProjection.class), mock(BlogPreviewProjection.class));
        List<BlogPreviewDto> dtos = List.of(
                createBlogPreviewDto("slug1", "Title 1"),
                createBlogPreviewDto("slug2", "Title 2")
        );

        when(blogRepository.findAllBlogsForPreview(lang.name())).thenReturn(projections);
        when(blogTransformer.mapToBlogPreviewDtos(projections)).thenReturn(dtos);

        List<BlogPreviewDto> result = blogService.getAllBlogsForPreview(lang);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).slug()).isEqualTo("slug1");
        assertThat(result.get(1).slug()).isEqualTo("slug2");

        verify(blogRepository).findAllBlogsForPreview(lang.name());
        verify(blogTransformer).mapToBlogPreviewDtos(projections);
    }

    private BlogPreviewDto createBlogPreviewDto(String slug, String title) {
        return new BlogPreviewDto(
                slug,
                "John Doe",
                createImageUrls(),
                title,
                "Sample excerpt",
                5,
                Instant.now(),
                Instant.now(),
                List.of()
        );
    }

    private ImageUrls createImageUrls() {
        return new ImageUrls(
                "https://example.com/images/small.jpg",
                "https://example.com/images/medium.jpg",
                "https://example.com/images/large.jpg"
        );
    }

    @Test
    void getBlogBySlug_returnsMappedDto() {
        String title = "STDs";
        String categorySlug = "std-tests";
        String blogSlug = "std-post";
        String content = "STDs are getting more common...";

        CategoryDto categoryDto = new CategoryDto(title, categorySlug);

        Blog blog = createTestBlog(blogSlug, createCategory());
        BlogResponseDto blogResponseDto =
                createBlogResponseDto(blogSlug, categoryDto, title, content);

        when(blogRepository.findBySlug(blogSlug)).thenReturn(Optional.of(blog));
        when(blogTransformer.toDto(blog, Language.EN)).thenReturn(blogResponseDto);

        BlogResponseDto result = blogService.getBlogBySlug(blogSlug, Language.EN);

        assertThat(result).isNotNull();
        assertThat(result.slug()).isEqualTo(blogSlug);
        assertThat(result.title()).isEqualTo(title);
        assertThat(result.content()).isEqualTo(content);
        assertThat(result.author()).isEqualTo("Author");
        assertThat(result.categories()).containsExactly(categoryDto);

        verify(blogRepository).findBySlug(blogSlug);
        verify(blogTransformer).toDto(blog, Language.EN);
    }

    @Test
    void getBlogById_throwsExceptionWhenNotFound() {
        String blogSlug = "std-post";
        mockFindBlogBySlug(Optional.empty(), blogSlug);
        assertThatThrownBy(() -> blogService.getBlogBySlug(blogSlug, Language.EN))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Blog not found");
    }

    @Test
    void createBlog_savesAndReturnsMappedDto() throws IOException {
        MultipartFile file = createMockImageFile();
        Category category = createCategory();
        BlogDto blogDto = createBlogDto();
        BlogResponseDto blogResponseDto = createBlogResponseDto();

        Blog blog = createTestBlog("std-post", category);

        when(blogTransformer.fromCreateDto(blogDto)).thenReturn(blog);
        when(mediaService.processAndUploadImage(file)).thenReturn(createImageUrls());

        mockSaveBlog(blog);
        mockFindCategories(List.of(category));
        mockTransformToDto(blog, blogResponseDto);

        BlogResponseDto result = blogService.createBlog(blogDto, file);

        assertThat(result).isNotNull();
        assertThat(result.slug()).isEqualTo("std-post");
        assertThat(result.author()).isEqualTo("Author");
        assertThat(result.title()).isEqualTo("STDs");
        assertThat(result.content()).isEqualTo("STDs are getting more common...");
        assertThat(result.categories())
                .containsExactly(new CategoryDto("STDs", "std-tests"));

        assertThat(blog.getImageUrlSmall())
                .isEqualTo("https://example.com/images/small.jpg");
        assertThat(blog.getImageUrlMedium())
                .isEqualTo("https://example.com/images/medium.jpg");
        assertThat(blog.getImageUrlLarge())
                .isEqualTo("https://example.com/images/large.jpg");

        verify(blogTransformer).fromCreateDto(blogDto);
        verify(mediaService).processAndUploadImage(file);
    }

    private BlogDto createBlogDto() {
        return new BlogDto(
                "Author",
                List.of("std-tests"),
                new TranslationDto(
                        Language.EN,
                        "STDs",
                        "STDs are getting more common..."
                )
        );
    }

    private BlogResponseDto createBlogResponseDto() {
        return new BlogResponseDto(
                "std-post",
                "Author",
                createImageUrls(),
                "STDs",
                "STDs are getting more common...",
                List.of(new CategoryDto("STDs", "std-tests")),
                Instant.now(),
                Instant.now(),
                10
        );
    }

    @Test
    void createBlog_withoutImage_setsNoImageUrls() throws IOException {
        BlogDto blogDto = createBlogDto();
        Category category = createCategory();
        BlogResponseDto blogResponseDto = createBlogResponseDto();

        Blog blog = createTestBlog("std-post", category);

        when(blogTransformer.fromCreateDto(blogDto)).thenReturn(blog);

        mockSaveBlog(blog);
        mockFindCategories(List.of(category));
        mockTransformToDto(blog, blogResponseDto);

        BlogResponseDto result = blogService.createBlog(blogDto, null);

        verify(blogTransformer).fromCreateDto(blogDto);
        verify(blogTransformer).toDto(eq(blog), any(Language.class));
        verify(mediaService, never()).processAndUploadImage(any());

        assertThat(result).isNotNull();
        assertThat(result.slug()).isEqualTo("std-post");
        assertThat(result.author()).isEqualTo("Author");
        assertThat(result.title()).isEqualTo("STDs");
        assertThat(result.content()).isEqualTo("STDs are getting more common...");
        assertThat(result.categories())
                .containsExactly(new CategoryDto("STDs", "std-tests"));

        assertThat(blog.getImageUrlSmall()).isNull();
        assertThat(blog.getImageUrlMedium()).isNull();
        assertThat(blog.getImageUrlLarge()).isNull();
    }

    @Test
    void createBlog_withEmptyFile_doesNotUpload() throws IOException {
        BlogDto blogDto = createBlogDto();
        Category category = createCategory();
        BlogResponseDto blogResponseDto = createBlogResponseDto();

        Blog blog = createTestBlog("std-post", category);

        when(blogTransformer.fromCreateDto(blogDto)).thenReturn(blog);

        mockSaveBlog(blog);
        mockFindCategories(List.of(category));
        mockTransformToDto(blog, blogResponseDto);

        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);

        BlogResponseDto result = blogService.createBlog(blogDto, emptyFile);

        assertThat(result).isNotNull();
        assertThat(result.slug()).isEqualTo("std-post");
        assertThat(result.author()).isEqualTo("Author");
        assertThat(result.title()).isEqualTo("STDs");
        assertThat(result.content()).isEqualTo("STDs are getting more common...");
        assertThat(result.categories())
                .containsExactly(new CategoryDto("STDs", "std-tests"));

        verify(blogTransformer).fromCreateDto(blogDto);
        verify(mediaService, never()).processAndUploadImage(any());

        assertThat(blog.getImageUrlSmall()).isNull();
        assertThat(blog.getImageUrlMedium()).isNull();
        assertThat(blog.getImageUrlLarge()).isNull();
    }


    private MockMultipartFile createMockImageFile() {
        return new MockMultipartFile(
                "file",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image-content".getBytes()
        );
    }

    @Test
    void updateBlog_shouldUpdateAndReturnDto() throws IOException {
        String blogSlug = "std-post";
        Category category = createCategory();

        Blog blog = createTestBlog(blogSlug, category);
        BlogDto updateDto = createUpdateBlogDto(category.getSlug());
        BlogResponseDto blogResponseDto = createBlogResponseDto();
        MultipartFile file = createMockImageFile();

        ImageUrls imageUrls = createImageUrls();

        mockFindBlogBySlug(Optional.of(blog), blogSlug);

        when(categoryRepository.findBySlugIn(updateDto.categorySlugs()))
                .thenReturn(List.of(category));
        when(mediaService.processAndUploadImage(file))
                .thenReturn(imageUrls);

        mockSaveBlog(blog);
        mockTransformToDto(blog, blogResponseDto);

        BlogResponseDto result = blogService.updateBlog(blogSlug, updateDto, file);

        assertBlogDto(result, "STDs", "STDs");

        assertThat(blog.getImageUrlSmall())
                .isEqualTo("https://example.com/images/small.jpg");
        assertThat(blog.getImageUrlMedium())
                .isEqualTo("https://example.com/images/medium.jpg");
        assertThat(blog.getImageUrlLarge())
                .isEqualTo("https://example.com/images/large.jpg");

        verify(blogRepository).findBySlug(blogSlug);
        verify(blogRepository).save(blog);
        verify(categoryRepository).findBySlugIn(updateDto.categorySlugs());
        verify(mediaService).processAndUploadImage(file);
        verify(blogTransformer).updateEntity(blog, updateDto);
        verify(blogTransformer).toDto(eq(blog), any(Language.class));
    }


    @Test
    void deleteBlog_deletesBySlug() {
        String blogSlug = "std-post";
        Blog blog = createTestBlog(blogSlug, createCategory());
        blog.setId(UUID.randomUUID());

        when(blogRepository.findBySlug(blogSlug)).thenReturn(Optional.of(blog));
        blogService.deleteBlog(blogSlug);

        verify(blogRepository).delete(blog);
    }

    @Test
    void testGetRandomBlogPreviews() {
        Language lang = Language.EN;

        BlogPreviewProjection projection1 = mock(BlogPreviewProjection.class);
        BlogPreviewProjection projection2 = mock(BlogPreviewProjection.class);

        BlogPreviewDto dto1 =createBlogPreviewDto("slug1", "Title 1");
        BlogPreviewDto dto2 = createBlogPreviewDto("slug2", "Title 2");

        when(blogRepository.findRandomBlogsByLanguage(lang.name()))
                .thenReturn(List.of(projection1, projection2));

        when(blogTransformer.mapToBlogPreviewDto(projection1)).thenReturn(dto1);
        when(blogTransformer.mapToBlogPreviewDto(projection2)).thenReturn(dto2);

        List<BlogPreviewDto> result = blogService.getRandomBlogPreviews(lang);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).slug()).isEqualTo("slug1");
        assertThat(result.get(1).slug()).isEqualTo("slug2");

        verify(blogRepository).findRandomBlogsByLanguage(lang.name());
        verify(blogTransformer).mapToBlogPreviewDto(projection1);
        verify(blogTransformer).mapToBlogPreviewDto(projection2);
    }

    private BlogResponseDto createBlogResponseDto(
            String slug,
            CategoryDto categoryDto,
            String title,
            String content
    ) {
        return new BlogResponseDto(
                slug,
                "Author",
                createImageUrls(),
                title,
                content,
                List.of(categoryDto),
                Instant.now(),
                Instant.now(),
                10
        );
    }

    private Blog createTestBlog(String slug, Category category) {
        Blog blog = Blog.builder()
                .slug(slug)
                .categories(Set.of(category))
                .build();

        BlogTranslation translation = BlogTranslation.builder()
                .blog(blog)
                .language(Language.EN)
                .title("Test Blog")
                .content("Content goes here")
                .readingTime(10)
                .build();

        blog.setTranslations(Set.of(translation));

        return blog;
    }

    private BlogDto createUpdateBlogDto(String categorySlug) {
        return new BlogDto(
                "Updated Author",
                List.of(categorySlug),
                new TranslationDto(
                        Language.EN,
                        "Updated Title",
                        "Updated blog content..."
                )
        );
    }

    private Category createCategory() {
        Category category = new Category();
        category.setSlug("std-tests");

        CategoryTranslation translation = new CategoryTranslation();
        translation.setCategory(category);
        translation.setLanguage(Language.EN);
        translation.setName("STDs");

        category.getTranslations().add(translation);

        return category;
    }

    private void assertBlogDto(
            BlogResponseDto dto,
            String expectedTitle,
            String expectedCategoryName
    ) {
        assertThat(dto).isNotNull();
        assertThat(dto.title()).isEqualTo(expectedTitle);
        assertThat(dto.categories()).hasSize(1);
        assertThat(dto.categories().get(0).name())
                .isEqualTo(expectedCategoryName);
    }

    private void mockFindBlogBySlug(Optional<Blog> blogOpt, String blogSlug) {
        when(blogRepository.findBySlug(blogSlug)).thenReturn(blogOpt);
    }

    private void mockFindCategories(List<Category> categories) {
        when(categoryRepository.findBySlugIn(anyList())).thenReturn(categories);
    }

    private void mockTransformToDto(Blog blog, BlogResponseDto dto) {
        when(blogTransformer.toDto(eq(blog), any(Language.class))).thenReturn(dto);
    }

    private void mockSaveBlog(Blog blog) {
        when(blogRepository.saveAndFlush(blog)).thenReturn(blog);
        when(blogRepository.save(blog)).thenReturn(blog);
    }
}