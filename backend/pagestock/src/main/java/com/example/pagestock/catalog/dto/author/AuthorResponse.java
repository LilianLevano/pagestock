package com.example.pagestock.catalog.dto.author;

/**
 * Response met de gegevens van een auteur.
 *
 * @param id   unieke identificatie van de auteur
 * @param name naam van de auteur
 */
public record AuthorResponse(Long id, String name) {
}
