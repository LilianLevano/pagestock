package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AuthorAlreadyExistsException extends RuntimeException {
    public AuthorAlreadyExistsException(String name) {
        super("Author already exists: " + name);
    }
}
