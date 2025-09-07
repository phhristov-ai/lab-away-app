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

public class RandomBlogPreviewsSteps {

    private List<Map<String, Object>> randomPreviews;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.base.url}")
    private String baseUrl;

    @When("the user requests random blog previews in language {string}")
    public void theUserRequestsRandomBlogPreviewsInLanguage(String lang) {
        String url = baseUrl + "/blogs/random?lang=" + lang;
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        randomPreviews = response.getBody();
    }

    @Then("the response should contain at least one blog preview")
    public void theResponseShouldContainAtLeastOneBlogPreview() {
        assertNotNull(randomPreviews);
        assertFalse(randomPreviews.isEmpty());
    }

    @Then("each blog should have a non-empty title")
    public void eachBlogShouldHaveANonEmptyTitle() {
        for (Map<String, Object> blog : randomPreviews) {
            String title = (String) blog.get("title");
            assertNotNull(title);
            assertFalse(title.trim().isEmpty());
        }
    }

    @Then("each blog should include at least one category")
    public void eachBlogShouldIncludeAtLeastOneCategory() {
        for (Map<String, Object> blog : randomPreviews) {
            List<Map<String, Object>> categories = (List<Map<String, Object>>) blog.get("categories");
            assertNotNull(categories);
            assertFalse(categories.isEmpty());
        }
    }
}
