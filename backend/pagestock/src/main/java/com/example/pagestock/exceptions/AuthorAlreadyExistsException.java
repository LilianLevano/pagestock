package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid wanneer een auteur wordt aangemaakt of hernoemd naar een naam die al bestaat
 * (ongeacht hoofdletters).
 * <p>
 * Geeft {@code 409 Conflict} terug aan de client.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class AuthorAlreadyExistsException extends RuntimeException {
    /**
     * @param name de naam die al in gebruik is
     */
    public AuthorAlreadyExistsException(String name) {
        super("Author already exists: " + name);
    }
}
