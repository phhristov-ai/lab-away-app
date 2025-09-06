package com.labaway.integration.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlogSteps {

    private Map<String, Object> responseBody;

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${api.base.url}")
    private String baseUrl;

    @When("the user views the blog post with slug {string} in language {string}")
    public void the_user_views_blog_post_with_slug_in_language(String slug, String lang) {
        String url = baseUrl + "/blogs/" + slug + "?lang=" + lang;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to fetch blog post");
        }

        responseBody = response.getBody();

        System.out.println(response);
    }

    @Then("the blog post title should be {string}")
    public void the_blog_post_title_should_be(String expectedTitle) {
        assertEquals(expectedTitle, responseBody.get("title"));
    }

    @Then("the blog post content should be {string}")
    public void the_blog_post_content_should_be(String expectedContent) {
    }
}