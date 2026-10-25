package com.example.pagestock.catalog.dto.book;


import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * Request body voor het bijwerken van een bestaand boek.
 * <p>
 * Het ISBN-nummer zit niet in de body maar wordt via de URL meegegeven en kan niet worden
 * gewijzigd. Alle andere velden worden volledig overschreven.
 *
 * @param title           titel van het boek; mag niet leeg zijn
 * @param description     beschrijving van het boek; mag niet leeg zijn
 * @param price           verkoopprijs; verplicht en niet negatief
 * @param publicationDate publicatiedatum; verplicht
 * @param stock           aantal exemplaren op voorraad; verplicht en niet negatief
 * @param categoryId      id van een bestaande categorie; verplicht
 * @param authorIds       id's van bestaande auteurs; minstens één auteur verplicht. Vervangt
 *                        de huidige auteurs volledig.
 */
public record BookUpdateRequest(

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
