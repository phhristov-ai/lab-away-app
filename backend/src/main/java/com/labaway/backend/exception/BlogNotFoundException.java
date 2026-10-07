package com.labaway.backend.exception;

public class BlogNotFoundException extends RuntimeException {

    public BlogNotFoundException(String slug) {
        super("Blog with slug '" + slug + "' was not found.");
    }
}