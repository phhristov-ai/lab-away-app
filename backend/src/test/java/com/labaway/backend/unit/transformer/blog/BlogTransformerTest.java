package com.labaway.backend.unit.transformer.blog;

import com.labaway.backend.dto.blog.*;
import com.labaway.backend.dto.category.CategoryDto;
import com.labaway.backend.entity.blog.Blog;
import com.labaway.backend.entity.blog.BlogTranslation;
import com.labaway.backend.entity.category.Category;
import com.labaway.backend.entity.category.CategoryTranslation;
import com.labaway.backend.enums.Language;
import com.labaway.backend.transformer.blog.BlogTransformer;
import com.labaway.backend.transformer.category.CategoryTransformer;
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

    private BlogPreviewProjection createMockProjection(
            String slug,
            String author,
            String title,
            String excerpt,
            int readingTime,
            String categoryJson
    ) {
        BlogPreviewProjection mockProjection = mock(BlogPreviewProjection.class);

        when(mockProjection.getSlug()).thenReturn(slug);
        when(mockProjection.getAuthor()).thenReturn(author);

        when(mockProjection.getImageUrlSmall()).thenReturn("http://image-small.jpg");
        when(mockProjection.getImageUrlMedium()).thenReturn("http://image-medium.jpg");
        when(mockProjection.getImageUrlLarge()).thenReturn("http://image-large.jpg");

        when(mockProjection.getTitle()).thenReturn(title);
        when(mockProjection.getExcerpt()).thenReturn(excerpt);
        when(mockProjection.getReadingTime()).thenReturn(readingTime);
        when(mockProjection.getCreatedAt()).thenReturn(Instant.now().minus(Duration.ofDays(1)));
        when(mockProjection.getUpdatedAt()).thenReturn(Instant.now());
        when(mockProjection.getCategories()).thenReturn(categoryJson);

        return mockProjection;
    }

    private CategoryDto createCategoryDto(String name, String slug) {
        return new CategoryDto(name, slug);
    }

    private void assertBlogPreviewDto(BlogPreviewDto dto, String expectedSlug, String expectedAuthor, String expectedTitle, String expectedExcerpt, int expectedReadingTime, String expectedCategoryName, String expectedCategorySlug) {
        assertThat(dto).isNotNull();
        assertThat(dto.slug()).isEqualTo(expectedSlug);
        assertThat(dto.author()).isEqualTo(expectedAuthor);
        assertThat(dto.title()).isEqualTo(expectedTitle);
        assertThat(dto.excerpt()).isEqualTo(expectedExcerpt);
        assertThat(dto.readingTime()).isEqualTo(expectedReadingTime);
        assertThat(dto.categories()).extracting("slug").containsExactly(expectedCategorySlug);
        assertThat(dto.categories()).extracting("name").containsExactly(expectedCategoryName);
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
                List.of("cat1", "cat2"),
                Language.EN
        );

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
        Blog blog = createBlogWithTranslationsAndCategories(
                "slug1", "author1", "imageUrl1",
                Set.of(), Language.DE, "Titre", "Contenu");

        BlogDto dto = createSampleCreateBlogDto(
                "new author",
                "English Title",
                "English content",
                List.of("cat1", "cat2"),
                Language.EN
        );

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

    private BlogDto createSampleCreateBlogDto(
            String author,
            String title,
            String content,
            List<String> categorySlugs,
            Language language
    ) {
        return new BlogDto(
                author,
                categorySlugs,
                new TranslationDto(
                        language,
                        title,
                        content
                )
        );
    }

    @Test
    void shouldMapBlogToBlogDto() {
        Category category = getCategory();

        Blog blog = getBlog(category);
        when(categoryTransformer.toDto(category, Language.EN))
                .thenReturn(new CategoryDto("Tech", "tech"));
        BlogResponseDto dto = blogTransformer.toDto(blog, Language.EN);

        assertBlogDto(dto, "Spring Boot Guide", 1, "Tech");
    }

    @Test
    void shouldMapCreateBlogDtoToBlog() {
        BlogDto dto = createSampleCreateBlogDto(
                "Jane",
                "Healthy Living",
                "Tips for a healthy lifestyle.",
                List.of("tech"),
                Language.EN
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
                .imageUrlSmall(randomImageUrl("480"))
                .imageUrlMedium(randomImageUrl("768"))
                .imageUrlLarge(randomImageUrl("1200"))
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

    private static String randomImageUrl(String size) {
        return "https://lab-away-images.s3.eu-north-1.amazonaws.com/blog/"
                + UUID.randomUUID()
                + "_blog_" + size + ".webp";
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
        assertThat(dto.title()).isEqualTo(expectedTitle);
        assertThat(dto.categories()).hasSize(expectedCategoryCount);
        assertThat(dto.categories().get(0).name()).isEqualTo(expectedCategoryName);
    }

    private static void assertBlogEntity(Blog blog, String expectedTitle, String expectedContent, String expectedAuthor) {
        assertThat(blog).isNotNull();
        assertThat(blog.getAuthor()).isEqualTo(expectedAuthor);
        assertThat(blog.getTranslations())
                .extracting(BlogTranslation::getTitle, BlogTranslation::getContent, t -> t.getLanguage().toString())
                .containsExactly(tuple(expectedTitle, expectedContent, Language.EN.toString()));

    }
}
