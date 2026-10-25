package com.example.pagestock.catalog.repository;

import com.example.pagestock.catalog.model.Book;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

/**
 * Repository voor {@link Book}-entiteiten, met het ISBN-nummer als id.
 * <p>
 * De queries worden door Spring Data JPA afgeleid uit de methodenamen.
 */
public interface BookRepository extends ListCrudRepository<Book, String> {
    /**
     * @param title tekst die in de titel moet voorkomen
     * @return boeken waarvan de titel {@code title} bevat, ongeacht hoofdletters
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * @return alle boeken, alfabetisch gesorteerd op titel
     */
    List<Book> findAllByOrderByTitleAsc();

    /**
     * @param categoryId id van de categorie
     * @return {@code true} als minstens één boek tot deze categorie behoort
     */
    boolean existsByCategoryId(Long categoryId);

    /**
     * @param categoryId id van de categorie
     * @return aantal boeken in deze categorie
     */
    long countByCategoryId(Long categoryId);

    /**
     * Zoekt via de many-to-many relatie {@code authors} (koppeltabel {@code book_authors}).
     *
     * @param authorId id van de auteur
     * @return {@code true} als minstens één boek aan deze auteur gekoppeld is
     */
    boolean existsByAuthorsId(Long authorId);

    /**
     * Telt via de many-to-many relatie {@code authors} (koppeltabel {@code book_authors}).
     *
     * @param authorId id van de auteur
     * @return aantal boeken dat aan deze auteur gekoppeld is
     */
    long countByAuthorsId(Long authorId);
}
