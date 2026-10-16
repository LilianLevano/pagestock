package com.example.pagestock.dto.book;

import jakarta.persistence.Id;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookRequest(

        //978-2-07-036822-8
        @NotBlank
        @Pattern(regexp = "\\d{9}[0-9X]|\\d{13}", message = "ISBN must be 10 characters (9 digits followed by a digit or X) or 13 digits.")
        String isbnNumber,

        @NotBlank(message = "Title can not be empty.")
        String title,

        @NotBlank(message = "Description can not be empty.")
        String description,

        @NotNull(message = "Price can not be null.")
        @PositiveOrZero
        BigDecimal price,

        LocalDate publicationDate,

        Long stock,

        Long category_id,

        Long[] author_ids


) { }
