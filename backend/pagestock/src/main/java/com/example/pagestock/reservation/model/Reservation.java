package com.example.pagestock.reservation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Een reservatie van een of meer boeken door een gebruiker.
 * <p>
 * Wordt opgeslagen in de tabel {@code reservations}. Een reservatie bestaat uit lijnen
 * ({@link ReservationLine}), één per boek, en doorloopt de statussen uit {@link ReservationStatus}.
 * <p>
 * De gebruiker wordt alleen via zijn id bijgehouden ({@link #userId}), niet via een JPA-relatie
 * naar {@code User}, zodat de modules {@code reservation} en {@code user} los van elkaar blijven.
 */
@Entity
@Table(name="reservations")
public class Reservation {

    /** Unieke identificatie van de reservatie; wordt automatisch gegenereerd door de database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Id van de gebruiker die de reservatie heeft gemaakt; verplicht. */
    @Column(nullable = false)
    @NotNull(message = "A user must be linked to a reservation.")
    private Long userId;

    /**
     * Lijnen van de reservatie, één per boek.
     * <p>
     * Dankzij {@code cascade = ALL} worden de lijnen samen met de reservatie opgeslagen en
     * verwijderd; dankzij {@code orphanRemoval} wordt een lijn die uit deze lijst wordt gehaald
     * ook uit de database verwijderd. Gebruik {@link #addLine(ReservationLine)} om een lijn toe
     * te voegen.
     */
    @OneToMany(mappedBy = "reservation",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationLine> lines = new ArrayList<>();

    /** Huidige status van de reservatie; wordt als tekst opgeslagen (bv. {@code "RESERVED"}). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    /** Tijdstip waarop de reservatie werd aangemaakt; verplicht. */
    @NotNull(message = "Reservation date can not be empty.")
    private LocalDateTime reservedAt;

    /** Tijdstip waarop de reservatie werd opgehaald; {@code null} zolang ze niet is opgehaald. */
    @Column(nullable = true)
    private LocalDateTime pickedUpAt;

    /** Lege constructor, vereist door JPA. */
    public Reservation() {
    }

    /**
     * Maakt een reservatie aan zonder lijnen; voeg ze daarna toe met {@link #addLine(ReservationLine)}.
     *
     * @param userId     id van de gebruiker die reserveert
     * @param status     beginstatus, normaal {@link ReservationStatus#RESERVED}
     * @param reservedAt tijdstip van de reservatie
     */
    public Reservation(Long userId, ReservationStatus status, LocalDateTime reservedAt) {
        this.userId = userId;
        this.status = status;
        this.reservedAt = reservedAt;
    }

    /**
     * Voegt een lijn toe aan deze reservatie en koppelt de lijn terug aan deze reservatie.
     * <p>
     * Beide kanten van de relatie worden ingesteld: zonder {@code line.setReservation(this)}
     * zou de kolom {@code reservation_id} van de lijn leeg blijven in de database.
     *
     * @param line de toe te voegen lijn
     */
    public void addLine(ReservationLine line) {
        lines.add(line);
        line.setReservation(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<ReservationLine> getLines() {
        return lines;
    }

    public void setLines(List<ReservationLine> lines) {
        this.lines = lines;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
    }

    public LocalDateTime getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(LocalDateTime pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }
}
