package com.labaway.integration.steps.blog;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ViewBlogPostSteps {

    private Map<String, Object> responseBody;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.base.url}")
    private String baseUrl;

    @When("the user views the blog post with slug {string} in language {string}")
    public void theUserViewsBlogPostWithSlugInLanguage(String slug, String lang) {
        String url = baseUrl + "/blogs/" + slug + "?lang=" + lang;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Expected 200 OK when fetching blog post, but got: " + response.getStatusCode());

        responseBody = response.getBody();
        assertNotNull(responseBody, "Response body should not be null");
    }

    @Then("the blog post title should be {string}")
    public void theBlogPostTitleShouldBe(String expectedTitle) {
        String actualTitle = (String) responseBody.get("title");
        assertEquals(expectedTitle.trim(), actualTitle.trim(), "Blog title mismatch");
    }

    @Then("the blog post content should contain {string}")
    public void theBlogPostContentShouldContain(String expectedSnippet) {
        String actualContent = (String) responseBody.get("content");
        assertNotNull(actualContent, "Blog content is missing");
        assertTrue(actualContent.contains(expectedSnippet),
                "Expected snippet not found in blog content.");
    }

    @Then("the blog post should include category with name {string}")
    public void blogPostShouldIncludeCategoryWithName(String expectedName) {
        List<Map<String, String>> categories = (List<Map<String, String>>) responseBody.get("categories");
        assertNotNull(categories, "Categories are missing");

        boolean found = categories.stream()
                .anyMatch(cat -> expectedName.equalsIgnoreCase(cat.get("name")));

        assertTrue(found, "Expected category name not found: " + expectedName);
    }
}
