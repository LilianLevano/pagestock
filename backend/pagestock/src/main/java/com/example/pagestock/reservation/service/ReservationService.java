package com.example.pagestock.reservation.service;

import com.example.pagestock.catalog.dto.book.BookResponse;
import com.example.pagestock.catalog.service.BookService;
import com.example.pagestock.exceptions.*;
import com.example.pagestock.reservation.dto.CreateReservationRequest;
import com.example.pagestock.reservation.dto.ReservationLineRequest;
import com.example.pagestock.reservation.dto.ReservationLineResponse;
import com.example.pagestock.reservation.dto.ReservationResponse;
import com.example.pagestock.reservation.model.Reservation;
import com.example.pagestock.reservation.model.ReservationLine;
import com.example.pagestock.reservation.model.ReservationStatus;
import com.example.pagestock.reservation.repository.ReservationRepository;
import com.example.pagestock.user.model.User;
import com.example.pagestock.user.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookService bookService;
    private final UserService userService;

    public ReservationService(ReservationRepository reservationRepository, BookService bookService, UserService userService) {
        this.reservationRepository = reservationRepository;
        this.bookService = bookService;
        this.userService = userService;
    }

    @Transactional
    public ReservationResponse create(String email, CreateReservationRequest request) {

        // Gebruiker ophalen via het e-mailadres uit het token
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(request.lines().isEmpty()){
            throw new ReservationEmptyException("You can't create reservation without any books.");
        }

        // Reservatie aanmaken met status RESERVED
        Reservation reservation = new Reservation();
        reservation.setUserId(user.getId());
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setReservedAt(LocalDateTime.now());

        Set<String> seenIsbns = new HashSet<>();

        for (ReservationLineRequest lineRequest : request.lines()) {

            // add() geeft false terug als het ISBN al in de set zit: dubbel boek
            if (!seenIsbns.add(lineRequest.bookIsbn())) {
                throw new DuplicateBookInReservationException("Book " + lineRequest.bookIsbn() + " appears twice in the reservation");
            }

            // Boek moet bestaan, anders 404
            BookResponse book = bookService.findById(lineRequest.bookIsbn()).orElseThrow(() ->  new ItemNotFoundException("Book " + lineRequest.bookIsbn() + " not found"));

            // Genoeg voorraad voor deze lijn, anders 409
            if (book.stock() < lineRequest.quantity()) {
                throw new OutOfStockException(lineRequest.bookIsbn(), book.stock());
            }

            // Lijn toevoegen; addLine zet ook line.setReservation(this)
            ReservationLine line = new ReservationLine();
            line.setBookIsbn(lineRequest.bookIsbn());
            line.setQuantity(lineRequest.quantity());
            line.setBookTitle(book.title());
            reservation.addLine(line);
        }

        // Eén save: dankzij cascade = ALL worden de lijnen mee opgeslagen
        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse confirmPickUp(Long id){

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Reservation " + id + " not found"));

        if (reservation.getStatus() != ReservationStatus.RESERVED){
            throw new ReservationNotReservedException("Reservation " + id + " is not reserved. That reservation is: " + reservation.getStatus());
        }

        for (ReservationLine line: reservation.getLines()) {
            bookService.decreaseStock(line.getBookIsbn(), line.getQuantity());
        }

        reservation.setStatus(ReservationStatus.PICKED_UP);
        reservation.setPickedUpAt(LocalDateTime.now());
        return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse cancelReservation(Long id, String email){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Reservation " + id + " not found"));

        // Gebruiker ophalen via het e-mailadres uit het token
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!Objects.equals(reservation.getUserId(), user.getId())){
            throw new NotOwnerOfReservationException("User " + user.getId() + " is not owner of this reservation (id: " + reservation.getId() + ")");
        }

        if (reservation.getStatus() != ReservationStatus.RESERVED){
            throw new ReservationNotReservedException("Reservation " + id + " is not reserved. That reservation is: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        return toResponse(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findReservationsOfUser(String email){
         User user = userService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));
        return reservationRepository.findByUserId(user.getId()).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllByUserIdAndPickedUp(String email){
        User user = userService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));
        return reservationRepository.findByUserIdAndStatus(user.getId(), ReservationStatus.PICKED_UP).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findReservationsToPickUp(){
        return reservationRepository.findByStatus(ReservationStatus.RESERVED).stream().map(this::toResponse).toList();
    }

    public boolean hasPickedUp(Long userId, String bookIsbn){
        return reservationRepository.existsByUserIdAndStatusAndLines_BookIsbn(userId, ReservationStatus.PICKED_UP, bookIsbn);
    }

    public ReservationResponse toResponse(Reservation  reservation){
        return new ReservationResponse(
                reservation.getId(),
                reservation.getStatus(),
                reservation.getReservedAt(),
                reservation.getPickedUpAt(),
                reservation.getLines().stream().map(this::toLineResponse).toList()
        );
    }

    public ReservationLineResponse toLineResponse(ReservationLine line){
        return new ReservationLineResponse(line.getBookIsbn(), line.getQuantity(), line.getBookTitle());
    }
}
