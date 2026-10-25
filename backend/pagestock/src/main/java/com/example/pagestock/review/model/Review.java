package com.example.pagestock.review.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Een review van een boek door een gebruiker.
 * <p>
 * Wordt opgeslagen in de tabel {@code reviews}. Een gebruiker kan maximaal één review per boek
 * schrijven: dat wordt zowel in de service gecontroleerd als in de database afgedwongen met een
 * unieke combinatie van {@code book_isbn} en {@code user_id}.
 * <p>
 * Boek en gebruiker worden alleen via hun ISBN-nummer en id bijgehouden, niet via JPA-relaties,
 * zodat de module {@code review} los blijft van {@code catalog} en {@code user}.
 */
@Entity
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(columnNames = {"book_isbn", "user_id"}))
public class Review {

    /** Unieke identificatie van de review; wordt automatisch gegenereerd door de database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tekst van de review; maximaal 2000 tekens. */
    @Column(length = 2000)
    private String comment;

    /** Score van 1 tot en met 5. */
    @NotNull(message = "Rating can not be null")
    @Min(1)
    @Max(5)
    private int rating;

    /** ISBN-nummer van het beoordeelde boek; verplicht. */
    @NotBlank(message = "A review must be bound to a book.")
    private String bookIsbn;

    /** Id van de gebruiker die de review heeft geschreven; verplicht. */
    @NotNull(message = "A review must be bound")
    private Long userId;

    /** Tijdstip waarop de review werd geplaatst. */
    private LocalDateTime createdAt;

    /** Lege constructor, vereist door JPA. */
    public Review() {
    }

    /**
     * Maakt een nieuwe review aan; het id wordt bij het opslaan door de database toegekend.
     *
     * @param comment   tekst van de review
     * @param rating    score van 1 tot en met 5
     * @param bookIsbn  ISBN-nummer van het beoordeelde boek
     * @param userId    id van de auteur van de review
     * @param createdAt tijdstip van plaatsing
     */
    public Review(String comment, int rating, String bookIsbn, Long userId, LocalDateTime createdAt) {
        this.comment = comment;
        this.rating = rating;
        this.bookIsbn = bookIsbn;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

}
