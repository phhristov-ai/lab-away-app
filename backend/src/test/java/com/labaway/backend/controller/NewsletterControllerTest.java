package com.labaway.backend.controller;

import com.labaway.backend.dto.subscription.SubscriptionRequest;
import com.labaway.backend.service.NewsletterService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NewsletterControllerTest {

    private final NewsletterService newsletterService = mock(NewsletterService.class);
    private final NewsletterController controller = new NewsletterController(newsletterService);

    @Test
    void subscribe_validEmail_shouldReturnSuccessMessage() {
        SubscriptionRequest request = SubscriptionRequest.builder().email("test@example.com").build();

        when(newsletterService.subscribe("test@example.com"))
                .thenReturn("Subscribed successfully");

        ResponseEntity<String> response = controller.subscribe(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals("Subscribed successfully", response.getBody());
    }

    @Test
    void subscribe_invalidEmail_shouldReturnBadRequest() {
        SubscriptionRequest request = SubscriptionRequest.builder().email("invalid-email").build();

        when(newsletterService.subscribe("invalid-email"))
                .thenThrow(new IllegalArgumentException("Invalid email format"));

        ResponseEntity<String> response = controller.subscribe(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertEquals("Invalid email format", response.getBody());
    }

    @Test
    void unsubscribe_existingEmail_shouldReturnSuccessMessage() {
        SubscriptionRequest request = SubscriptionRequest.builder().email("test@example.com").build();

        when(newsletterService.unsubscribe("test@example.com")).thenReturn(true);

        ResponseEntity<String> response = controller.unsubscribe(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals("You have been unsubscribed.", response.getBody());
    }

    @Test
    void unsubscribe_nonExistentEmail_shouldReturnNotFound() {
        SubscriptionRequest request = SubscriptionRequest.builder().email("missing@example.com").build();

        when(newsletterService.unsubscribe("missing@example.com")).thenReturn(false);

        ResponseEntity<String> response = controller.unsubscribe(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertEquals("Email not found.", response.getBody());
    }
}