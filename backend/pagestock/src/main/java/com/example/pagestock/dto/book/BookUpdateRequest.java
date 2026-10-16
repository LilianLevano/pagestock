package com.example.pagestock.dto.book;


import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

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

        Long[] author_ids


) { }
