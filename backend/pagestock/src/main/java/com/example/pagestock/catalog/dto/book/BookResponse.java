package com.example.pagestock.catalog.dto.book;

import com.example.pagestock.catalog.dto.author.AuthorResponse;
import com.example.pagestock.catalog.dto.category.CategoryResponse;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


/**
 * Response met de volledige gegevens van een boek, inclusief categorie en auteurs.
 *
 * @param isbnNumber      ISBN-nummer van het boek
 * @param title           titel van het boek
 * @param description     beschrijving van het boek
 * @param price           verkoopprijs
 * @param publicationDate publicatiedatum
 * @param stock           aantal exemplaren op voorraad
 * @param category        categorie waartoe het boek behoort
 * @param authors         auteurs van het boek
 */
public record BookResponse(
        String isbnNumber,
        String title,
        String description,
        BigDecimal price,
        LocalDate publicationDate,
        Long stock,
        CategoryResponse category,
        List<AuthorResponse> authors
) { }
