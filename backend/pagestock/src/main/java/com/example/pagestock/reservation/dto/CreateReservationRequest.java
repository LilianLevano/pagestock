package com.example.pagestock.reservation.dto;

import com.example.pagestock.reservation.model.Reservation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateReservationRequest(
        @NotEmpty
        @Valid
        List<ReservationLineRequest> lines
) {
}
