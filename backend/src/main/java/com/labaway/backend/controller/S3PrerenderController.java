package com.labaway.backend.controller;

import com.labaway.backend.service.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class S3PrerenderController {

    private final S3Service s3Service;

    public S3PrerenderController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    private boolean isCrawler(String userAgent) {
        if (userAgent == null) return false;
        String ua = userAgent.toLowerCase();
        return ua.contains("googlebot") || ua.contains("bingbot") || ua.contains("yahoo") || ua.contains("baiduspider");
    }

    @GetMapping({"/en/**", "/de/**"})
    public ResponseEntity<String> handleRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String path = request.getRequestURI();

        if (isCrawler(userAgent)) {
            try {
                String html = s3Service.getHtml(path);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
                        .body(html);
            } catch (Exception e) {
                return ResponseEntity.status(404).body("Page not found");
            }
        }

        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, "/index.html")
                .build();
    }

}
