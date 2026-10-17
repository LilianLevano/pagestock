package com.example.pagestock.dto.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthorRequest(
        @NotBlank(message = "The name of an author can not be empty.")
        String name
) { }
