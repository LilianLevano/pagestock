package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid wanneer een gebruiker een review wil plaatsen op een boek dat hij of zij nog niet
 * heeft opgehaald via een reservatie.
 * <p>
 * Geeft {@code 403 Forbidden} terug aan de client.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ReviewNotAllowedException extends RuntimeException {
    /**
     * @param message foutmelding voor de client
     */
    public ReviewNotAllowedException(String message) {
        super(message);
    }
}
