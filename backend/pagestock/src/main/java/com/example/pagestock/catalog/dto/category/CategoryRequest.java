package com.example.pagestock.catalog.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(

        @NotBlank(message = "Name of a category can not be empty.")
        String name
) { }
