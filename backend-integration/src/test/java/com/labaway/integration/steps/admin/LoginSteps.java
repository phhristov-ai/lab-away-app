package com.labaway.integration.steps.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labaway.integration.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LoginSteps {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private ResponseEntity<Map> loginResponse;
    private String jwtToken;
    @Value("${api.base.url}")
    private String baseUrl;
    @Value("${admin.test.username}")
    private String testUsername;
    @Value("${admin.test.password}")
    private String testPassword;

    private final TestContext testContext;

    public LoginSteps(TestContext testContext) {
        this.testContext = testContext;
    }
    @When("the admin logs in with username {string} and password {string}")
    public void adminLogsIn(String username, String password) throws JsonProcessingException {
        String resolvedUsername = resolveCredential(username);
        String resolvedPassword = resolveCredential(password);

        String url = baseUrl + "/admin-login";
        HttpEntity<Map<String, String>> request = buildLoginRequest(resolvedUsername, resolvedPassword);
        try {
            loginResponse = restTemplate.postForEntity(url, request, Map.class);
            testContext.setJwtToken((String) loginResponse.getBody().get("token"));
        } catch (HttpClientErrorException ex) {
            // Still handle expected 401/400 but don't suppress parsing errors
            String responseBody = ex.getResponseBodyAsString();
            Map<String, Object> errorBody = parseErrorBodyOrThrow(responseBody);
            loginResponse = ResponseEntity.status(ex.getStatusCode()).body(errorBody);
        }
    }

    private String resolveCredential(String value) {
        return switch (value) {
            case "<validUsername>" -> testUsername;
            case "<validPassword>" -> testPassword;
            default -> value;
        };
    }


    private HttpEntity<Map<String, String>> buildLoginRequest(String username, String password) {
        Map<String, String> body = Map.of(
                "username", username,
                "password", password
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(body, headers);
    }

    private Map<String, Object> parseErrorBodyOrThrow(String rawBody) throws JsonProcessingException {
        if (rawBody == null || rawBody.trim().isEmpty()) return null;
        return objectMapper.readValue(rawBody, Map.class);
    }

    @Then("the login should {loginOutcome}")
    public void theLoginShouldSucceedOrFail(boolean shouldSucceed) {
        if (shouldSucceed) {
            assertSuccessfulLogin();
        } else {
            assertFailedLogin();
        }
    }

    private void assertSuccessfulLogin() {
        HttpStatusCode status = loginResponse.getStatusCode();
        Map<String, Object> body = loginResponse.getBody();

        assertEquals(HttpStatus.OK, status, "Expected HTTP 200 OK for successful login");
        assertNotNull(body, "Response body should not be null");
        assertTrue(body.containsKey("token"), "Expected token in response");
    }

    private void assertFailedLogin() {
        HttpStatusCode status = loginResponse.getStatusCode();
        Map<String, Object> body = loginResponse.getBody();

        assertEquals(HttpStatus.UNAUTHORIZED, status, "Expected HTTP 401 Unauthorized for failed login");
        assertTrue(body == null || !body.containsKey("token"),
                "Token should not be present for failed login");
    }

}
