package com.example.pagestock.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wordt gegooid wanneer een auteur wordt verwijderd terwijl er nog boeken aan gekoppeld zijn.
 * <p>
 * Geeft {@code 409 Conflict} terug aan de client.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class AuthorNotEmptyException extends RuntimeException {
    /**
     * @param author    id van de auteur die niet kan worden verwijderd
     * @param bookCount aantal boeken dat nog aan de auteur gekoppeld is
     */
    public AuthorNotEmptyException(Long author, long bookCount) {

        super("Can not delete author: " + author + ", it contains " + bookCount +  " books.");
    }
}
