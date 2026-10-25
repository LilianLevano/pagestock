package com.example.pagestock.catalog.dto.book;


import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * Request body voor het aanmaken van een nieuw boek.
 * <p>
 * Bevat, in tegenstelling tot {@link BookUpdateRequest}, ook het ISBN-nummer, omdat dat
 * als primaire sleutel dient en na het aanmaken niet meer kan worden gewijzigd.
 *
 * @param isbnNumber      ISBN-nummer: 10 tekens (9 cijfers gevolgd door een cijfer of {@code X})
 *                        of 13 cijfers, zonder streepjes of spaties
 * @param title           titel van het boek; mag niet leeg zijn
 * @param description     beschrijving van het boek; mag niet leeg zijn
 * @param price           verkoopprijs; verplicht en niet negatief
 * @param publicationDate publicatiedatum; verplicht
 * @param stock           aantal exemplaren op voorraad; verplicht en niet negatief
 * @param categoryId      id van een bestaande categorie; verplicht
 * @param authorIds       id's van bestaande auteurs; minstens één auteur verplicht
 */
public record BookRequest(

        @NotBlank
        @Pattern(regexp = "\\d{9}[0-9X]|\\d{13}", message = "ISBN must be 10 characters (9 digits followed by a digit or X) or 13 digits.")
        String isbnNumber,

        @NotBlank(message = "Title can not be empty.")
        String title,

        @NotBlank(message = "Description can not be empty.")
        String description,

        @NotNull(message = "Price can not be null.")
        @PositiveOrZero(message = "Price can not be negative.")
        BigDecimal price,

        @NotNull(message = "Publication date can not be empty.")
        LocalDate publicationDate,

        @NotNull(message = "Stock can not be null.")
        @PositiveOrZero(message = "Stock must be at least 0.")
        Long stock,

        @NotNull(message = "Category can not be empty.")
        Long categoryId,

        @NotEmpty(message = "A book must have at least one author.")
        Set<Long> authorIds


) { }
