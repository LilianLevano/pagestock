package com.example.pagestock.reservation.repository;

import com.example.pagestock.reservation.model.Reservation;
import com.example.pagestock.reservation.model.ReservationStatus;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface ReservationRepository extends ListCrudRepository<Reservation, Long> {
    List<Reservation> getReservationByUserId(Long userId);

    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    List<Reservation> findByStatus(ReservationStatus status);

}
