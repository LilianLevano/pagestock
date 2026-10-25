package com.example.pagestock.catalog.dto.author;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body voor het aanmaken of bijwerken van een auteur.
 *
 * @param name naam van de auteur; mag niet leeg zijn. Spaties vooraan en achteraan worden
 *             door de service verwijderd.
 */
public record AuthorRequest(
        @NotBlank(message = "The name of an author can not be empty.")
        String name
) { }
