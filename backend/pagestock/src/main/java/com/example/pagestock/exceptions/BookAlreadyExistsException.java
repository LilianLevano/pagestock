package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid wanneer een boek wordt aangemaakt met een ISBN-nummer dat al bestaat.
 * <p>
 * Geeft {@code 409 Conflict} terug aan de client.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyExistsException extends RuntimeException {
    /**
     * @param isbnNumber het ISBN-nummer dat al in gebruik is
     */
    public BookAlreadyExistsException(String isbnNumber) {
        super("Book already exists: " + isbnNumber);
    }
}
