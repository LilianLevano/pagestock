package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException(String isbnNumber) {
        super("Book already exists: " + isbnNumber);
    }
}
