package com.example.pagestock.user.repository;

import com.example.pagestock.user.model.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

/**
 * Repository voor {@link User}-entiteiten.
 * <p>
 * De queries worden door Spring Data JPA afgeleid uit de methodenamen. De vergelijking op
 * e-mailadres is exact: geef het e-mailadres al in kleine letters en zonder spaties mee.
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
}
