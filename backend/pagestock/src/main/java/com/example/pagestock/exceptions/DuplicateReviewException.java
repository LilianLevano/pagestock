package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid wanneer een gebruiker een tweede review wil plaatsen op een boek dat hij of zij
 * al heeft beoordeeld. Per gebruiker is één review per boek toegestaan.
 * <p>
 * Geeft {@code 409 Conflict} terug aan de client.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateReviewException extends RuntimeException {
    /**
     * @param message foutmelding voor de client
     */
    public DuplicateReviewException(String message) {
        super(message);
    }
}
