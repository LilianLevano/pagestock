package com.example.pagestock.review.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        int rating,
        String comment,
        LocalDateTime createdAt,
        String authorReviewName
) {
}
