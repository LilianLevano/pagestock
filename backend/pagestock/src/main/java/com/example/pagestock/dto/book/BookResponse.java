package com.example.pagestock.dto.book;

import com.example.pagestock.dto.category.CategoryResponse;


import java.math.BigDecimal;
import java.time.LocalDate;


public record BookResponse(
        String isbnNumber,
        String title,
        String description,
        BigDecimal price,
        LocalDate publicationDate,
        Long stock,
        CategoryResponse category
) { }
