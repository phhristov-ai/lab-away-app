package com.labaway.backend.controller;

import com.labaway.backend.service.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class S3PrerenderControllerTest {

    private S3Service s3Service;
    private S3PrerenderController controller;

    @BeforeEach
    void setUp() {
        s3Service = mock(S3Service.class);
        controller = new S3PrerenderController( s3Service);
    }

    @Test
    void testCrawlerRequest_returnsHtml() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("User-Agent")).thenReturn("Googlebot");
        when(request.getRequestURI()).thenReturn("/en/blog/test-post");

        String expectedHtml = "<html><body>Test Page</body></html>";
        when(s3Service.getHtml("/en/blog/test-post")).thenReturn(expectedHtml);

        ResponseEntity<String> response = controller.handleRequest(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("text/html; charset=UTF-8", response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals(expectedHtml, response.getBody());
        verify(s3Service, times(1)).getHtml("/en/blog/test-post");
    }

    @Test
    void testNonCrawlerRequest_redirectsToIndex() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(request.getRequestURI()).thenReturn("/en/blog/test-post");

        ResponseEntity<String> response = controller.handleRequest(request);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals("/index.html", response.getHeaders().getFirst(HttpHeaders.LOCATION));
        verifyNoInteractions(s3Service);
    }

    @Test
    void testCrawlerRequest_s3ThrowsException_returns404() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("User-Agent")).thenReturn("Bingbot");
        when(request.getRequestURI()).thenReturn("/en/blog/missing-page");

        when(s3Service.getHtml("/en/blog/missing-page")).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<String> response = controller.handleRequest(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Page not found", response.getBody());
    }
}