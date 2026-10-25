package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid wanneer een gebruiker een review wil verwijderen die niet van hem of haar is.
 * Een {@code ADMIN} mag elke review verwijderen.
 * <p>
 * Geeft {@code 403 Forbidden} terug aan de client.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ReviewDeleteNotAllowedException extends RuntimeException {
    /**
     * @param message foutmelding voor de client
     */
    public ReviewDeleteNotAllowedException(String message) {
        super(message);
    }
}
