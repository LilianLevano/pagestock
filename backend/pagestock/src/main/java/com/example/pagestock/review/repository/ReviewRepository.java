package com.example.pagestock.review.repository;

import com.example.pagestock.reservation.model.ReservationStatus;
import com.example.pagestock.review.model.Review;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

/**
 * Repository voor {@link Review}-entiteiten.
 * <p>
 * De queries worden door Spring Data JPA afgeleid uit de methodenamen.
 */
public interface ReviewRepository extends ListCrudRepository<Review, Long> {
    /**
     * @param bookIsbn ISBN-nummer van het boek
     * @return alle reviews van dit boek, de meest recente eerst
     */
    List<Review> findByBookIsbnOrderByCreatedAtDesc(String bookIsbn);

    /**
     * Wordt gebruikt om te voorkomen dat een gebruiker twee reviews op hetzelfde boek plaatst.
     *
     * @param bookIsbn ISBN-nummer van het boek
     * @param userId   id van de gebruiker
     * @return {@code true} als deze gebruiker al een review op dit boek heeft geplaatst
     */
    boolean existsByBookIsbnAndUserId(String bookIsbn, Long userId);

}
