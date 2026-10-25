package com.example.pagestock.catalog.dto.category;

/**
 * Response met de gegevens van een categorie.
 *
 * @param id   unieke identificatie van de categorie
 * @param name naam van de categorie
 */
public record CategoryResponse(Long id, String name) {
}
