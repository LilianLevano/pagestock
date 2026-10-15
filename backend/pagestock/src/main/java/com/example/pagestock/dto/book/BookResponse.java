package com.example.pagestock.dto.book;

import com.example.pagestock.model.Author;
import com.example.pagestock.model.Category;

import java.time.LocalDate;
import java.util.List;

public record BookResponse(
        Long ISBN_number,
        String title,
        String description,
        float price,
        LocalDate publicationDate,
        Long stock
) {


}
