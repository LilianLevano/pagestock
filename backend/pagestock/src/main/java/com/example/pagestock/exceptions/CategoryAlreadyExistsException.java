package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String name) {
        super("A category with this name: " + name + " already exists");
    }
}
