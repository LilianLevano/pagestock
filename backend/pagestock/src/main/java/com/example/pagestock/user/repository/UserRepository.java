package com.example.pagestock.user.repository;

import com.example.pagestock.user.model.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

/**
 * Repository voor {@link User}-entiteiten.
 * <p>
 * De queries worden door Spring Data JPA afgeleid uit de methodenamen. De vergelijking op
 * e-mailadres en telefoonnummer is exact: geef het e-mailadres al in kleine letters en zonder
 * spaties mee.
 */
public interface UserRepository extends ListCrudRepository<User, Long> {
    /**
     * @param email e-mailadres in kleine letters
     * @return de gebruiker met dit e-mailadres, of {@link Optional#empty()} als die niet bestaat
     */
    Optional<User> findByEmail(String email);

    /**
     * @param email e-mailadres in kleine letters
     * @return {@code true} als er al een gebruiker met dit e-mailadres bestaat
     */
    boolean existsByEmail(String email);

    /**
     * Wordt gebruikt bij registratie, omdat het telefoonnummer uniek moet zijn. De vergelijking
     * is exact: spaties of een ander formaat (bv. {@code +32} in plaats van {@code 0}) tellen
     * als een ander nummer.
     *
     * @param phoneNumber telefoonnummer zoals het werd ingevuld
     * @return {@code true} als er al een gebruiker met dit telefoonnummer bestaat
     */
    boolean existsByPhoneNumber(String phoneNumber);
}
