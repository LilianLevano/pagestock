package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid wanneer een categorie wordt aangemaakt of hernoemd naar een naam die al bestaat
 * (ongeacht hoofdletters).
 * <p>
 * Geeft {@code 409 Conflict} terug aan de client.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryAlreadyExistsException extends RuntimeException {
    /**
     * @param name de naam die al in gebruik is
     */
    public CategoryAlreadyExistsException(String name) {
        super("A category with this name: " + name + " already exists");
    }
}
