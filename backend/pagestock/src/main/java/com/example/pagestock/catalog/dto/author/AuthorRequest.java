package com.example.pagestock.catalog.dto.author;

import jakarta.validation.constraints.NotBlank;

public record AuthorRequest(
        @NotBlank(message = "The name of an author can not be empty.")
        String name
) { }
