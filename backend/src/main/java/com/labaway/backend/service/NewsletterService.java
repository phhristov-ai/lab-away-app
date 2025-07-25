package com.labaway.backend.service;

import com.labaway.backend.entity.NewsletterSubscriber;
import com.labaway.backend.entity.repository.NewsletterSubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsletterService {
    private final NewsletterSubscriberRepository repository;
    public String subscribe(String email) {
        return findValidSubscriberByEmail(email)
                .map(existing -> "Already subscribed")
                .orElseGet(() -> {
                    repository.save(NewsletterSubscriber.builder()
                            .email(email)
                            .build());
                    return "Subscribed successfully";
                });
    }

    public boolean unsubscribe(String email) {
        return findValidSubscriberByEmail(email)
                .map(existing -> {
                    repository.delete(existing);
                    return true;
                })
                .orElse(false);
    }

    private Optional<NewsletterSubscriber> findValidSubscriberByEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return repository.findByEmail(email);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
}