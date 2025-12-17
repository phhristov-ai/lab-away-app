package com.labaway.backend.service;

import com.labaway.backend.dto.blog.*;
import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.dto.image.ImageUrls;
import com.labaway.backend.entity.blog.Blog;
import com.labaway.backend.entity.blog.BlogTranslation;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.repository.blog.BlogRepository;
import com.labaway.backend.entity.repository.blog.BlogTranslationRepository;
import com.labaway.backend.entity.repository.category.CategoryRepository;
import com.labaway.backend.enums.Language;
import com.labaway.backend.exception.ResourceNotFoundException;
import com.labaway.backend.service.blog.BlogService;
import com.labaway.backend.service.media.ImageService;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
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
    private ImageService imageService;
    private String slug;
    private String categorySlug;
    private Blog blog;
    private BlogResponseDto blogResponseDto;
    private Category category;
    private CategoryDto categoryDto;
    private BlogDto blogDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        slug = "test-blog";
        categorySlug = "test-category";

        setupCategory();
        setupDtos();
        blog = createTestBlog();
        blogDto = createSampleCreateBlogDto();
    }

    @Test
    void getAllBlogsForPreview_returnsMappedDtos() {
        Language lang = Language.EN;
        List<BlogPreviewProjection> projections = List.of(mock(BlogPreviewProjection.class), mock(BlogPreviewProjection.class));
        List<BlogPreviewDto> dtos = List.of(
                BlogPreviewDto.builder().slug("slug1").title("Title 1").build(),
                BlogPreviewDto.builder().slug("slug2").title("Title 2").build()
        );

        when(blogRepository.findAllBlogsForPreview(lang.name())).thenReturn(projections);
        when(blogTransformer.mapToBlogPreviewDtos(projections)).thenReturn(dtos);

        List<BlogPreviewDto> result = blogService.getAllBlogsForPreview(lang);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSlug()).isEqualTo("slug1");
        assertThat(result.get(1).getSlug()).isEqualTo("slug2");

        verify(blogRepository).findAllBlogsForPreview(lang.name());
        verify(blogTransformer).mapToBlogPreviewDtos(projections);
    }


    @Test
    void getBlogById_returnsMappedDto() {
        when(blogRepository.findBySlug(slug)).thenReturn(Optional.of(blog));

        when(blogTransformer.toDto(blog, Language.EN)).thenReturn(blogResponseDto);

        BlogResponseDto result = blogService.getBlogBySlug(slug, Language.EN);

        assertBlogDto(result);
        verify(blogRepository).findBySlug(slug);
        verify(blogTransformer).toDto(blog, Language.EN);
        assertThat(blogResponseDto).isNotNull();
        assertThat(blogResponseDto.getTitle()).isEqualTo("Test Blog");
    }

    @Test
    void getBlogById_throwsExceptionWhenNotFound() {
        mockFindBlogBySlug(Optional.empty());

        assertThatThrownBy(() -> blogService.getBlogBySlug(slug, Language.EN))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Blog not found");
    }

    @Test
    void createBlog_savesAndReturnsMappedDto() throws IOException {
        MultipartFile file = createMockImageFile();

        when(blogTransformer.fromCreateDto(blogDto)).thenReturn(blog);
        when(imageService.processAndUploadImage(file)).thenReturn(getImageUrls());

        mockSaveBlog(blog);
        mockFindCategories(List.of(category));
        mockTransformToDto(blog, blogResponseDto);

        BlogResponseDto result = blogService.createBlog(blogDto, file);

        assertBlogDto(result);

        assertEquals("small.webp", blog.getImageUrlSmall());
        assertEquals("medium.webp", blog.getImageUrlMedium());
        assertEquals("large.webp", blog.getImageUrlLarge());
    }

    private static ImageUrls getImageUrls() {
        return ImageUrls.builder()
                .small("small.webp")
                .medium("medium.webp")
                .large("large.webp")
                .build();
    }


    @Test
    void createBlog_withoutImage_setsNoImageUrls() throws IOException {
        when(blogTransformer.fromCreateDto(blogDto)).thenReturn(blog);
        mockSaveBlog(blog);
        mockTransformToDto(blog, blogResponseDto);
        mockFindCategories(List.of(category));

        BlogResponseDto result = blogService.createBlog(blogDto, null);

        verify(blogTransformer).toDto(eq(blog), any(Language.class));
        verify(imageService, never()).processAndUploadImage(any());

        assertBlogDto(result);

        assertNull(blog.getImageUrlSmall());
        assertNull(blog.getImageUrlMedium());
        assertNull(blog.getImageUrlLarge());
    }


    @Test
    void createBlog_withEmptyFile_doesNotUpload() throws IOException {
        when(blogTransformer.fromCreateDto(blogDto)).thenReturn(blog);
        mockSaveBlog(blog);
        mockTransformToDto(blog, blogResponseDto);
        mockFindCategories(List.of(category));

        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);

        BlogResponseDto result = blogService.createBlog(blogDto, emptyFile);

        assertBlogDto(result);

        verify(imageService, never()).processAndUploadImage(any());

        assertNull(blog.getImageUrlSmall());
        assertNull(blog.getImageUrlMedium());
        assertNull(blog.getImageUrlLarge());
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
        BlogDto updateDto = createUpdateBlogDto();
        MultipartFile file = createMockImageFile();

        ImageUrls imageUrls = ImageUrls.builder()
                .small("small.webp")
                .medium("medium.webp")
                .large("large.webp")
                .build();

        mockFindBlogBySlug(Optional.of(blog));

        when(categoryRepository.findBySlugIn(updateDto.getCategorySlugs()))
                .thenReturn(List.of(category));
        when(imageService.processAndUploadImage(file))
                .thenReturn(imageUrls);

        mockSaveBlog(blog);
        mockTransformToDto(blog, blogResponseDto);

        BlogResponseDto result = blogService.updateBlog(slug, updateDto, file);

        assertBlogDto(result);

        assertEquals("small.webp", blog.getImageUrlSmall());
        assertEquals("medium.webp", blog.getImageUrlMedium());
        assertEquals("large.webp", blog.getImageUrlLarge());

        verify(blogRepository).findBySlug(slug);
        verify(blogRepository).save(blog);
        verify(imageService).processAndUploadImage(file);
        verify(blogTransformer).updateEntity(blog, updateDto);
    }


    @Test
    void deleteBlog_deletesBySlug() {
        blog.setId(UUID.randomUUID());
        blog.setSlug(slug);

        when(blogRepository.findBySlug(slug)).thenReturn(Optional.of(blog));
        blogService.deleteBlog(slug);

        verify(blogRepository).delete(blog);
    }

    @Test
    void testGetRandomBlogPreviews() {
        Language lang = Language.EN;

        BlogPreviewProjection projection1 = mock(BlogPreviewProjection.class);
        BlogPreviewProjection projection2 = mock(BlogPreviewProjection.class);

        BlogPreviewDto dto1 = BlogPreviewDto.builder().slug("slug-1").title("Title 1").build();
        BlogPreviewDto dto2 = BlogPreviewDto.builder().slug("slug-2").title("Title 2").build();

        when(blogRepository.findRandomBlogsByLanguage(lang.name()))
                .thenReturn(List.of(projection1, projection2));

        when(blogTransformer.mapToBlogPreviewDto(projection1)).thenReturn(dto1);
        when(blogTransformer.mapToBlogPreviewDto(projection2)).thenReturn(dto2);

        List<BlogPreviewDto> result = blogService.getRandomBlogPreviews(lang);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSlug()).isEqualTo("slug-1");
        assertThat(result.get(1).getSlug()).isEqualTo("slug-2");

        verify(blogRepository).findRandomBlogsByLanguage(lang.name());
        verify(blogTransformer).mapToBlogPreviewDto(projection1);
        verify(blogTransformer).mapToBlogPreviewDto(projection2);
    }

    private void setupCategory() {
        category = Category.builder()
                .id(UUID.randomUUID())
                .slug(categorySlug)
                .build();

        categoryDto = CategoryDto.builder()
                .name("Tech")
                .slug(categorySlug)
                .build();
    }

    private void setupDtos() {
        categoryDto = createCategoryDto();
        blogResponseDto = createBlogWithTranslationDto(categoryDto, "Test Blog", "Content goes here");
    }
    private CategoryDto createCategoryDto() {
        return CategoryDto.builder()
                .name("Tech")
                .slug(categorySlug)
                .build();
    }

    private BlogResponseDto createBlogWithTranslationDto(CategoryDto categoryDto, String title, String content) {
        return BlogResponseDto.builder()
                .title(title)
                .author("Author")
                .content(content)
                .readingTime(10)
                .categories(List.of(categoryDto))
                .build();
    }

    private Blog createTestBlog() {
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

    private BlogDto createSampleCreateBlogDto() {
        BlogDto dto = BlogDto.builder()
                .author("Jane")
                .categorySlugs(List.of(categorySlug)).build();

        TranslationDto translationDto = createSampleCreateBlogTranslationDto();
        dto.setTranslation(translationDto);

        return dto;
    }

    private TranslationDto createSampleCreateBlogTranslationDto() {
        return TranslationDto.builder()
                .language(Language.EN)
                .title("Healthy Living")
                .content("Tips for a healthy lifestyle.")
                .build();
    }



    private BlogDto createUpdateBlogDto() {
        return BlogDto.builder()
                .author("Updated Author")
                .categorySlugs(List.of(categorySlug))
                .build();
    }

    private void assertBlogDto(BlogResponseDto dto) {
        assertThat(dto).isNotNull();
        assertThat(dto.getTitle()).isEqualTo("Test Blog");
        assertThat(dto.getCategories()).hasSize(1);
        assertThat(dto.getCategories().get(0).getName()).isEqualTo("Tech");
    }

    private void mockFindBlogBySlug(Optional<Blog> blogOpt) {
        when(blogRepository.findBySlug(slug)).thenReturn(blogOpt);
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