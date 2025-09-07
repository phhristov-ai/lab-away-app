package com.labaway.integration.steps.blog;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllBlogPreviewsSteps {

    private List<Map<String, Object>> blogPreviewList;
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${api.base.url}")
    private String baseUrl;

    @When("the user requests all blog previews in language {string}")
    public void theUserRequestsAllBlogPreviewsInLanguage(String lang) {
        String url = baseUrl + "/blogs?lang=" + lang;
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        blogPreviewList = response.getBody();
    }

    @Then("the response should contain a blog with title {string}")
    public void theResponseShouldContainABlogWithTitle(String expectedTitle) {
        boolean found = blogPreviewList.stream()
                .map(blog -> (String) blog.get("title"))
                .anyMatch(title -> title.equals(expectedTitle));

        assertTrue(found, "Expected to find a blog with title: " + expectedTitle);
    }

    @Then("the blog should have category with name {string}")
    public void theBlogShouldHaveCategoryWithName(String expectedCategory) {
        boolean found = blogPreviewList.stream().anyMatch(blog -> {
            List<Map<String, String>> categories = (List<Map<String, String>>) blog.get("categories");
            return categories.stream()
                    .anyMatch(cat -> cat.get("name").equals(expectedCategory));
        });

        assertTrue(found, "Expected to find at least one blog with category: " + expectedCategory);
    }
}
