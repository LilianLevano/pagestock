package com.example.pagestock.dto.book;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookRequest(

        Long ISBN_number,

        @NotBlank(message = "Title can not be empty.")
        String title,

        @NotBlank(message = "Description can not be empty.")
        String description,

        @NotNull(message = "Price can not be null.")
        float price,

        LocalDate publicationDate,

        Long stock,

        Long category_id,

        Long[] author_ids


) { }
