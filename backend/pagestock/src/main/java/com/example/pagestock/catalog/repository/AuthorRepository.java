package com.example.pagestock.catalog.repository;

import com.example.pagestock.catalog.model.Author;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

/**
 * Repository voor {@link Author}-entiteiten.
 * <p>
 * De queries worden door Spring Data JPA afgeleid uit de methodenamen.
 */
public interface AuthorRepository extends ListCrudRepository<Author, Long> {
    /**
     * @return alle auteurs, oplopend gesorteerd op id
     */
    List<Author> findAllByOrderByIdAsc();

    /**
     * @param name tekst die in de naam moet voorkomen
     * @return auteurs waarvan de naam {@code name} bevat, ongeacht hoofdletters
     */
    List<Author> findByNameContainingIgnoreCase(String name);

    /**
     * @param name volledige naam om te controleren
     * @return {@code true} als er een auteur met exact deze naam bestaat, ongeacht hoofdletters
     */
    boolean existsByNameEqualsIgnoreCase(String name);

    /**
     * Controleert of een <em>andere</em> auteur al deze naam heeft; gebruikt bij het bijwerken,
     * zodat de auteur zelf niet als duplicaat telt.
     *
     * @param name volledige naam om te controleren
     * @param id   id van de auteur die wordt bijgewerkt en die wordt uitgesloten
     * @return {@code true} als een auteur met een ander id exact deze naam heeft, ongeacht hoofdletters
     */
    boolean existsByNameEqualsIgnoreCaseAndIdNot(String name, Long id);
}
