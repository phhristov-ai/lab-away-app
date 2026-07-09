package com.labaway.backend.controller.communication;

import com.labaway.backend.dto.subscription.SubscriptionRequest;
import com.labaway.backend.service.communication.NewsletterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
public class NewsletterController {
    private final NewsletterService newsletterService;

    @PostMapping
    public ResponseEntity<String> subscribe(@RequestBody SubscriptionRequest request) {
        try {
            String message = newsletterService.subscribe(request.email());
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<String> unsubscribe(@RequestBody SubscriptionRequest request) {
        boolean success = newsletterService.unsubscribe(request.email());
        if (success) {
            return ResponseEntity.ok("You have been unsubscribed.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        }
    }

}
