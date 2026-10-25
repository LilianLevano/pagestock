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
import com.example.pagestock.user.model.Role;
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

/**
 * Business logic voor reservaties: aanmaken, ophalen, annuleren en opvragen.
 * <p>
 * Een reservatie doorloopt de statussen {@code RESERVED} → {@code PICKED_UP} of
 * {@code RESERVED} → {@code CANCELLED}. De voorraad wordt bij het reserveren alleen gecontroleerd
 * en pas bij het ophalen ({@link #confirmPickUp(Long)}) effectief verlaagd.
 * <p>
 * Voor boeken en gebruikers gaat deze service via {@link BookService} en {@link UserService},
 * niet rechtstreeks via hun repositories.
 */
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookService bookService;
    private final UserService userService;

    /**
     * Maakt de service aan met de benodigde afhankelijkheden.
     *
     * @param reservationRepository repository voor reservaties
     * @param bookService           service om boeken op te halen en de voorraad te verlagen
     * @param userService           service om de ingelogde gebruiker op te halen via zijn e-mailadres
     */
    public ReservationService(ReservationRepository reservationRepository, BookService bookService, UserService userService) {
        this.reservationRepository = reservationRepository;
        this.bookService = bookService;
        this.userService = userService;
    }

    /**
     * Maakt een nieuwe reservatie aan met status {@code RESERVED} voor de opgegeven gebruiker.
     * <p>
     * Voor elke lijn wordt gecontroleerd dat het boek bestaat, niet dubbel voorkomt en voldoende
     * voorraad heeft. De titel van het boek wordt in de lijn bewaard. De voorraad zelf wordt hier
     * niet verlaagd.
     *
     * @param email   e-mailadres van de ingelogde gebruiker (uit het JWT-token)
     * @param request de lijnen van de reservatie
     * @return de opgeslagen reservatie
     * @throws UsernameNotFoundException            als er geen gebruiker met dit e-mailadres bestaat
     * @throws ReservationEmptyException            als de reservatie geen lijnen bevat
     * @throws DuplicateBookInReservationException  als hetzelfde boek meer dan één keer voorkomt
     * @throws ItemNotFoundException                als een van de boeken niet bestaat
     * @throws OutOfStockException                  als de voorraad van een boek onvoldoende is
     */
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

    /**
     * Bevestigt dat een reservatie is opgehaald: verlaagt de voorraad van elk boek, zet de status
     * op {@code PICKED_UP} en registreert het tijdstip van ophaling.
     * <p>
     * Alles gebeurt in één transactie: faalt het verlagen van de voorraad voor één boek, dan worden
     * ook de eerder verlaagde voorraden teruggedraaid en blijft de reservatie {@code RESERVED}.
     * De wijzigingen worden via dirty checking opgeslagen, zonder expliciete {@code save}.
     *
     * @param id id van de reservatie
     * @return de opgehaalde reservatie
     * @throws ItemNotFoundException           als de reservatie of een van haar boeken niet bestaat
     * @throws ReservationNotReservedException als de reservatie niet meer {@code RESERVED} is
     * @throws OutOfStockException             als de voorraad van een boek intussen onvoldoende is
     */
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

    /**
     * Annuleert een reservatie; de status wordt {@code CANCELLED}.
     * <p>
     * Alleen de eigenaar van de reservatie of een {@code ADMIN} mag annuleren, en alleen zolang
     * de reservatie {@code RESERVED} is. De voorraad verandert niet, omdat die bij het reserveren
     * nog niet werd verlaagd.
     *
     * @param id    id van de reservatie
     * @param email e-mailadres van de ingelogde gebruiker (uit het JWT-token)
     * @return de geannuleerde reservatie
     * @throws ItemNotFoundException           als er geen reservatie met dit id bestaat
     * @throws UsernameNotFoundException       als er geen gebruiker met dit e-mailadres bestaat
     * @throws NotOwnerOfReservationException  als de gebruiker geen eigenaar en geen {@code ADMIN} is
     * @throws ReservationNotReservedException als de reservatie niet meer {@code RESERVED} is
     */
    @Transactional
    public ReservationResponse cancelReservation(Long id, String email){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Reservation " + id + " not found"));

        // Gebruiker ophalen via het e-mailadres uit het token
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!Objects.equals(reservation.getUserId(), user.getId()) && user.getRole() != Role.ADMIN){
            throw new NotOwnerOfReservationException("User " + user.getId() + " is not owner of this reservation (id: " + reservation.getId() + ")");
        }

        if (reservation.getStatus() != ReservationStatus.RESERVED){
            throw new ReservationNotReservedException("Reservation " + id + " is not reserved. That reservation is: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        return toResponse(reservation);
    }

    /**
     * Haalt alle reservaties van een gebruiker op, ongeacht hun status, de meest recente eerst.
     *
     * @param email e-mailadres van de ingelogde gebruiker (uit het JWT-token)
     * @return de reservaties van de gebruiker; leeg als er geen zijn
     * @throws UsernameNotFoundException als er geen gebruiker met dit e-mailadres bestaat
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> findReservationsOfUser(String email){
         User user = userService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));
        return reservationRepository.findByUserIdOrderByReservedAtDesc(user.getId()).stream().map(this::toResponse).toList();
    }

    /**
     * Haalt de opgehaalde reservaties (status {@code PICKED_UP}) van een gebruiker op, dus de
     * boeken die de gebruiker effectief heeft gekocht.
     *
     * @param email e-mailadres van de ingelogde gebruiker (uit het JWT-token)
     * @return de opgehaalde reservaties; leeg als er geen zijn
     * @throws UsernameNotFoundException als er geen gebruiker met dit e-mailadres bestaat
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllByUserIdAndPickedUp(String email){
        User user = userService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User " + email + " not found"));
        return reservationRepository.findByUserIdAndStatus(user.getId(), ReservationStatus.PICKED_UP).stream().map(this::toResponse).toList();
    }

    /**
     * Haalt alle reservaties op die nog moeten worden opgehaald (status {@code RESERVED}),
     * van alle gebruikers.
     *
     * @return de openstaande reservaties; leeg als er geen zijn
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> findReservationsToPickUp(){
        return reservationRepository.findByStatus(ReservationStatus.RESERVED).stream().map(this::toResponse).toList();
    }

    /**
     * Controleert of een gebruiker een bepaald boek heeft opgehaald, via een reservatie met status
     * {@code PICKED_UP}. Wordt gebruikt door de review-module: alleen wie een boek heeft opgehaald,
     * mag er een review over schrijven.
     *
     * @param userId   id van de gebruiker
     * @param bookIsbn ISBN-nummer van het boek
     * @return {@code true} als de gebruiker het boek minstens één keer heeft opgehaald
     */
    public boolean hasPickedUp(Long userId, String bookIsbn){
        return reservationRepository.existsByUserIdAndStatusAndLines_BookIsbn(userId, ReservationStatus.PICKED_UP, bookIsbn);
    }

    /**
     * Zet een {@link Reservation}-entiteit om naar een {@link ReservationResponse}, inclusief haar lijnen.
     *
     * @param reservation de om te zetten reservatie
     * @return de response-representatie van de reservatie
     */
    public ReservationResponse toResponse(Reservation  reservation){
        return new ReservationResponse(
                reservation.getId(),
                reservation.getStatus(),
                reservation.getReservedAt(),
                reservation.getPickedUpAt(),
                reservation.getLines().stream().map(this::toLineResponse).toList()
        );
    }

    /**
     * Zet een {@link ReservationLine} om naar een {@link ReservationLineResponse}.
     *
     * @param line de om te zetten lijn
     * @return de response-representatie van de lijn
     */
    public ReservationLineResponse toLineResponse(ReservationLine line){
        return new ReservationLineResponse(line.getBookIsbn(), line.getQuantity(), line.getBookTitle());
    }
}
