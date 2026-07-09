package com.labaway.backend.unit.service.communication;

import com.labaway.backend.entity.communication.NewsletterSubscriber;
import com.labaway.backend.entity.repository.communication.NewsletterSubscriberRepository;
import com.labaway.backend.service.communication.NewsletterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsletterServiceTest {
    private NewsletterSubscriberRepository repository;
    private NewsletterService service;

    @BeforeEach
    void setUp() {
        repository = mock(NewsletterSubscriberRepository.class);
        service = new NewsletterService(repository);
    }

    @Test
    void subscribe_validEmail_notSubscribedYet_shouldSaveAndReturnSuccess() {
        String email = "test@example.com";
        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        String result = service.subscribe(email);

        assertEquals("Subscribed successfully", result);
        verify(repository).save(any(NewsletterSubscriber.class));
    }

    @Test
    void subscribe_validEmail_alreadySubscribed_shouldReturnMessage() {
        String email = "test@example.com";
        when(repository.findByEmail(email)).thenReturn(Optional.of(new NewsletterSubscriber()));

        String result = service.subscribe(email);

        assertEquals("Already subscribed", result);
        verify(repository, never()).save(any());
    }

    @Test
    void subscribe_invalidEmail_shouldThrowException() {
        String email = "invalid-email";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.subscribe(email);
        });
        assertEquals("Invalid email format", exception.getMessage());
        verify(repository, never()).findByEmail(any());
        verify(repository, never()).save(any());
    }

    @Test
    void unsubscribe_validEmail_existingSubscriber_shouldDeleteAndReturnTrue() {
        String email = "test@example.com";
        NewsletterSubscriber subscriber = NewsletterSubscriber.builder().email(email).build();

        when(repository.findByEmail(email)).thenReturn(Optional.of(subscriber));

        boolean result = service.unsubscribe(email);

        assertTrue(result);
        verify(repository).delete(subscriber);
    }

    @Test
    void unsubscribe_validEmail_notSubscribed_shouldReturnFalse() {
        String email = "notfound@example.com";
        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        boolean result = service.unsubscribe(email);

        assertFalse(result);
        verify(repository, never()).delete(any());
    }

    @Test
    void unsubscribe_invalidEmail_shouldThrowException() {
        String email = "bad-email";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.unsubscribe(email);
        });

        assertEquals("Invalid email format", exception.getMessage());
        verify(repository, never()).findByEmail(any());
        verify(repository, never()).delete(any());
    }
}