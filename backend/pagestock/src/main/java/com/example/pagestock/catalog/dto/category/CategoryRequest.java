package com.example.pagestock.catalog.dto.category;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body voor het aanmaken of bijwerken van een categorie.
 *
 * @param name naam van de categorie; mag niet leeg zijn. Spaties vooraan en achteraan worden
 *             door de service verwijderd.
 */
public record CategoryRequest(

        @NotBlank(message = "Name of a category can not be empty.")
        String name
) { }
