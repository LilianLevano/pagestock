package com.example.pagestock.catalog.repository;

import com.example.pagestock.catalog.model.Category;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

/**
 * Repository voor {@link Category}-entiteiten.
 * <p>
 * De queries worden door Spring Data JPA afgeleid uit de methodenamen.
 */
public interface CategoryRepository extends ListCrudRepository<Category, Long> {
    /**
     * @return alle categorieën, oplopend gesorteerd op id
     */
    List<Category> findAllByOrderByIdAsc();

    /**
     * @param name tekst die in de naam moet voorkomen
     * @return categorieën waarvan de naam {@code name} bevat, ongeacht hoofdletters
     */
    List<Category> findByNameContainingIgnoreCase(String name);

    /**
     * @param name volledige naam om te controleren
     * @return {@code true} als er een categorie met exact deze naam bestaat, ongeacht hoofdletters
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Controleert of een <em>andere</em> categorie al deze naam heeft; gebruikt bij het bijwerken,
     * zodat de categorie zelf niet als duplicaat telt.
     *
     * @param name volledige naam om te controleren
     * @param id   id van de categorie die wordt bijgewerkt en die wordt uitgesloten
     * @return {@code true} als een categorie met een ander id exact deze naam heeft, ongeacht hoofdletters
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
