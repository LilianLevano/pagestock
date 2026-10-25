package com.example.pagestock.reservation.repository;

import com.example.pagestock.reservation.model.Reservation;
import com.example.pagestock.reservation.model.ReservationStatus;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

/**
 * Repository voor {@link Reservation}-entiteiten.
 * <p>
 * De queries worden door Spring Data JPA afgeleid uit de methodenamen. Alleen
 * {@link #findByUserIdOrderByReservedAtDesc(Long)} sorteert; bij de andere methodes hangt de
 * volgorde van de resultaten af van de database.
 */
public interface ReservationRepository extends ListCrudRepository<Reservation, Long> {
    /**
     * @param userId id van de gebruiker
     * @return alle reservaties van deze gebruiker, ongeacht hun status, gesorteerd op
     *         reservatiedatum (meest recent eerst)
     */
    List<Reservation> findByUserIdOrderByReservedAtDesc(Long userId);

    /**
     * @param userId id van de gebruiker
     * @param status de gezochte status
     * @return de reservaties van deze gebruiker met de opgegeven status
     */
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    /**
     * @param status de gezochte status
     * @return alle reservaties met de opgegeven status, van alle gebruikers
     */
    List<Reservation> findByStatus(ReservationStatus status);


    /**
     * Controleert of een gebruiker een reservatie met de opgegeven status heeft die een bepaald
     * boek bevat.
     * <p>
     * {@code Lines_BookIsbn} zoekt via de relatie {@link Reservation#getLines()} in het veld
     * {@code bookIsbn} van elke lijn; de underscore maakt expliciet dat {@code BookIsbn} een veld
     * van de lijnen is. Wordt gebruikt om na te gaan of een gebruiker een boek heeft opgehaald
     * voordat hij of zij er een review over mag schrijven.
     *
     * @param userId   id van de gebruiker
     * @param status   de gezochte status, in de praktijk {@link ReservationStatus#PICKED_UP}
     * @param bookIsbn ISBN-nummer van het boek
     * @return {@code true} als minstens één zo'n reservatie bestaat
     */
    boolean existsByUserIdAndStatusAndLines_BookIsbn(Long userId, ReservationStatus status, String bookIsbn);

}
