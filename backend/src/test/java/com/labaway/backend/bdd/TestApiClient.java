package com.labaway.backend.bdd;

import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

@Component
@Profile("test")
public class TestApiClient {

    private final Environment environment;

    public TestApiClient(Environment environment) {
        this.environment = environment;
    }

    private RestClient client() {

        String port =
                environment.getProperty("local.server.port");

        if (port == null) {
            throw new IllegalStateException(
                    "local.server.port not available");
        }

        return RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    public <T, R> ResponseEntity<R> post(
            String path,
            T body,
            HttpHeaders headers,
            Class<R> responseType) {

        return client()
                .post()
                .uri(path)
                .headers(h -> h.addAll(headers))
                .body(body)
                .retrieve()
                .toEntity(responseType);
    }

    public ResponseEntity<String> postExpectingErrors(
            String path,
            Object body,
            HttpHeaders headers) {

        try {
            return client()
                    .post()
                    .uri(path)
                    .headers(h -> h.addAll(headers))
                    .body(body)
                    .retrieve()
                    .toEntity(String.class);

        } catch (HttpStatusCodeException ex) {
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(ex.getResponseBodyAsString());
        }
    }

    public <R> ResponseEntity<R> get(
            String path,
            HttpHeaders headers,
            Class<R> responseType) {

        return client()
                .get()
                .uri(path)
                .headers(h -> h.addAll(headers))
                .retrieve()
                .toEntity(responseType);
    }

    public ResponseEntity<String> getExpectingErrors(
            String path,
            HttpHeaders headers) {

        try {
            return client()
                    .get()
                    .uri(path)
                    .headers(h -> h.addAll(headers))
                    .retrieve()
                    .toEntity(String.class);

        } catch (HttpStatusCodeException ex) {

            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(ex.getResponseBodyAsString());
        }
    }
}