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

/**
 * REST-controller voor reservaties, bereikbaar onder {@code /reservations}.
 * <p>
 * Een reservatie doorloopt de statussen {@code RESERVED} → {@code PICKED_UP} (opgehaald)
 * of {@code RESERVED} → {@code CANCELLED} (geannuleerd).
 * <p>
 * Toegang (zie {@code SecurityConfig}):
 * <ul>
 *   <li>{@code GET /reservations} en {@code PATCH /reservations/{id}/pickup}: alleen {@code ADMIN}.</li>
 *   <li>Alle andere endpoints: elke ingelogde gebruiker. De gebruiker wordt bepaald via het
 *       e-mailadres in het JWT-token ({@link Authentication#getName()}).</li>
 * </ul>
 * Zonder geldig JWT-token volgt {@code 401 Unauthorized}, zonder de vereiste rol
 * {@code 403 Forbidden}. Een {@code id} dat geen getal is geeft {@code 400 Bad Request}.
 */
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Maakt de controller aan.
     *
     * @param reservationService service met de business logic voor reservaties
     */
    @Autowired
    public ReservationController(ReservationService reservationService){
        this.reservationService = reservationService;
    }

    /**
     * Maakt een nieuwe reservatie aan voor de ingelogde gebruiker, met status {@code RESERVED}.
     * <p>
     * De voorraad wordt hier alleen gecontroleerd, niet verlaagd: dat gebeurt pas bij het ophalen
     * ({@link #pickUp(Long)}).
     * <p>
     * Endpoint: {@code POST /reservations} — ingelogde gebruiker.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 400 Bad Request} — ongeldige body (geen lijnen, leeg ISBN-nummer, hoeveelheid kleiner
     *       dan 1) of hetzelfde boek komt meer dan één keer voor</li>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 404 Not Found} — een van de boeken bestaat niet</li>
     *   <li>{@code 409 Conflict} — onvoldoende voorraad voor een van de boeken</li>
     * </ul>
     *
     * @param authentication de ingelogde gebruiker, ingevuld door Spring Security
     * @param request        de lijnen van de reservatie (ISBN-nummer en hoeveelheid per boek)
     * @return {@code 201 Created} met de aangemaakte reservatie
     */
    @PostMapping
    public ResponseEntity<ReservationResponse> create(Authentication authentication,@Valid @RequestBody CreateReservationRequest request){
        String emailUser = authentication.getName();
        return  ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(emailUser,request));
    }

    /**
     * Haalt alle reservaties van de ingelogde gebruiker op, ongeacht hun status.
     * <p>
     * Endpoint: {@code GET /reservations/me} — ingelogde gebruiker.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     * </ul>
     *
     * @param authentication de ingelogde gebruiker, ingevuld door Spring Security
     * @return {@code 200 OK} met de reservaties van de gebruiker; leeg als er geen zijn
     */
    @GetMapping("/me")
    public ResponseEntity<List<ReservationResponse>> myReservations(Authentication authentication){
        String emailUser = authentication.getName();
        return ResponseEntity.ok(reservationService.findReservationsOfUser(emailUser));
    }

    /**
     * Haalt de opgehaalde reservaties (status {@code PICKED_UP}) van de ingelogde gebruiker op,
     * dus de boeken die de gebruiker effectief heeft gekocht.
     * <p>
     * Endpoint: {@code GET /reservations/me/purchased} — ingelogde gebruiker.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     * </ul>
     *
     * @param authentication de ingelogde gebruiker, ingevuld door Spring Security
     * @return {@code 200 OK} met de opgehaalde reservaties; leeg als er geen zijn
     */
    @GetMapping("/me/purchased")
    public ResponseEntity<List<ReservationResponse>> myPurchasedReservations(Authentication authentication){
        String emailUser = authentication.getName();
        return ResponseEntity.ok(reservationService.findAllByUserIdAndPickedUp(emailUser));
    }

    /**
     * Annuleert een reservatie van de ingelogde gebruiker; de status wordt {@code CANCELLED}.
     * <p>
     * Alleen de eigenaar (en een admin) van de reservatie kan haar annuleren, en alleen zolang ze nog
     * {@code RESERVED} is. De voorraad verandert niet, omdat die bij het reserveren nog niet
     * werd verlaagd.
     * <p>
     * Endpoint: {@code PATCH /reservations/{id}/cancel} — ingelogde gebruiker (eigenaar) of admin.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 403 Forbidden} — de reservatie is niet van deze gebruiker, of ze is niet meer
     *       {@code RESERVED} (al opgehaald of geannuleerd)</li>
     *   <li>{@code 404 Not Found} — er bestaat geen reservatie met dit id</li>
     * </ul>
     *
     * @param authentication de ingelogde gebruiker, ingevuld door Spring Security
     * @param id             id van de reservatie
     * @return {@code 200 OK} met de geannuleerde reservatie
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancel(Authentication authentication, @PathVariable Long id){
        String emailUser = authentication.getName();
        return ResponseEntity.ok(reservationService.cancelReservation(id,emailUser));
    }

    /**
     * Haalt alle reservaties op die nog moeten worden opgehaald (status {@code RESERVED}),
     * van alle gebruikers.
     * <p>
     * Endpoint: {@code GET /reservations} — alleen {@code ADMIN}.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 403 Forbidden} — gebruiker heeft de rol {@code ADMIN} niet</li>
     * </ul>
     *
     * @return {@code 200 OK} met de openstaande reservaties; leeg als er geen zijn
     */
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findAll(){
        return ResponseEntity.ok(reservationService.findReservationsToPickUp());
    }

    /**
     * Bevestigt dat een reservatie is opgehaald; de status wordt {@code PICKED_UP}.
     * <p>
     * Pas op dit moment wordt de voorraad van elk boek in de reservatie verlaagd. Alles gebeurt
     * in één transactie: als één boek onvoldoende voorraad heeft, wordt niets gewijzigd.
     * <p>
     * Endpoint: {@code PATCH /reservations/{id}/pickup} — alleen {@code ADMIN}.
     * <p>
     * Foutcodes:
     * <ul>
     *   <li>{@code 401 Unauthorized} — geen geldig JWT-token</li>
     *   <li>{@code 403 Forbidden} — gebruiker heeft de rol {@code ADMIN} niet, of de reservatie
     *       is niet meer {@code RESERVED}</li>
     *   <li>{@code 404 Not Found} — er bestaat geen reservatie met dit id, of een boek uit de
     *       reservatie bestaat niet meer</li>
     *   <li>{@code 409 Conflict} — onvoldoende voorraad voor een van de boeken (de voorraad kan
     *       sinds het reserveren gedaald zijn)</li>
     * </ul>
     *
     * @param id id van de reservatie
     * @return {@code 200 OK} met de opgehaalde reservatie
     */
    @PatchMapping("/{id}/pickup")
    public ResponseEntity<ReservationResponse> pickUp(@PathVariable Long id){
        return ResponseEntity.ok(reservationService.confirmPickUp(id));
    }



}
