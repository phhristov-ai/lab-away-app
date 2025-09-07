package com.labaway.integration.steps.blog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.labaway.integration.TestContext;
import com.labaway.integration.dto.blog.BlogDto;
import com.labaway.integration.dto.blog.BlogResponseDto;
import com.labaway.integration.dto.blog.TranslationDto;
import com.labaway.integration.enums.Language;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateBlogSteps {

    private final RestTemplate restTemplate = new RestTemplate();
    private final TestContext testContext;

    private BlogResponseDto blogResponseDto;
    @Value("${api.base.url}")
    private String baseUrl;

    public CreateBlogSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @When("the admin submits a new blog post with:")
    public void submitNewBlogPost(Map<String, String> data) throws Exception {
        String languageStr = data.get("language");
        String title = data.get("title");
        String content = data.get("content");
        String filename = data.get("image");

        String url = baseUrl + "/blogs";

        Language language = Language.valueOf(languageStr);

        TranslationDto translation = TranslationDto.builder()
                .language(language)
                .title(title)
                .content(content)
                .build();

        BlogDto blogDto = BlogDto.builder()
                .author("testAuthor")
                .categorySlugs(List.of("std-tests"))
                .translation(translation)
                .build();

        String blogJson = new ObjectMapper().writeValueAsString(blogDto);

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("blog", new HttpEntity<>(blogJson, createJsonHeaders()));

        Path filePath = Path.of("src/test/resources/images/", filename);
        FileSystemResource fileResource = new FileSystemResource(filePath.toFile());
        parts.add("file", fileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(testContext.getJwtToken());

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(parts, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        System.out.println("Raw JSON response: " + response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }


    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Then("the blog should be created successfully with:")
    public void verifyBlogCreatedSuccessfullyWith(Map<String, String> expectedData) {
        String expectedTitle = expectedData.get("title");
        String expectedContent = expectedData.get("content");
        String expectedLanguage = expectedData.get("language");

        // Retrieve actual blog info from response or database
        // For example, assume you have a stored BlogResponseDto:

    }
}
