package com.example.pagestock.reservation.model;

import jakarta.persistence.*;

/**
 * Eén lijn van een {@link Reservation}: een boek en het aantal gereserveerde exemplaren.
 * <p>
 * Wordt opgeslagen in de tabel {@code reservation_lines}. Het boek wordt via zijn ISBN-nummer
 * bijgehouden, niet via een JPA-relatie naar {@code Book}, zodat de modules {@code reservation}
 * en {@code catalog} los van elkaar blijven.
 */
@Entity
@Table(name = "reservation_lines")
public class ReservationLine {

    /** Unieke identificatie van de lijn; wordt automatisch gegenereerd door de database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ISBN-nummer van het gereserveerde boek. */
    private String bookIsbn;

    /** Aantal gereserveerde exemplaren van dit boek. */
    private int quantity;

    /**
     * Titel van het boek op het moment van reserveren.
     * <p>
     * Wordt bewaard zodat de reservatie leesbaar blijft zonder het boek op te halen, ook als de
     * titel later wijzigt of het boek wordt verwijderd.
     */
    private String bookTitle;

    /** De reservatie waartoe deze lijn behoort (kolom {@code reservation_id}). */
    @ManyToOne()
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    /** Lege constructor, vereist door JPA. */
    public ReservationLine() {
    }

    /**
     * Maakt een lijn aan zonder titel en zonder reservatie; koppel ze aan een reservatie via
     * {@link Reservation#addLine(ReservationLine)}.
     *
     * @param bookIsbn ISBN-nummer van het boek
     * @param quantity aantal exemplaren
     */
    public ReservationLine(String bookIsbn, int quantity) {
        this.bookIsbn = bookIsbn;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
