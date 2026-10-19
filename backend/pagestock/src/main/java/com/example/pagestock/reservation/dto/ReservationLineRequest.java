package com.example.pagestock.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReservationLineRequest(
        @NotBlank(message = "Book isbn can not be empty.")
        String bookIsbn,

        @Min(value = 1, message = "You must at least order 1 book to place the order.")
        int quantity
) {
}
