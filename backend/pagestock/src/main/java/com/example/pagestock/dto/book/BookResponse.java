package com.example.pagestock.dto.book;

import com.example.pagestock.dto.category.CategoryResponse;
import com.example.pagestock.model.Author;
import com.example.pagestock.model.Category;

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
        CategoryResponse category
) { }
