package com.example.pagestock.catalog.dto.book;

import com.example.pagestock.catalog.dto.author.AuthorResponse;
import com.example.pagestock.catalog.dto.category.CategoryResponse;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public record BookResponse(
        String isbnNumber,
        String title,
        String description,
        BigDecimal price,
        LocalDate publicationDate,
        Long stock,
        CategoryResponse category,
        List<AuthorResponse> authors
) { }
