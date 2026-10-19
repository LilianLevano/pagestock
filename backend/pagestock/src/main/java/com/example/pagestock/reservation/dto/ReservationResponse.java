package com.example.pagestock.reservation.dto;

import com.example.pagestock.reservation.model.ReservationLine;
import com.example.pagestock.reservation.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponse(
        Long id,
        ReservationStatus status,
        LocalDateTime reservedAt,
        LocalDateTime pickedUpAt,
        List<ReservationLineResponse> lines
) {
}
