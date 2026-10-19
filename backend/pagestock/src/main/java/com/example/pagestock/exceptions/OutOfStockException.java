package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String isbnNumber) {
        super("Book ( " + isbnNumber + " ) is out of stock.");
    }
}
