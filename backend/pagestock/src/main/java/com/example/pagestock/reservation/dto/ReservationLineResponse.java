package com.example.pagestock.reservation.dto;

import com.example.pagestock.catalog.service.BookService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReservationLineResponse(
        @NotBlank
        String bookIsbn,

        @Min(1)
        int quantity,

        String bookTitle

) {
}
