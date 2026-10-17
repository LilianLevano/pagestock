package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AuthorNotEmptyException extends RuntimeException {
    public AuthorNotEmptyException(Long author, long bookCount) {

        super("Can not delete author: " + author + ", it contains " + bookCount +  " books.");
    }
}
