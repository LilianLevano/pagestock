package com.example.pagestock.dto.book;

import jakarta.persistence.Id;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookUpdateRequest(

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
