package com.example.pagestock.review.dto;

import java.time.LocalDate;

public record ReviewResponse(
        Long id,
        int rating,
        String comment,
        LocalDate createdAt,
        String authorReviewName
) {
}
