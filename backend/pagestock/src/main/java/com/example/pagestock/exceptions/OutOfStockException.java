package com.example.pagestock.exceptions;

import com.example.pagestock.catalog.model.Book;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String isbnNumber, Long stock) {
        super("You can't order that many of that book ( " + isbnNumber + " ). There are only: " + stock + " left.");
    }
}
