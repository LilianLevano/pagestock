package com.example.pagestock.catalog.dto.book;


import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

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
