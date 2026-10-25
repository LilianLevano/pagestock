package com.example.pagestock.reservation.controller;

import com.example.pagestock.reservation.dto.CreateReservationRequest;
import com.example.pagestock.reservation.dto.ReservationResponse;
import com.example.pagestock.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService){
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(Authentication authentication,@Valid @RequestBody CreateReservationRequest request){
        String emailUser = authentication.getName();
        return  ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(emailUser,request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReservationResponse>> myReservations(Authentication authentication){
        String emailUser = authentication.getName();
        return ResponseEntity.ok(reservationService.findReservationsOfUser(emailUser));
    }

    @GetMapping("/me/purchased")
    public ResponseEntity<List<ReservationResponse>> myPurchasedReservations(Authentication authentication){
        String emailUser = authentication.getName();
        return ResponseEntity.ok(reservationService.findAllByUserIdAndPickedUp(emailUser));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> update(Authentication authentication, @PathVariable Long id){
        String emailUser = authentication.getName();
        return ResponseEntity.ok(reservationService.cancelReservation(id,emailUser));
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findAll(){
        return ResponseEntity.ok(reservationService.findReservationsToPickUp());
    }

    @PatchMapping("/{id}/pickup")
    public ResponseEntity<ReservationResponse> pickUp(@PathVariable Long id){
        return ResponseEntity.ok(reservationService.confirmPickUp(id));
    }



}
