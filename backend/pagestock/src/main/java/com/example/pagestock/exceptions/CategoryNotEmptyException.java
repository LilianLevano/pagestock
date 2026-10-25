package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid wanneer een categorie wordt verwijderd terwijl er nog boeken in zitten.
 * <p>
 * Geeft {@code 409 Conflict} terug aan de client.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryNotEmptyException extends RuntimeException {
    /**
     * @param category  id van de categorie die niet kan worden verwijderd
     * @param bookCount aantal boeken dat nog in de categorie zit
     */
    public CategoryNotEmptyException(Long category, Long bookCount) {
        super("Can not delete category: " + category + ", it contains " + bookCount +  " books.");
    }
}
