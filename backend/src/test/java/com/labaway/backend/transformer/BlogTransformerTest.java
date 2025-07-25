package com.labaway.backend.transformer;

import com.labaway.backend.dto.blog.*;
import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.entity.blog.Blog;
import com.labaway.backend.entity.blog.BlogTranslation;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.category.CategoryTranslation;
import com.labaway.backend.enums.Language;
import com.labaway.backend.util.JsonParsingUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class BlogTransformerTest {

    private BlogTransformer blogTransformer;
    @Mock
    private CategoryTransformer categoryTransformer;
    @Mock
    private JsonParsingUtils jsonParsingUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.blogTransformer = new BlogTransformer(categoryTransformer, jsonParsingUtils);
    }

    @Test
    void mapToBlogPreviewDtos_mapsCorrectly() {
        List<BlogPreviewProjection> projections = List.of(
                createMockProjection("slug-1", "Author 1", "Title 1", "Excerpt 1", 3, "[{\"name\":\"Tech\",\"slug\":\"tech\"}]"),
                createMockProjection("slug-2", "Author 2", "Title 2", "Excerpt 2", 5, "[{\"name\":\"Life\",\"slug\":\"life\"}]")
        );

        when(jsonParsingUtils.parseCategoryList("[{\"name\":\"Tech\",\"slug\":\"tech\"}]"))
                .thenReturn(List.of(createCategoryDto("Tech", "tech")));
        when(jsonParsingUtils.parseCategoryList("[{\"name\":\"Life\",\"slug\":\"life\"}]"))
                .thenReturn(List.of(createCategoryDto("Life", "life")));

        List<BlogPreviewDto> dtos = blogTransformer.mapToBlogPreviewDtos(projections);

        assertBlogPreviewDto(dtos.get(0), "slug-1", "Author 1", "Title 1", "Excerpt 1", 3, "Tech", "tech");
        assertBlogPreviewDto(dtos.get(1), "slug-2", "Author 2", "Title 2", "Excerpt 2", 5, "Life", "life");

        verify(jsonParsingUtils).parseCategoryList("[{\"name\":\"Tech\",\"slug\":\"tech\"}]");
        verify(jsonParsingUtils).parseCategoryList("[{\"name\":\"Life\",\"slug\":\"life\"}]");
    }

    private BlogPreviewProjection createMockProjection(String slug, String author, String title, String excerpt, int readingTime, String categoryJson) {
        BlogPreviewProjection mockProjection = mock(BlogPreviewProjection.class);
        when(mockProjection.getSlug()).thenReturn(slug);
        when(mockProjection.getAuthor()).thenReturn(author);
        when(mockProjection.getImageUrl()).thenReturn("http://image.jpg");
        when(mockProjection.getTitle()).thenReturn(title);
        when(mockProjection.getExcerpt()).thenReturn(excerpt);
        when(mockProjection.getReadingTime()).thenReturn(readingTime);
        when(mockProjection.getCreatedAt()).thenReturn(Instant.now().minus(Duration.ofDays(1L)));
        when(mockProjection.getUpdatedAt()).thenReturn(Instant.now());
        when(mockProjection.getCategories()).thenReturn(categoryJson);
        return mockProjection;
    }

    private CategoryDto createCategoryDto(String name, String slug) {
        return CategoryDto.builder().name(name).slug(slug).build();
    }

    private void assertBlogPreviewDto(BlogPreviewDto dto, String expectedSlug, String expectedAuthor, String expectedTitle, String expectedExcerpt, int expectedReadingTime, String expectedCategoryName, String expectedCategorySlug) {
        assertThat(dto).isNotNull();
        assertThat(dto.getSlug()).isEqualTo(expectedSlug);
        assertThat(dto.getAuthor()).isEqualTo(expectedAuthor);
        assertThat(dto.getTitle()).isEqualTo(expectedTitle);
        assertThat(dto.getExcerpt()).isEqualTo(expectedExcerpt);
        assertThat(dto.getReadingTime()).isEqualTo(expectedReadingTime);
        assertThat(dto.getCategories()).extracting("slug").containsExactly(expectedCategorySlug);
        assertThat(dto.getCategories()).extracting("name").containsExactly(expectedCategoryName);
    }

    @Test
    void updateEntity_shouldUpdateExistingTranslation() {
        String originalTitle = "Original Title";
        String originalContent = "Original content";
        Blog blog = createBlogWithTranslationsAndCategories(
                "slug1", "author1", "imageUrl1",
                Set.of(), Language.EN, originalTitle, originalContent);

        BlogDto dto = createSampleCreateBlogDto(
                "new author",
                "Updated Title",
                "Updated content",
                List.of("cat1", "cat2"));

        blogTransformer.updateEntity(blog, dto);

        assertEquals("new author", blog.getAuthor());

        assertEquals(1, blog.getTranslations().size());

        BlogTranslation translation = blog.getTranslations().iterator().next();
        assertEquals(Language.EN, translation.getLanguage());
        assertEquals("Updated Title", translation.getTitle());
        assertEquals("Updated content", translation.getContent());
        assertTrue(translation.getReadingTime() > 0);
    }

    @Test
    void updateEntity_shouldAddNewTranslationWhenNoneExists() {
        // Arrange
        Blog blog = createBlogWithTranslationsAndCategories(
                "slug1", "author1", "imageUrl1",
                Set.of(), Language.DE, "Titre", "Contenu");

        BlogDto dto = createSampleCreateBlogDto(
                "new author",
                "English Title",
                "English content",
                List.of("cat1", "cat2"));

        dto.getTranslation().setLanguage(Language.EN);

        blogTransformer.updateEntity(blog, dto);

        assertEquals("new author", blog.getAuthor());

        assertEquals(2, blog.getTranslations().size());

        Optional<BlogTranslation> englishTranslationOpt = blog.getTranslations().stream()
                .filter(t -> t.getLanguage() == Language.EN)
                .findFirst();

        assertTrue(englishTranslationOpt.isPresent());
        BlogTranslation enTranslation = englishTranslationOpt.get();
        assertEquals("English Title", enTranslation.getTitle());
        assertEquals("English content", enTranslation.getContent());
        assertTrue(enTranslation.getReadingTime() > 0);
    }

    @Test
    void shouldMapBlogToBlogDto() {
        Category category = getCategory();
        Blog blog = getBlog(category);
        when(categoryTransformer.toDto(category, Language.EN))
                .thenReturn(CategoryDto.builder().slug("tech").name("Tech").build());
        BlogResponseDto dto = blogTransformer.toDto(blog, Language.EN);

        assertBlogDto(dto, "Spring Boot Guide", 1, "Tech");
    }

    @Test
    void shouldMapCreateBlogDtoToBlog() {
        BlogDto dto = createSampleCreateBlogDto(
                "Jane",
                "Healthy Living",
                "Tips for a healthy lifestyle.",
                List.of("tech")
        );

        Blog blog = blogTransformer.fromCreateDto(dto);

        assertBlogEntity(blog, "Healthy Living", "Tips for a healthy lifestyle.", "Jane");
    }

    private static Category createCategoryWithTranslation(String slug, Language lang, String name) {
        Category category = Category.builder()
                .id(UUID.randomUUID())
                .slug(slug)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        CategoryTranslation translation = CategoryTranslation.builder()
                .language(lang)
                .name(name)
                .category(category)
                .build();

        category.setTranslations(Set.of(translation));
        return category;
    }

    private static Blog createBlogWithTranslationsAndCategories(
            String slug, String author, String imageUrl,
            Set<Category> categories,
            Language lang, String title, String content) {

        Blog blog = Blog.builder()
                .id(UUID.randomUUID())
                .slug(slug)
                .author(author)
                .imageUrl(imageUrl)
                .categories(categories)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        BlogTranslation translation = BlogTranslation.builder()
                .language(lang)
                .title(title)
                .content(content)
                .readingTime(10)
                .blog(blog)
                .build();

        blog.setTranslations(new HashSet<>(Set.of(translation)));
        return blog;
    }

    private static Category getCategory() {
        return createCategoryWithTranslation("tech", Language.EN, "Tech");
    }

    private static Blog getBlog(Category category) {
        return createBlogWithTranslationsAndCategories(
                "spring-boot-guide", "John Doe", "https://example.com/spring-boot.jpg",
                Set.of(category),
                Language.EN, "Spring Boot Guide", "Content of the guide"
        );
    }

    private static void assertBlogDto(BlogResponseDto dto, String expectedTitle, int expectedCategoryCount, String expectedCategoryName) {
        assertThat(dto).isNotNull();
        assertThat(dto.getTitle()).isEqualTo(expectedTitle);
        assertThat(dto.getCategories()).hasSize(expectedCategoryCount);
        assertThat(dto.getCategories().get(0).getName()).isEqualTo(expectedCategoryName);
    }

    private static void assertBlogEntity(Blog blog, String expectedTitle, String expectedContent, String expectedAuthor) {
        assertThat(blog).isNotNull();
        assertThat(blog.getAuthor()).isEqualTo(expectedAuthor);
        assertThat(blog.getTranslations())
                .extracting(BlogTranslation::getTitle, BlogTranslation::getContent, t -> t.getLanguage().toString())
                .containsExactly(tuple(expectedTitle, expectedContent, Language.EN.toString()));

    }

    private static BlogDto createSampleCreateBlogDto(
            String author,
            String translationTitle,
            String translationContent,
            List<String> categorySlugs) {

        BlogDto dto = BlogDto.builder().build();
        TranslationDto blogTranslationDto = TranslationDto.builder()
                .language(Language.EN)
                .title(translationTitle)
                .content(translationContent)
                .build();
        dto.setTranslation(blogTranslationDto);
        dto.setAuthor(author);
        dto.setCategorySlugs(categorySlugs);
        return dto;
    }

}
