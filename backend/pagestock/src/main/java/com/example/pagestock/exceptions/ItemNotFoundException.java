package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Algemene exception voor wanneer een gevraagd object (boek, auteur, categorie, ...) niet bestaat.
 * <p>
 * Geeft {@code 404 Not Found} terug aan de client.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException {
    /**
     * @param message foutmelding voor de client, bij voorkeur met het type en het id van het object
     */
    public ItemNotFoundException(String message) {
        super(message);
    }
}
