package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryNotEmptyException extends RuntimeException {
    public CategoryNotEmptyException(Long category, Long bookCount) {
        super("Can not delete category: " + category + ", it contains " + bookCount +  " books.");
    }
}
