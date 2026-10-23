package com.example.pagestock.review.dto;

import jakarta.validation.constraints.*;

public record CreateReviewRequest(

        @NotNull (message = "A review must have a rating")
        @Min(value = 1, message = "A rating can not be less than 1")
        @Max(value = 5, message = "A rating can not be more than 5")
        int rating,

        @NotBlank(message = "A comment can not be empty")
        @Size(max = 2000)
        String comment
) {
}
